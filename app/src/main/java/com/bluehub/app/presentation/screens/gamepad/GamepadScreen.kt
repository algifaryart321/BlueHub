package com.bluehub.app.presentation.screens.gamepad

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluehub.app.presentation.theme.AlertRed
import com.bluehub.app.presentation.theme.CardBorder
import com.bluehub.app.presentation.theme.DarkBackground
import com.bluehub.app.presentation.theme.DarkSurface
import com.bluehub.app.presentation.theme.DarkSurfaceVariant
import com.bluehub.app.presentation.theme.NeonGreen
import com.bluehub.app.presentation.theme.TechBlue
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary
import com.bluehub.app.presentation.theme.WarningOrange

@Composable
fun GamepadScreen(
    viewModel: GamepadViewModel
) {
    val state by viewModel.inputState.collectAsState()
    val deadzone by viewModel.deadzone.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Gamepad & HID Controller",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pengujian visual input tombol, analog stick, dan pemantauan latensi",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Latency and KeyCode Monitor Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Input Latency", color = TextMuted, fontSize = 11.sp)
                    Text(
                        text = "${state.latencyMs} ms",
                        color = if (state.latencyMs < 20) NeonGreen else WarningOrange,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Last KeyCode", color = TextMuted, fontSize = 11.sp)
                    Text(
                        text = if (state.lastKeyCode != 0) "${state.lastKeyCode}" else "--",
                        color = TechCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Controller Visual Tester Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shoulder & Triggers (L1/L2 and R1/R2)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        TriggerIndicator(label = "L2", value = state.triggerL2)
                        Spacer(modifier = Modifier.height(6.dp))
                        ButtonIndicator(label = "L1", isPressed = state.triggerL1)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        TriggerIndicator(label = "R2", value = state.triggerR2)
                        Spacer(modifier = Modifier.height(6.dp))
                        ButtonIndicator(label = "R1", isPressed = state.triggerR1)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Mid section: D-Pad, Sticks, and Action Buttons (ABXY)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // D-Pad
                    DPadView(
                        up = state.dpadUp,
                        down = state.dpadDown,
                        left = state.dpadLeft,
                        right = state.dpadRight
                    )

                    // Analog Sticks (Left and Right)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            AnalogStickView(label = "LS", x = state.leftStickX, y = state.leftStickY)
                            AnalogStickView(label = "RS", x = state.rightStickX, y = state.rightStickY)
                        }
                    }

                    // Action Buttons (A, B, X, Y)
                    ActionButtonsView(
                        a = state.buttonA,
                        b = state.buttonB,
                        x = state.buttonX,
                        y = state.buttonY
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Deadzone Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Deadzone Sensitivitas", color = TextPrimary, fontSize = 14.sp)
                    Text("${(deadzone * 100).toInt()}%", color = TechCyan, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = deadzone,
                    onValueChange = { viewModel.updateDeadzone(it) },
                    valueRange = 0.02f..0.30f,
                    colors = SliderDefaults.colors(thumbColor = TechCyan, activeTrackColor = TechCyan)
                )
            }
        }
    }
}

@Composable
fun AnalogStickView(label: String, x: Float, y: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant)
                .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = (size.width / 2) - 14.dp.toPx()

                // Draw center crosshair
                drawLine(Color.Gray.copy(alpha = 0.3f), Offset(center.x, 0f), Offset(center.x, size.height), 1f)
                drawLine(Color.Gray.copy(alpha = 0.3f), Offset(0f, center.y), Offset(size.width, center.y), 1f)

                // Current stick displacement
                val stickPos = Offset(
                    center.x + (x * maxRadius),
                    center.y + (y * maxRadius)
                )

                drawCircle(
                    color = TechCyan,
                    radius = 12.dp.toPx(),
                    center = stickPos
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$label (${String.format("%.1f", x)}, ${String.format("%.1f", y)})",
            color = TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ButtonIndicator(label: String, isPressed: Boolean, activeColor: Color = TechCyan) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPressed) activeColor else DarkSurfaceVariant)
            .border(1.dp, if (isPressed) activeColor else CardBorder, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPressed) DarkBackground else TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    }
}

@Composable
fun TriggerIndicator(label: String, value: Float) {
    Column {
        Text("$label: ${(value * 100).toInt()}%", color = TextMuted, fontSize = 10.sp)
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DarkSurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = value.coerceIn(0f, 1f))
                    .height(10.dp)
                    .background(TechCyan)
            )
        }
    }
}

@Composable
fun DPadView(up: Boolean, down: Boolean, left: Boolean, right: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ButtonIndicator("▲", up)
        Row {
            ButtonIndicator("◀", left)
            Spacer(modifier = Modifier.width(36.dp))
            ButtonIndicator("▶", right)
        }
        ButtonIndicator("▼", down)
    }
}

@Composable
fun ActionButtonsView(a: Boolean, b: Boolean, x: Boolean, y: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ButtonIndicator("Y", y, WarningOrange)
        Row {
            ButtonIndicator("X", x, TechBlue)
            Spacer(modifier = Modifier.width(36.dp))
            ButtonIndicator("B", b, AlertRed)
        }
        ButtonIndicator("A", a, NeonGreen)
    }
}
