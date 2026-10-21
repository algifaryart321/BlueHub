package com.bluehub.app.domain.usecase

import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.domain.repository.BluetoothRepository
import javax.inject.Inject

class ScanDevicesUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    val scannedDevices = repository.scannedDevices
    val savedDevices = repository.savedDevices
    val isScanning = repository.isScanning

    fun startScan() = repository.startScanning()
    fun stopScan() = repository.stopScanning()
}

class ConnectDeviceUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    val isConnected = repository.isConnected
    val connectedDevice = repository.connectedDevice
    val connectedAddresses = repository.connectedAddresses

    suspend operator fun invoke(device: BluetoothDeviceDomain): Boolean {
        return repository.connect(device)
    }

    fun disconnect(address: String? = null) {
        repository.disconnect(address)
    }

    suspend fun removeSavedDevice(address: String) {
        repository.removeSavedDevice(address)
    }
}

class SendSerialCommandUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    val incomingStream = repository.incomingSerialStream

    suspend operator fun invoke(message: String): Boolean {
        return repository.sendSerialData(message)
    }
}

class PrintThermalDataUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    suspend operator fun invoke(data: ByteArray): Boolean {
        return repository.sendRawBytes(data)
    }
}
