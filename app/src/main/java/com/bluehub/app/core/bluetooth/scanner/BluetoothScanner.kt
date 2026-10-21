package com.bluehub.app.core.bluetooth.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.domain.model.ConnectionProtocol
import com.bluehub.app.domain.model.DeviceCategory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _scannedDevices = MutableStateFlow<Map<String, BluetoothDeviceDomain>>(emptyMap())
    val scannedDevices: StateFlow<Map<String, BluetoothDeviceDomain>> = _scannedDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private var bleScanCallback: ScanCallback? = null

    private val classicReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND,
                BluetoothDevice.ACTION_NAME_CHANGED -> {
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    val extraName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME)
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()

                    device?.let { dev ->
                        val resolvedName = extraName?.trim()?.takeIf { it.isNotEmpty() }
                            ?: dev.name?.trim()?.takeIf { it.isNotEmpty() }
                            ?: getBondedName(dev.address)

                        val category = detectCategory(dev, resolvedName)
                        val fallbackName = if (!resolvedName.isNullOrBlank()) {
                            resolvedName
                        } else {
                            getDefaultCategoryName(category, dev.address)
                        }

                        val protocol = if (dev.type == BluetoothDevice.DEVICE_TYPE_LE) {
                            ConnectionProtocol.BLE
                        } else {
                            ConnectionProtocol.CLASSIC_SPP
                        }

                        val domainDevice = BluetoothDeviceDomain(
                            address = dev.address,
                            name = fallbackName,
                            category = category,
                            protocol = protocol,
                            rssi = if (rssi != Short.MIN_VALUE.toInt()) rssi else 0,
                            isBonded = dev.bondState == BluetoothDevice.BOND_BONDED
                        )
                        updateDevice(domainDevice)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    // Discovery ended
                }
            }
        }
    }

    private var isReceiverRegistered = false

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return
        _isScanning.value = true

        // 1. Populate already paired/bonded devices first
        bluetoothAdapter.bondedDevices?.forEach { dev ->
            val bondedName = dev.name?.trim()?.takeIf { it.isNotEmpty() }
                ?: getDefaultCategoryName(detectCategory(dev, null), dev.address)
            val protocol = if (dev.type == BluetoothDevice.DEVICE_TYPE_LE) {
                ConnectionProtocol.BLE
            } else {
                ConnectionProtocol.CLASSIC_SPP
            }
            val domain = BluetoothDeviceDomain(
                address = dev.address,
                name = bondedName,
                category = detectCategory(dev, dev.name),
                protocol = protocol,
                rssi = 0,
                isBonded = true
            )
            updateDevice(domain)
        }

        // 2. Start BLE Scan
        val scanner = bluetoothAdapter.bluetoothLeScanner
        if (scanner != null) {
            bleScanCallback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult?) {
                    result?.let { handleScanResult(it) }
                }

                override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                    results?.forEach { handleScanResult(it) }
                }

                override fun onScanFailed(errorCode: Int) {
                    _isScanning.value = false
                }
            }

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

            try {
                scanner.startScan(null, settings, bleScanCallback)
            } catch (_: Exception) {}
        }

        // 3. Start Classic Scan
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothDevice.ACTION_NAME_CHANGED)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            context.registerReceiver(classicReceiver, filter)
            isReceiverRegistered = true
        }
        try {
            bluetoothAdapter.startDiscovery()
        } catch (_: Exception) {}
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (_isScanning.value) {
            _isScanning.value = false
            try {
                bluetoothAdapter?.bluetoothLeScanner?.let { scanner ->
                    bleScanCallback?.let { scanner.stopScan(it) }
                }
            } catch (_: Exception) {}
            bleScanCallback = null

            try {
                if (bluetoothAdapter?.isDiscovering == true) {
                    bluetoothAdapter.cancelDiscovery()
                }
            } catch (_: Exception) {}

            if (isReceiverRegistered) {
                try {
                    context.unregisterReceiver(classicReceiver)
                } catch (_: Exception) {}
                isReceiverRegistered = false
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleScanResult(result: ScanResult) {
        val dev = result.device ?: return

        val rawParsedName = parseDeviceNameFromBytes(result.scanRecord?.bytes)
        val resolvedName = result.scanRecord?.deviceName?.trim()?.takeIf { it.isNotEmpty() }
            ?: dev.name?.trim()?.takeIf { it.isNotEmpty() }
            ?: rawParsedName
            ?: getBondedName(dev.address)

        val category = detectCategory(dev, resolvedName)
        val finalName = if (!resolvedName.isNullOrBlank()) {
            resolvedName
        } else {
            getDefaultCategoryName(category, dev.address)
        }

        val domain = BluetoothDeviceDomain(
            address = dev.address,
            name = finalName,
            category = category,
            protocol = ConnectionProtocol.BLE,
            rssi = result.rssi,
            isBonded = dev.bondState == BluetoothDevice.BOND_BONDED
        )
        updateDevice(domain)
    }

    private fun updateDevice(device: BluetoothDeviceDomain) {
        scope.launch {
            _scannedDevices.value = _scannedDevices.value.toMutableMap().apply {
                val existing = get(device.address)

                val hasRealName = !device.name.isNullOrBlank() &&
                        !device.name.startsWith("BLE Device") &&
                        !device.name.startsWith("Perangkat")

                val finalName = when {
                    hasRealName -> device.name
                    existing != null && !existing.name.isNullOrBlank() &&
                            !existing.name.startsWith("BLE Device") &&
                            !existing.name.startsWith("Perangkat") -> existing.name
                    else -> device.name
                }

                val finalCategory = if (device.category != DeviceCategory.UNKNOWN) {
                    device.category
                } else {
                    existing?.category ?: DeviceCategory.UNKNOWN
                }

                put(device.address, device.copy(name = finalName, category = finalCategory))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getBondedName(address: String): String? {
        return bluetoothAdapter?.bondedDevices?.firstOrNull { it.address.equals(address, ignoreCase = true) }?.name
    }

    private fun getDefaultCategoryName(category: DeviceCategory, address: String): String {
        val suffix = address.takeLast(5)
        return when (category) {
            DeviceCategory.SMARTHOME -> "Smart Home ($suffix)"
            DeviceCategory.KAMERA -> "Kamera ($suffix)"
            DeviceCategory.PERIFERAL -> "Periferal ($suffix)"
            DeviceCategory.IOT -> "Perangkat IoT ($suffix)"
            DeviceCategory.PRINTER -> "Printer ($suffix)"
            DeviceCategory.UNKNOWN -> "Perangkat ($suffix)"
        }
    }

    @SuppressLint("MissingPermission")
    private fun detectCategory(device: BluetoothDevice, candidateName: String?): DeviceCategory {
        val nameToCheck = (candidateName ?: device.name ?: "").lowercase()

        // 1. Check PERIFERAL (Keyboards, Mice, Gamepads, Joysticks, Personal Headsets/Earbuds)
        if (nameToCheck.contains("keyboard") || nameToCheck.contains("keybd") ||
            nameToCheck.contains("kb") || nameToCheck.contains("btk") ||
            nameToCheck.contains("mouse") || nameToCheck.contains("gamepad") ||
            nameToCheck.contains("controller") || nameToCheck.contains("joystick") ||
            nameToCheck.contains("airpods") || nameToCheck.contains("buds") ||
            nameToCheck.contains("headset") || nameToCheck.contains("headphone") ||
            nameToCheck.contains("earphone") || nameToCheck.contains("logitech") ||
            nameToCheck.contains("rexus") || nameToCheck.contains("fantech") ||
            nameToCheck.contains("razer") || nameToCheck.contains("dualshock") ||
            nameToCheck.contains("dualsense") || nameToCheck.contains("xbox")
        ) {
            return DeviceCategory.PERIFERAL
        }

        // 2. Check PRINTER (Thermal Printers & Office Printers)
        if (nameToCheck.contains("print") || nameToCheck.contains("pos") ||
            nameToCheck.contains("rpp") || nameToCheck.contains("mtp") ||
            nameToCheck.contains("eppos") || nameToCheck.contains("xprinter") ||
            nameToCheck.contains("thermal") || nameToCheck.contains("kasir") ||
            nameToCheck.contains("blueprint")
        ) {
            return DeviceCategory.PRINTER
        }

        // 3. Check KAMERA (Cameras, Action Cams, Bluetooth Shutters)
        if (nameToCheck.contains("cam") || nameToCheck.contains("kamera") ||
            nameToCheck.contains("gopro") || nameToCheck.contains("insta360") ||
            nameToCheck.contains("canon") || nameToCheck.contains("nikon") ||
            nameToCheck.contains("fujifilm") || nameToCheck.contains("shutter") ||
            nameToCheck.contains("osmo") || nameToCheck.contains("selfie")
        ) {
            return DeviceCategory.KAMERA
        }

        // 4. Check SMARTHOME (TV, AC, Fan/Kipas, Smartbox, DVD Player, Projector)
        if (nameToCheck.contains("tv") || nameToCheck.contains("bravia") ||
            nameToCheck.contains("proyektor") || nameToCheck.contains("projector") ||
            nameToCheck.contains("smartbox") || nameToCheck.contains("mi box") ||
            nameToCheck.contains("stb") || nameToCheck.contains("dvd") ||
            nameToCheck.contains("kipas") || nameToCheck.contains("fan") ||
            nameToCheck.contains("ac ") || nameToCheck.contains("daikin") ||
            nameToCheck.contains("gree") || nameToCheck.contains("panasonic") ||
            nameToCheck.contains("polytron") || nameToCheck.contains("tcl") ||
            nameToCheck.contains("coocaa") || nameToCheck.contains("hisense") ||
            nameToCheck.contains("bardi") || nameToCheck.contains("tuya")
        ) {
            return DeviceCategory.SMARTHOME
        }

        // 5. Check IOT (Microcontrollers, Sensors, Smart Devices)
        if (nameToCheck.contains("esp") || nameToCheck.contains("arduino") ||
            nameToCheck.contains("hc-0") || nameToCheck.contains("hm-10") ||
            nameToCheck.contains("temp") || nameToCheck.contains("sensor") ||
            nameToCheck.contains("iot") || nameToCheck.contains("obd") ||
            nameToCheck.contains("elm327") || nameToCheck.contains("stella") ||
            nameToCheck.contains("smart") || nameToCheck.contains("band") ||
            nameToCheck.contains("watch") || nameToCheck.contains("scale")
        ) {
            return DeviceCategory.IOT
        }

        // 6. Fallback to BluetoothClass inspection
        val btClass = device.bluetoothClass ?: return DeviceCategory.UNKNOWN
        val majorClass = btClass.majorDeviceClass
        val deviceClass = btClass.deviceClass

        return when (majorClass) {
            BluetoothClass.Device.Major.PERIPHERAL -> DeviceCategory.PERIFERAL
            BluetoothClass.Device.Major.IMAGING -> DeviceCategory.PRINTER
            BluetoothClass.Device.Major.HEALTH,
            BluetoothClass.Device.Major.WEARABLE -> DeviceCategory.IOT
            BluetoothClass.Device.Major.AUDIO_VIDEO -> {
                when (deviceClass) {
                    BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER -> DeviceCategory.KAMERA
                    BluetoothClass.Device.AUDIO_VIDEO_VIDEO_DISPLAY_AND_LOUDSPEAKER,
                    BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX,
                    BluetoothClass.Device.AUDIO_VIDEO_VCR,
                    BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO -> DeviceCategory.SMARTHOME
                    else -> DeviceCategory.PERIFERAL // Headsets, Earbuds, Speakers
                }
            }
            else -> DeviceCategory.UNKNOWN
        }
    }

    private fun parseDeviceNameFromBytes(bytes: ByteArray?): String? {
        if (bytes == null) return null
        var index = 0
        while (index < bytes.size) {
            val length = bytes[index++].toInt() and 0xFF
            if (length == 0 || index + length > bytes.size) break
            val type = bytes[index++].toInt() and 0xFF
            if (type == 0x09 || type == 0x08) {
                val nameLength = length - 1
                if (nameLength > 0 && index + nameLength <= bytes.size) {
                    val nameBytes = bytes.copyOfRange(index, index + nameLength)
                    val parsed = String(nameBytes, Charsets.UTF_8).trim()
                    if (parsed.isNotEmpty()) return parsed
                }
            }
            index += length - 1
        }
        return null
    }
}
