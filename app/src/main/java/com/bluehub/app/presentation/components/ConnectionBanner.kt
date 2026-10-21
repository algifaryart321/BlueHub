package com.bluehub.app.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluehub.app.domain.model.BluetoothDeviceDomain
import com.bluehub.app.presentation.theme.AlertRed
import com.bluehub.app.presentation.theme.DarkSurfaceVariant
import com.bluehub.app.presentation.theme.NeonGreen
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary

@Composable
fun ConnectionBanner(
    connectedDevice: BluetoothDeviceDomain?,
    onDisconnect: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurfaceVariant,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status indicator dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (connectedDevice != null) NeonGreen else TextMuted)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = connectedDevice?.name ?: "Tidak ada perangkat terhubung",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (connectedDevice != null) {
                            "${connectedDevice.protocol.name} • ${connectedDevice.address}"
                        } else "Buka tab 'Devices' untuk menghubungkan",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (connectedDevice != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    connectedDevice.batteryLevel?.let { battery ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = "Battery",
                                tint = NeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$battery%",
                                color = TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDisconnect,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Disconnect",
                            tint = AlertRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
