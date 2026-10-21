package com.bluehub.app.presentation.screens.iot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluehub.app.presentation.components.TelemetryChart
import com.bluehub.app.presentation.theme.DarkBackground
import com.bluehub.app.presentation.theme.DarkSurface
import com.bluehub.app.presentation.theme.NeonGreen
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary
import com.bluehub.app.presentation.theme.WarningOrange

@Composable
fun IotDashboardScreen(
    viewModel: IotViewModel
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val history by viewModel.temperatureHistory.collectAsState()
    val actuators by viewModel.actuators.collectAsState()
    val rule by viewModel.automationRule.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "IoT & Sensor Telemetry",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pemantauan telemetri real-time dan kontrol aktuator",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Real-time Chart
        TelemetryChart(
            title = "Sensor Trend (Live)",
            points = history,
            unit = history.lastOrNull()?.unit ?: "°C",
            lineColor = TechCyan
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Actuators Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kontrol Aktuator & Sakelar",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Relay 1 Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = null,
                            tint = if (actuators.relay1) NeonGreen else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Relay 1 (Aktuator Utama)", color = TextPrimary, fontSize = 14.sp)
                    }
                    Switch(
                        checked = actuators.relay1,
                        onCheckedChange = { viewModel.toggleRelay1() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonGreen,
                            checkedTrackColor = NeonGreen.copy(alpha = 0.5f)
                        )
                    )
                }

                // Relay 2 Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = null,
                            tint = if (actuators.relay2) NeonGreen else TextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Relay 2 (Pompa/Kipas)", color = TextPrimary, fontSize = 14.sp)
                    }
                    Switch(
                        checked = actuators.relay2,
                        onCheckedChange = { viewModel.toggleRelay2() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonGreen,
                            checkedTrackColor = NeonGreen.copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // LED Dimmer Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = TechCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "LED Dimmer (PWM)", color = TextPrimary, fontSize = 14.sp)
                        }
                        Text(
                            text = "${(actuators.ledBrightness * 100).toInt()}%",
                            color = TechCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = actuators.ledBrightness,
                        onValueChange = { viewModel.setLedBrightness(it) },
                        colors = SliderDefaults.colors(
                            thumbColor = TechCyan,
                            activeTrackColor = TechCyan
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rule Engine Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Rule,
                            contentDescription = null,
                            tint = TechCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Rule Engine Automasi",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Switch(
                        checked = rule.isEnabled,
                        onCheckedChange = {
                            viewModel.updateRule(it, rule.thresholdValue, rule.commandToTrigger)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TechCyan,
                            checkedTrackColor = TechCyan.copy(alpha = 0.5f)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Jika nilai sensor > ${rule.thresholdValue} -> Kirim perintah: '${rule.commandToTrigger}'",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
