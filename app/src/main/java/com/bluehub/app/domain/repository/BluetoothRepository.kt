package com.bluehub.app.domain.repository

import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.domain.model.TelemetryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    val scannedDevices: StateFlow<Map<String, BluetoothDeviceDomain>>
    val savedDevices: Flow<List<BluetoothDeviceDomain>>
    val connectedAddresses: StateFlow<Set<String>>
    val isScanning: StateFlow<Boolean>
    val isConnected: StateFlow<Boolean>
    val connectedDevice: StateFlow<BluetoothDeviceDomain?>
    val telemetryStream: Flow<TelemetryPoint>
    val incomingSerialStream: Flow<String>

    fun startScanning()
    fun stopScanning()
    suspend fun connect(device: BluetoothDeviceDomain): Boolean
    fun disconnect(address: String? = null)
    suspend fun removeSavedDevice(address: String)
    suspend fun sendSerialData(message: String): Boolean
    suspend fun sendRawBytes(bytes: ByteArray): Boolean
}
