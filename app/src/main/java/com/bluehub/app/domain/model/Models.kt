package com.bluehub.app.domain.model

enum class DeviceCategory(
    val displayTitle: String,
    val cardLabel: String
) {
    SMARTHOME(
        displayTitle = "smarthome",
        cardLabel = "Smarthome"
    ),
    KAMERA(
        displayTitle = "Kamera",
        cardLabel = "Kamera"
    ),
    PERIFERAL(
        displayTitle = "periferal",
        cardLabel = "Periferal"
    ),
    IOT(
        displayTitle = "IoT",
        cardLabel = "Perangkat IoT"
    ),
    PRINTER(
        displayTitle = "Printer",
        cardLabel = "Printer"
    ),
    UNKNOWN(
        displayTitle = "Bluetooth",
        cardLabel = "Lainnya"
    );

    companion object {
        val selectableCategories = listOf(SMARTHOME, KAMERA, PERIFERAL, IOT, PRINTER)

        fun fromName(name: String?): DeviceCategory {
            if (name == null) return PERIFERAL
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: PERIFERAL
        }
    }
}

enum class SmartHomeRemoteType(val title: String) {
    TV("Remot TV"),
    DVD("Pemutar DVD"),
    FAN("Remot Kipas"),
    AC("Remot AC"),
    SMARTBOX("Smartbox Remot"),
    PROJECTOR("Remot Proyektor");

    companion object {
        fun detectFromDeviceName(name: String): SmartHomeRemoteType {
            val lower = name.lowercase()
            return when {
                lower.contains("dvd") || lower.contains("player") -> DVD
                lower.contains("kipas") || lower.contains("fan") -> FAN
                lower.contains("ac") || lower.contains("air") -> AC
                lower.contains("box") || lower.contains("stb") || lower.contains("android") -> SMARTBOX
                lower.contains("proyektor") || lower.contains("projector") -> PROJECTOR
                else -> TV
            }
        }
    }
}

enum class ConnectionProtocol {
    BLE,
    CLASSIC_SPP
}

data class BluetoothDeviceDomain(
    val address: String,
    val name: String?,
    val category: DeviceCategory = DeviceCategory.UNKNOWN,
    val protocol: ConnectionProtocol = ConnectionProtocol.BLE,
    val rssi: Int = 0,
    val isConnected: Boolean = false,
    val isBonded: Boolean = false,
    val batteryLevel: Int? = null
)

data class TelemetryPoint(
    val timestamp: Long = System.currentTimeMillis(),
    val label: String,
    val value: Float,
    val unit: String
)

data class GamepadInputState(
    val leftStickX: Float = 0f,
    val leftStickY: Float = 0f,
    val rightStickX: Float = 0f,
    val rightStickY: Float = 0f,
    val buttonA: Boolean = false,
    val buttonB: Boolean = false,
    val buttonX: Boolean = false,
    val buttonY: Boolean = false,
    val dpadUp: Boolean = false,
    val dpadDown: Boolean = false,
    val dpadLeft: Boolean = false,
    val dpadRight: Boolean = false,
    val triggerL1: Boolean = false,
    val triggerR1: Boolean = false,
    val triggerL2: Float = 0f,
    val triggerR2: Float = 0f,
    val lastKeyCode: Int = 0,
    val latencyMs: Long = 0L
)

data class SerialLogEntry(
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean,
    val message: String,
    val isHex: Boolean = false
)

sealed interface BleConnectionState {
    data object Disconnected : BleConnectionState
    data object Connecting : BleConnectionState
    data class Connected(val device: BluetoothDeviceDomain) : BleConnectionState
    data class Error(val message: String) : BleConnectionState
}
