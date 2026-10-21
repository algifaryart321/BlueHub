package com.bluehub.app.core.bluetooth.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import com.bluehub.app.domain.model.TelemetryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlueHubBleManager @Inject constructor(
    @ApplicationContext context: Context
) : BleManager(context) {

    private val scope = CoroutineScope(Dispatchers.Default)

    // Standard UUIDs
    companion object {
        val BATTERY_SERVICE_UUID: UUID = UUID.fromString("0000180F-0000-1000-8000-00805F9B34FB")
        val BATTERY_LEVEL_CHAR_UUID: UUID = UUID.fromString("00002A19-0000-1000-8000-00805F9B34FB")

        val HEART_RATE_SERVICE_UUID: UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")
        val HEART_RATE_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB")

        val HEALTH_THERMOMETER_SERVICE_UUID: UUID = UUID.fromString("00001809-0000-1000-8000-00805F9B34FB")
        val TEMPERATURE_MEASUREMENT_CHAR_UUID: UUID = UUID.fromString("00002A1C-0000-1000-8000-00805F9B34FB")

        // Nordic UART Service (NUS) for custom IoT/microcontroller communication
        val NUS_SERVICE_UUID: UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val NUS_RX_CHAR_UUID: UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E") // Write
        val NUS_TX_CHAR_UUID: UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E") // Notify
    }

    private var batteryLevelCharacteristic: BluetoothGattCharacteristic? = null
    private var heartRateCharacteristic: BluetoothGattCharacteristic? = null
    private var temperatureCharacteristic: BluetoothGattCharacteristic? = null
    private var nusRxCharacteristic: BluetoothGattCharacteristic? = null
    private var nusTxCharacteristic: BluetoothGattCharacteristic? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel: StateFlow<Int?> = _batteryLevel.asStateFlow()

    private val _telemetryStream = MutableSharedFlow<TelemetryPoint>(extraBufferCapacity = 64)
    val telemetryStream: SharedFlow<TelemetryPoint> = _telemetryStream.asSharedFlow()

    private val _incomingSerialData = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val incomingSerialData: SharedFlow<String> = _incomingSerialData.asSharedFlow()

    private val _discoveredServices = MutableStateFlow<List<BluetoothGattService>>(emptyList())
    val discoveredServices: StateFlow<List<BluetoothGattService>> = _discoveredServices.asStateFlow()

    override fun getGattCallback(): BleManagerGattCallback = object : BleManagerGattCallback() {
        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            _discoveredServices.value = gatt.services

            val batteryService = gatt.getService(BATTERY_SERVICE_UUID)
            if (batteryService != null) {
                batteryLevelCharacteristic = batteryService.getCharacteristic(BATTERY_LEVEL_CHAR_UUID)
            }

            val hrService = gatt.getService(HEART_RATE_SERVICE_UUID)
            if (hrService != null) {
                heartRateCharacteristic = hrService.getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)
            }

            val tempService = gatt.getService(HEALTH_THERMOMETER_SERVICE_UUID)
            if (tempService != null) {
                temperatureCharacteristic = tempService.getCharacteristic(TEMPERATURE_MEASUREMENT_CHAR_UUID)
            }

            val nusService = gatt.getService(NUS_SERVICE_UUID)
            if (nusService != null) {
                nusRxCharacteristic = nusService.getCharacteristic(NUS_RX_CHAR_UUID)
                nusTxCharacteristic = nusService.getCharacteristic(NUS_TX_CHAR_UUID)
            }

            return true
        }

        override fun initialize() {
            setNotificationCallback(batteryLevelCharacteristic).with { _, data ->
                val level = data.getIntValue(Data.FORMAT_UINT8, 0)
                level?.let { _batteryLevel.value = it }
            }
            readCharacteristic(batteryLevelCharacteristic).enqueue()
            enableNotifications(batteryLevelCharacteristic).enqueue()

            // Heart Rate
            setNotificationCallback(heartRateCharacteristic).with { _, data ->
                val hrValue = data.getIntValue(Data.FORMAT_UINT8, 1) ?: 0
                scope.launch {
                    _telemetryStream.emit(
                        TelemetryPoint(label = "Heart Rate", value = hrValue.toFloat(), unit = "BPM")
                    )
                }
            }
            enableNotifications(heartRateCharacteristic).enqueue()

            // Temperature
            setNotificationCallback(temperatureCharacteristic).with { _, data ->
                val rawTemp = data.getIntValue(Data.FORMAT_UINT16, 1) ?: 0
                val tempC = rawTemp / 10.0f
                scope.launch {
                    _telemetryStream.emit(
                        TelemetryPoint(label = "Temperature", value = tempC, unit = "°C")
                    )
                }
            }
            enableNotifications(temperatureCharacteristic).enqueue()

            // NUS Serial
            setNotificationCallback(nusTxCharacteristic).with { _, data ->
                val text = data.getStringValue(0) ?: ""
                scope.launch {
                    _incomingSerialData.emit(text)
                    // Check if it's formatted telemetry like: "TEMP:24.5"
                    parseCustomTelemetry(text)
                }
            }
            enableNotifications(nusTxCharacteristic).enqueue()

            _isConnected.value = true
        }

        override fun onServicesInvalidated() {
            batteryLevelCharacteristic = null
            heartRateCharacteristic = null
            temperatureCharacteristic = null
            nusRxCharacteristic = null
            nusTxCharacteristic = null
            _discoveredServices.value = emptyList()
            _isConnected.value = false
        }
    }

    private fun parseCustomTelemetry(message: String) {
        val parts = message.trim().split(":", "=")
        if (parts.size == 2) {
            val key = parts[0].trim()
            val numValue = parts[1].trim().filter { it.isDigit() || it == '.' || it == '-' }.toFloatOrNull()
            if (numValue != null) {
                scope.launch {
                    _telemetryStream.emit(
                        TelemetryPoint(label = key, value = numValue, unit = "")
                    )
                }
            }
        }
    }

    fun sendNusData(message: String) {
        nusRxCharacteristic?.let { char ->
            writeCharacteristic(char, message.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                .enqueue()
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: BluetoothDevice) {
        connect(device)
            .retry(3, 100)
            .useAutoConnect(false)
            .timeout(10000)
            .enqueue()
    }

    fun disconnectDevice() {
        disconnect().enqueue()
    }
}
