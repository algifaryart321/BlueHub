package com.bluehub.app.presentation.screens.remote

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Input
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Eject
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bluehub.app.domain.model.SmartHomeRemoteType

private val FigmaBlack = Color(0xFF0A0A0A)
private val FigmaDarkPanel = Color(0xFF171717)
private val FigmaBorder = Color(0xFF262626)
private val FigmaPowerGreen = Color(0xFF2ECC71)
private val FigmaAccentBlue = Color(0xFF2575FC)

@Composable
fun SmartHomeRemoteScreen(
    deviceName: String,
    initialRemoteType: SmartHomeRemoteType,
    onBack: () -> Unit,
    onSendCommand: (String) -> Unit
) {
    var currentRemote by remember { mutableStateOf(initialRemoteType) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FigmaBlack)
    ) {
        // Top App Bar matching Figma: Back Arrow + Centered Remote Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentRemote.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = deviceName,
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }

        // Sleek switcher bar so user can switch between the 6 Smart Home remotes in Figma
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmartHomeRemoteType.entries.forEach { type ->
                val selected = type == currentRemote
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) FigmaAccentBlue else Color(0xFF1E1E1E))
                        .clickable { currentRemote = type }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = type.title,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        // Render the selected Figma remote UI
        Box(modifier = Modifier.weight(1f)) {
            when (currentRemote) {
                SmartHomeRemoteType.TV -> TvRemoteLayout(onSendCommand)
                SmartHomeRemoteType.DVD -> DvdRemoteLayout(onSendCommand)
                SmartHomeRemoteType.FAN -> FanRemoteLayout(onSendCommand)
                SmartHomeRemoteType.AC -> AcRemoteLayout(onSendCommand)
                SmartHomeRemoteType.SMARTBOX -> SmartboxRemoteLayout(onSendCommand)
                SmartHomeRemoteType.PROJECTOR -> ProjectorRemoteLayout(onSendCommand)
            }
        }
    }
}

/**
 * 1. REMOT TV (Figma Image 3)
 */
@Composable
private fun TvRemoteLayout(onCommand: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top 2x3 Grid of White Circle Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WhiteCircleIconButton(
                    icon = Icons.Default.PowerSettingsNew,
                    iconTint = FigmaPowerGreen,
                    onClick = { onCommand("TV:POWER") }
                )
                WhiteCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.Input,
                    iconTint = Color.Black,
                    onClick = { onCommand("TV:SOURCE") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.Menu,
                    iconTint = Color.Black,
                    onClick = { onCommand("TV:MENU") }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WhiteCircleIconButton(
                    icon = Icons.Default.Home,
                    iconTint = Color.DarkGray,
                    onClick = { onCommand("TV:HOME") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.Info,
                    iconTint = Color.Black,
                    onClick = { onCommand("TV:INFO") }
                )
                WhiteCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    iconTint = Color.Black,
                    onClick = { onCommand("TV:BACK") }
                )
            }
        }

        // Center Large White D-Pad with Black Center "OK"
        FigmaCircularDPad(
            darkCenter = true,
            onDirection = { dir -> onCommand("TV:$dir") },
            onOk = { onCommand("TV:OK") }
        )

        // Bottom Section: Left (CH Rocker + More) & Right (VOL Rocker + Mute)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column: Channel Up/Down + More (...)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WhiteVerticalRocker(
                    topContent = {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "CH+", tint = Color.Black)
                    },
                    bottomContent = {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "CH-", tint = Color.Black)
                    },
                    onTopClick = { onCommand("TV:CH_UP") },
                    onBottomClick = { onCommand("TV:CH_DOWN") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.MoreHoriz,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("TV:MORE") }
                )
            }

            // Right Column: Volume + / - + Mute
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                WhiteVerticalRocker(
                    topContent = {
                        Text("+", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                    },
                    bottomContent = {
                        Text("−", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Medium)
                    },
                    onTopClick = { onCommand("TV:VOL_UP") },
                    onBottomClick = { onCommand("TV:VOL_DOWN") }
                )
                WhiteCircleIconButton(
                    icon = Icons.AutoMirrored.Filled.VolumeOff,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("TV:MUTE") }
                )
            }
        }
    }
}

/**
 * 2. PEMUTAR DVD (Figma Image 1 - Rightmost screen)
 */
@Composable
private fun DvdRemoteLayout(onCommand: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Row 1: Power (Left) & Eject (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WhiteCircleIconButton(
                    icon = Icons.Default.PowerSettingsNew,
                    iconTint = FigmaPowerGreen,
                    onClick = { onCommand("DVD:POWER") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.Eject,
                    iconTint = Color.Black,
                    onClick = { onCommand("DVD:EJECT") }
                )
            }

            // Row 2: Prev, Rewind, FastForward, Next
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WhiteCircleIconButton(
                    icon = Icons.Default.SkipPrevious,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:PREV") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.FastRewind,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:REW") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.FastForward,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:FF") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.SkipNext,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:NEXT") }
                )
            }

            // Row 3: Stop, Play, Pause
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WhiteCircleIconButton(
                    icon = Icons.Default.Stop,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:STOP") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.PlayArrow,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:PLAY") }
                )
                WhiteCircleIconButton(
                    icon = Icons.Default.Pause,
                    iconTint = Color.Black,
                    size = 56.dp,
                    onClick = { onCommand("DVD:PAUSE") }
                )
            }
        }

        // Bottom Large D-Pad with Light Gray Center "OK"
        Box(modifier = Modifier.padding(bottom = 24.dp)) {
            FigmaCircularDPad(
                darkCenter = false,
                onDirection = { dir -> onCommand("DVD:$dir") },
                onOk = { onCommand("DVD:OK") }
            )
        }
    }
}

/**
 * 3. REMOT KIPAS (Figma Image 4)
 */
@Composable
private fun FanRemoteLayout(onCommand: (String) -> Unit) {
    var isOn by remember { mutableStateOf(true) }
    var speed by remember { mutableIntStateOf(1) }

    val infiniteTransition = rememberInfiniteTransition(label = "fan_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isOn) (1800 / speed) else 100000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "fan_rotation"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Center Fan Blade Graphic matching Figma Image 4
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(
                    modifier = Modifier
                        .size(140.dp)
                        .rotate(if (isOn) angle else 0f)
                ) {
                    val cx = size.width / 2
                    val cy = size.height / 2
                    val bladeLen = size.width * 0.42f

                    // Draw 4 thin cross fan blades like Figma
                    for (i in 0 until 4) {
                        val rad = Math.toRadians((i * 90).toDouble())
                        val endX = cx + (bladeLen * Math.cos(rad)).toFloat()
                        val endY = cy + (bladeLen * Math.sin(rad)).toFloat()
                        drawLine(
                            color = Color.White,
                            start = Offset(cx, cy),
                            end = Offset(endX, endY),
                            strokeWidth = 6f
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 6f,
                            center = Offset(endX, endY)
                        )
                    }
                    drawCircle(color = Color.Gray, radius = 8f, center = Offset(cx, cy))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isOn) "Kecepatan: Level $speed" else "Mati",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        // Bottom 2x3 Dark Grid matching Figma Image 4
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FigmaDarkPanel)
                .border(0.5.dp, FigmaBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        isOn = !isOn
                        onCommand("FAN:POWER")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = FigmaPowerGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("FAN:TIMER") }
                ) {
                    Text("Timer", color = Color.White, fontSize = 14.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        speed = if (speed >= 3) 1 else speed + 1
                        onCommand("FAN:SPEED_$speed")
                    }
                ) {
                    Text("Kecepatan", color = Color.White, fontSize = 14.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
            ) {
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("FAN:MODE") }
                ) {
                    Text("Mode kipas", color = Color.White, fontSize = 14.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("FAN:SWING") }
                ) {
                    Text("Osilasi", color = Color.White, fontSize = 14.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("FAN:MORE") }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.DarkGray
                    )
                }
            }
        }
    }
}

/**
 * 4. REMOT AC (Figma Image 5 - Top screen)
 */
@Composable
private fun AcRemoteLayout(onCommand: (String) -> Unit) {
    var isOn by remember { mutableStateOf(true) }
    var temp by remember { mutableIntStateOf(24) }
    var mode by remember { mutableStateOf("Cool") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Upper Black Display Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (isOn) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$temp°C",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = "Mode: $mode",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Bottom 4-Row Dark Control Grid matching Figma Image 5
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(FigmaDarkPanel)
        ) {
            // Row 1 (2 columns: Power | Mode)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            ) {
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        isOn = !isOn
                        onCommand("AC:POWER")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Power",
                        tint = FigmaPowerGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        mode = when (mode) {
                            "Cool" -> "Dry"
                            "Dry" -> "Fan"
                            else -> "Cool"
                        }
                        onCommand("AC:MODE_$mode")
                    }
                ) {
                    Text("Mode", color = Color.Gray, fontSize = 13.sp)
                }
            }

            // Row 2 (3 columns: Kecepatan | empty | Goyang)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            ) {
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("AC:SPEED") }
                ) {
                    Text("Kecepatan", color = Color.Gray, fontSize = 13.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {}
                ) {}
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("AC:SWING") }
                ) {
                    Text("Goyang", color = Color.Gray, fontSize = 13.sp)
                }
            }

            // Row 3 (3 columns: - | Suhu | +)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            ) {
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (temp > 16) temp--
                        onCommand("AC:TEMP_$temp")
                    }
                ) {
                    Text("−", color = Color.Gray, fontSize = 20.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {}
                ) {
                    Text("Suhu", color = Color.White, fontSize = 13.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (temp < 30) temp++
                        onCommand("AC:TEMP_$temp")
                    }
                ) {
                    Text("+", color = Color.Gray, fontSize = 20.sp)
                }
            }

            // Row 4 (3 columns: Timer | Tidur | ...)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
            ) {
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("AC:TIMER") }
                ) {
                    Text("Timer", color = Color.Gray, fontSize = 13.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("AC:SLEEP") }
                ) {
                    Text("Tidur", color = Color.Gray, fontSize = 13.sp)
                }
                GridCell(
                    modifier = Modifier.weight(1f),
                    onClick = { onCommand("AC:MORE") }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "More",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

/**
 * 5. SMARTBOX REMOT (Figma Image 5 - Middle screen)
 */
@Composable
private fun SmartboxRemoteLayout(onCommand: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row 1: Power, Home, Menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WhiteCircleIconButton(
                icon = Icons.Default.PowerSettingsNew,
                iconTint = FigmaPowerGreen,
                onClick = { onCommand("BOX:POWER") }
            )
            WhiteCircleIconButton(
                icon = Icons.Default.Home,
                iconTint = Color.Black,
                onClick = { onCommand("BOX:HOME") }
            )
            WhiteCircleIconButton(
                icon = Icons.Default.Menu,
                iconTint = Color.Black,
                onClick = { onCommand("BOX:MENU") }
            )
        }

        // Middle: Large White D-Pad with Light Gray Center "OK"
        FigmaCircularDPad(
            darkCenter = false,
            onDirection = { dir -> onCommand("BOX:$dir") },
            onOk = { onCommand("BOX:OK") }
        )

        // Bottom: Split Dark Volume Pill + Wide Dark Return Pill
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DarkSplitVolumeBar(
                onVolDown = { onCommand("BOX:VOL_DOWN") },
                onVolUp = { onCommand("BOX:VOL_UP") }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF050505))
                    .border(1.dp, FigmaBorder, RoundedCornerShape(26.dp))
                    .clickable { onCommand("BOX:BACK") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * 6. REMOT PROYEKTOR (Figma Image 5 - Bottom screen)
 */
@Composable
private fun ProjectorRemoteLayout(onCommand: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Row 1: Power, More (...), Back
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WhiteCircleIconButton(
                icon = Icons.Default.PowerSettingsNew,
                iconTint = FigmaPowerGreen,
                onClick = { onCommand("PROJ:POWER") }
            )
            WhiteCircleIconButton(
                icon = Icons.Default.MoreHoriz,
                iconTint = Color.Black,
                onClick = { onCommand("PROJ:MORE") }
            )
            WhiteCircleIconButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                iconTint = Color.Black,
                onClick = { onCommand("PROJ:BACK") }
            )
        }

        // Middle: Large White D-Pad with Light Gray Center "OK"
        FigmaCircularDPad(
            darkCenter = false,
            onDirection = { dir -> onCommand("PROJ:$dir") },
            onOk = { onCommand("PROJ:OK") }
        )

        // Bottom: Split Dark Volume Pill
        DarkSplitVolumeBar(
            onVolDown = { onCommand("PROJ:VOL_DOWN") },
            onVolUp = { onCommand("PROJ:VOL_UP") }
        )
    }
}

/**
 * Shared Figma Components
 */
@Composable
private fun WhiteCircleIconButton(
    icon: ImageVector,
    iconTint: Color,
    size: Dp = 62.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
private fun WhiteVerticalRocker(
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
    onTopClick: () -> Unit,
    onBottomClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(56.dp)
            .height(132.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onTopClick() },
            contentAlignment = Alignment.Center
        ) {
            topContent()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { onBottomClick() },
            contentAlignment = Alignment.Center
        ) {
            bottomContent()
        }
    }
}

@Composable
private fun FigmaCircularDPad(
    darkCenter: Boolean,
    onDirection: (String) -> Unit,
    onOk: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(230.dp)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Top dot (UP)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(64.dp)
                .clickable { onDirection("UP") },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        // Bottom dot (DOWN)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(64.dp)
                .clickable { onDirection("DOWN") },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        // Left dot (LEFT)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(64.dp)
                .clickable { onDirection("LEFT") },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        // Right dot (RIGHT)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(64.dp)
                .clickable { onDirection("RIGHT") },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }

        // Center OK Button
        Box(
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .background(if (darkCenter) Color(0xFF141414) else Color(0xFFF3F3F3))
                .clickable { onOk() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "OK",
                color = if (darkCenter) Color.White else Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DarkSplitVolumeBar(
    onVolDown: () -> Unit,
    onVolUp: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0xFF050505))
            .border(1.dp, FigmaBorder, RoundedCornerShape(26.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onVolDown() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VolumeDown,
                contentDescription = "Vol-",
                tint = Color.White
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.6f)
                .background(FigmaBorder)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clickable { onVolUp() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Vol+",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun GridCell(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .border(0.5.dp, FigmaBorder)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
