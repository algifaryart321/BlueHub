package com.bluehub.app.presentation.screens.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.domain.model.DeviceCategory
import com.bluehub.app.domain.usecase.ConnectDeviceUseCase
import com.bluehub.app.domain.usecase.ScanDevicesUseCase
import com.bluehub.app.domain.usecase.SendSerialCommandUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val scanDevicesUseCase: ScanDevicesUseCase,
    private val connectDeviceUseCase: ConnectDeviceUseCase,
    private val sendSerialCommandUseCase: SendSerialCommandUseCase
) : ViewModel() {

    val isScanning = scanDevicesUseCase.isScanning
    val isConnected = connectDeviceUseCase.isConnected
    val connectedDevice = connectDeviceUseCase.connectedDevice
    val connectedAddresses = connectDeviceUseCase.connectedAddresses

    private val _selectedCategory = MutableStateFlow<DeviceCategory?>(null)
    val selectedCategory: StateFlow<DeviceCategory?> = _selectedCategory.asStateFlow()

    private val _hideUnnamed = MutableStateFlow(true)
    val hideUnnamed: StateFlow<Boolean> = _hideUnnamed.asStateFlow()

    private val _connectingAddress = MutableStateFlow<String?>(null)
    val connectingAddress: StateFlow<String?> = _connectingAddress.asStateFlow()

    private val _statusMessage = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val statusMessage: SharedFlow<String> = _statusMessage.asSharedFlow()

    // Saved devices ("Perangkat Yang Pernah Terhubung")
    val savedDevices: StateFlow<List<BluetoothDeviceDomain>> = combine(
        scanDevicesUseCase.savedDevices,
        connectedAddresses
    ) { saved, connectedSet ->
        saved.map { it.copy(isConnected = connectedSet.contains(it.address)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Scanned devices for the category scanner screen
    val filteredDevices: StateFlow<List<BluetoothDeviceDomain>> = combine(
        scanDevicesUseCase.scannedDevices,
        _selectedCategory,
        _hideUnnamed,
        connectedAddresses
    ) { devicesMap, category, hideUnnamed, connectedSet ->
        var list = devicesMap.values.toList().map { dev ->
            dev.copy(isConnected = connectedSet.contains(dev.address))
        }

        if (hideUnnamed) {
            list = list.filter { dev ->
                val name = dev.name ?: ""
                !name.startsWith("Perangkat (") &&
                        !name.startsWith("BLE Device") &&
                        !name.startsWith("Smart Home (") &&
                        !name.startsWith("Kamera (") &&
                        !name.startsWith("Periferal (") &&
                        !name.startsWith("Perangkat IoT (") &&
                        !name.startsWith("Printer (") &&
                        name.isNotBlank()
            }
        }

        // Strictly filter so ONLY devices matching the selected category appear!
        if (category != null) {
            list = list.filter { it.category == category }
        }

        // Sort: connected first, then strongest signal (closest to phone) first
        list.sortedWith(
            compareByDescending<BluetoothDeviceDomain> { it.isConnected }
                .thenByDescending { it.rssi }
                .thenByDescending { it.isBonded }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startScan() {
        scanDevicesUseCase.startScan()
    }

    fun stopScan() {
        scanDevicesUseCase.stopScan()
    }

    fun toggleScan() {
        if (isScanning.value) {
            scanDevicesUseCase.stopScan()
        } else {
            scanDevicesUseCase.startScan()
        }
    }

    fun selectCategory(category: DeviceCategory?) {
        _selectedCategory.value = category
    }

    fun toggleHideUnnamed() {
        _hideUnnamed.value = !_hideUnnamed.value
    }

    fun connectDevice(
        device: BluetoothDeviceDomain,
        categoryOverride: DeviceCategory? = null,
        onConnected: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _connectingAddress.value = device.address
            val targetDevice = if (categoryOverride != null) {
                device.copy(category = categoryOverride)
            } else {
                device
            }
            val success = connectDeviceUseCase(targetDevice)
            _connectingAddress.value = null

            if (success) {
                _statusMessage.emit("Berhasil terhubung ke ${device.name ?: "perangkat"}")
                onConnected()
            } else {
                _statusMessage.emit("Gagal terhubung ke ${device.name}. Pastikan perangkat aktif & dalam jangkauan.")
            }
        }
    }

    fun disconnect(address: String? = null) {
        connectDeviceUseCase.disconnect(address)
    }

    fun deleteSavedDevice(device: BluetoothDeviceDomain) {
        viewModelScope.launch {
            connectDeviceUseCase.removeSavedDevice(device.address)
            _statusMessage.emit("${device.name ?: "Perangkat"} dihapus dari daftar")
        }
    }

    fun sendRemoteCommand(command: String) {
        viewModelScope.launch {
            sendSerialCommandUseCase("$command\n")
        }
    }
}
