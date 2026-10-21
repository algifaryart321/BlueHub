package com.bluehub.app.core.bluetooth.classic

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothClassicManager @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter?
) {
    companion object {
        // Standard Serial Port Profile (SPP) UUID
        val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var socket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var readJob: Job? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    private val _incomingData = MutableSharedFlow<ByteArray>(extraBufferCapacity = 64)
    val incomingData: SharedFlow<ByteArray> = _incomingData.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val errorFlow: SharedFlow<String> = _errorFlow.asSharedFlow()

    @SuppressLint("MissingPermission")
    suspend fun connect(address: String): Boolean = withContext(Dispatchers.IO) {
        disconnect()
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            _errorFlow.emit("Bluetooth tidak aktif")
            return@withContext false
        }

        try {
            val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
            // Cancel discovery to ensure fast and reliable connection
            bluetoothAdapter.cancelDiscovery()

            val sock = device.createRfcommSocketToServiceRecord(SPP_UUID)
            sock.connect()

            socket = sock
            inputStream = sock.inputStream
            outputStream = sock.outputStream
            _isConnected.value = true
            _connectedDeviceName.value = device.name ?: address

            startReading()
            true
        } catch (e: Exception) {
            _errorFlow.emit("Gagal terhubung: ${e.localizedMessage ?: "Unknown error"}")
            disconnect()
            false
        }
    }

    private fun startReading() {
        readJob?.cancel()
        readJob = scope.launch {
            val buffer = ByteArray(1024)
            while (isActive && _isConnected.value) {
                try {
                    val stream = inputStream ?: break
                    val bytesRead = stream.read(buffer)
                    if (bytesRead > 0) {
                        val packet = buffer.copyOf(bytesRead)
                        _incomingData.emit(packet)
                    }
                } catch (e: IOException) {
                    if (isActive) {
                        _errorFlow.emit("Koneksi terputus: ${e.message}")
                        disconnect()
                    }
                    break
                }
            }
        }
    }

    suspend fun sendData(data: ByteArray): Boolean = withContext(Dispatchers.IO) {
        try {
            outputStream?.let { stream ->
                stream.write(data)
                stream.flush()
                return@withContext true
            }
        } catch (e: Exception) {
            _errorFlow.emit("Gagal mengirim data: ${e.localizedMessage}")
        }
        false
    }

    suspend fun sendString(message: String): Boolean {
        return sendData(message.toByteArray(Charsets.UTF_8))
    }

    fun disconnect() {
        readJob?.cancel()
        readJob = null
        try {
            inputStream?.close()
            outputStream?.close()
            socket?.close()
        } catch (_: Exception) {}
        socket = null
        inputStream = null
        outputStream = null
        _isConnected.value = false
        _connectedDeviceName.value = null
    }
}
