package com.bluehub.app.data.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.bluehub.app.core.bluetooth.ble.BlueHubBleManager
import com.bluehub.app.core.bluetooth.classic.BluetoothClassicManager
import com.bluehub.app.core.bluetooth.scanner.BluetoothScanner
import com.bluehub.app.data.local.db.DeviceDao
import com.bluehub.app.data.local.db.DeviceEntity
import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.domain.model.ConnectionProtocol
import com.bluehub.app.domain.model.DeviceCategory
import com.bluehub.app.domain.model.TelemetryPoint
import com.bluehub.app.domain.repository.BluetoothRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BluetoothRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanner: BluetoothScanner,
    private val bleManager: BlueHubBleManager,
    private val classicManager: BluetoothClassicManager,
    private val deviceDao: DeviceDao,
    private val bluetoothAdapter: BluetoothAdapter?
) : BluetoothRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    override val scannedDevices: StateFlow<Map<String, BluetoothDeviceDomain>> = scanner.scannedDevices
    override val isScanning: StateFlow<Boolean> = scanner.isScanning

    private val _connectedAddresses = MutableStateFlow<Set<String>>(emptySet())
    override val connectedAddresses: StateFlow<Set<String>> = _connectedAddresses.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothDeviceDomain?>(null)
    override val connectedDevice: StateFlow<BluetoothDeviceDomain?> = _connectedDevice.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    override val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    override val savedDevices: Flow<List<BluetoothDeviceDomain>> =
        deviceDao.getAllDevices().map { entities ->
            entities.map { entity ->
                BluetoothDeviceDomain(
                    address = entity.address,
                    name = entity.name,
                    category = DeviceCategory.fromName(entity.category),
                    protocol = try {
                        ConnectionProtocol.valueOf(entity.protocol)
                    } catch (_: Exception) {
                        ConnectionProtocol.CLASSIC_SPP
                    },
                    isConnected = _connectedAddresses.value.contains(entity.address),
                    isBonded = true
                )
            }
        }

    private val _incomingSerialMerged = MutableSharedFlow<String>(extraBufferCapacity = 64)
    override val incomingSerialStream: Flow<String> = _incomingSerialMerged.asSharedFlow()

    override val telemetryStream: Flow<TelemetryPoint> = bleManager.telemetryStream

    private val aclReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }
            val address = device?.address ?: return

            when (intent?.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    _connectedAddresses.value = _connectedAddresses.value + address
                    _isConnected.value = true
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    _connectedAddresses.value = _connectedAddresses.value - address
                    _isConnected.value = _connectedAddresses.value.isNotEmpty()
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE)
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        // Once bonded, trigger profile & BLE connection immediately
                        connectSystemProfiles(device)
                        if (device.type == BluetoothDevice.DEVICE_TYPE_LE || device.type == BluetoothDevice.DEVICE_TYPE_DUAL) {
                            bleManager.connectToDevice(device)
                        }
                    }
                }
            }
        }
    }

    init {
        // Register receiver for real hardware ACL connection/disconnection events
        try {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            }
            context.registerReceiver(aclReceiver, filter)
        } catch (_: Exception) {}

        // Sync already-connected OS Bluetooth devices on startup
        syncConnectedDevicesWithOs()

        scope.launch {
            bleManager.incomingSerialData.collect {
                _incomingSerialMerged.emit(it)
            }
        }
        scope.launch {
            classicManager.incomingData.collect { bytes ->
                val str = String(bytes, Charsets.UTF_8)
                _incomingSerialMerged.emit(str)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun syncConnectedDevicesWithOs() {
        scope.launch {
            try {
                val activeSet = mutableSetOf<String>()
                bluetoothAdapter?.bondedDevices?.forEach { dev ->
                    if (isDeviceConnectedAtOsLevel(dev)) {
                        activeSet.add(dev.address)
                    }
                }
                _connectedAddresses.value = activeSet
                _isConnected.value = activeSet.isNotEmpty()
            } catch (_: Exception) {}
        }
    }

    override fun startScanning() {
        syncConnectedDevicesWithOs()
        scanner.startScan()
    }

    override fun stopScanning() {
        scanner.stopScan()
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(device: BluetoothDeviceDomain): Boolean = withContext(Dispatchers.IO) {
        stopScanning()
        delay(200) // Allow discovery to stop cleanly before opening connection

        val btDevice = bluetoothAdapter?.getRemoteDevice(device.address) ?: return@withContext false

        // 1. If not yet paired/bonded, initiate bonding and wait for user confirmation
        if (btDevice.bondState != BluetoothDevice.BOND_BONDED) {
            try {
                btDevice.createBond()
            } catch (_: Exception) {}

            // Wait up to 12 seconds for bonding to finish
            var waitedMs = 0
            while (btDevice.bondState == BluetoothDevice.BOND_BONDING || btDevice.bondState == BluetoothDevice.BOND_NONE) {
                delay(400)
                waitedMs += 400
                if (btDevice.bondState == BluetoothDevice.BOND_BONDED) break
                if (waitedMs >= 12000) break
            }
        }

        // 2. Connect Android OS profiles (HID_HOST = 4 for keyboards/gamepads, A2DP = 2, HEADSET = 1)
        connectSystemProfiles(btDevice)

        // 3. Connect BLE GATT if device supports LE (crucial for BLE 5.0 keyboards like BT5.0 KB!)
        val isBleCapable = btDevice.type == BluetoothDevice.DEVICE_TYPE_LE ||
                btDevice.type == BluetoothDevice.DEVICE_TYPE_DUAL ||
                device.protocol == ConnectionProtocol.BLE

        if (isBleCapable) {
            try {
                bleManager.connectToDevice(btDevice)
            } catch (_: Exception) {}
        }

        // 4. Connect Classic RFCOMM SPP if applicable (Printers, IoT microcontrollers, etc.)
        if (!isBleCapable || device.category == DeviceCategory.PRINTER || device.category == DeviceCategory.IOT) {
            scope.launch {
                try {
                    classicManager.connect(device.address)
                } catch (_: Exception) {}
            }
        }

        // 5. Wait up to 3.5 seconds to verify real hardware ACL connection
        var actuallyConnected = false
        for (i in 1..7) {
            if (isDeviceConnectedAtOsLevel(btDevice) || bleManager.isConnected.value || classicManager.isConnected.value) {
                actuallyConnected = true
                break
            }
            delay(500)
        }

        if (actuallyConnected || device.category == DeviceCategory.SMARTHOME) {
            // Save to "Perangkat Yang Pernah Terhubung"
            deviceDao.insertDevice(
                DeviceEntity(
                    address = device.address,
                    name = device.name ?: btDevice.name ?: "Nama Perangkat",
                    category = device.category.name,
                    protocol = if (isBleCapable) ConnectionProtocol.BLE.name else device.protocol.name,
                    lastConnected = System.currentTimeMillis()
                )
            )
            _connectedAddresses.value = _connectedAddresses.value + device.address
            _connectedDevice.value = device.copy(isConnected = true)
            _isConnected.value = true
            return@withContext true
        }

        return@withContext false
    }

    /**
     * Connects Android system Bluetooth profiles:
     * - Profile 4: HID_HOST (Keyboards, Mice, Gamepads, Shutter remotes)
     * - Profile 2: A2DP (Speakers, TVs, AirPods, Audio)
     * - Profile 1: HEADSET (Handsfree / Earphones)
     */
    @SuppressLint("MissingPermission", "DiscouragedPrivateApi")
    private fun connectSystemProfiles(btDevice: BluetoothDevice) {
        val profiles = listOf(4, BluetoothProfile.A2DP, BluetoothProfile.HEADSET)
        for (profileId in profiles) {
            try {
                bluetoothAdapter?.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                        try {
                            val connectMethod = proxy.javaClass.getMethod("connect", BluetoothDevice::class.java)
                            connectMethod.isAccessible = true
                            connectMethod.invoke(proxy, btDevice)
                        } catch (_: Exception) {
                            try {
                                val declaredMethod = proxy.javaClass.getDeclaredMethod("connect", BluetoothDevice::class.java)
                                declaredMethod.isAccessible = true
                                declaredMethod.invoke(proxy, btDevice)
                            } catch (_: Exception) {}
                        } finally {
                            bluetoothAdapter.closeProfileProxy(profile, proxy)
                        }
                    }

                    override fun onServiceDisconnected(profile: Int) {}
                }, profileId)
            } catch (_: Exception) {}
        }
    }

    /**
     * Checks whether the BluetoothDevice currently has an active ACL radio link in Android OS.
     */
    private fun isDeviceConnectedAtOsLevel(device: BluetoothDevice): Boolean {
        return try {
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as? Boolean ?: false
        } catch (_: Exception) {
            false
        }
    }

    override fun disconnect(address: String?) {
        if (address != null) {
            _connectedAddresses.value = _connectedAddresses.value - address
            if (_connectedDevice.value?.address == address) {
                _connectedDevice.value = null
                _isConnected.value = _connectedAddresses.value.isNotEmpty()
            }
            bleManager.disconnectDevice()
            classicManager.disconnect()
        } else {
            _connectedAddresses.value = emptySet()
            _isConnected.value = false
            _connectedDevice.value = null
            bleManager.disconnectDevice()
            classicManager.disconnect()
        }
    }

    override suspend fun removeSavedDevice(address: String) = withContext(Dispatchers.IO) {
        disconnect(address)
        deviceDao.deleteDevice(address)
    }

    override suspend fun sendSerialData(message: String): Boolean {
        val current = _connectedDevice.value ?: return false
        return when (current.protocol) {
            ConnectionProtocol.BLE -> {
                bleManager.sendNusData(message)
                true
            }
            ConnectionProtocol.CLASSIC_SPP -> {
                classicManager.sendString(message)
            }
        }
    }

    override suspend fun sendRawBytes(bytes: ByteArray): Boolean {
        val current = _connectedDevice.value ?: return false
        return when (current.protocol) {
            ConnectionProtocol.BLE -> {
                bleManager.sendNusData(String(bytes))
                true
            }
            ConnectionProtocol.CLASSIC_SPP -> {
                classicManager.sendData(bytes)
            }
        }
    }
}
