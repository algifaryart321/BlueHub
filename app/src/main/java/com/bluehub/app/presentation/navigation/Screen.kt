package com.bluehub.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Devices : Screen("devices", "Devices", Icons.Default.Bluetooth)
    data object Iot : Screen("iot", "IoT Hub", Icons.Default.Sensors)
    data object Printer : Screen("printer", "Printer", Icons.Default.Print)
    data object Gamepad : Screen("gamepad", "Gamepad", Icons.Default.Gamepad)
    data object Terminal : Screen("terminal", "Terminal", Icons.Default.Code)

    companion object {
        val items = listOf(Devices, Iot, Printer, Gamepad, Terminal)
    }
}
