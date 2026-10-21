package com.bluehub.app.presentation.screens.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TerminalScreen(
    viewModel: TerminalViewModel
) {
    val logs by viewModel.logs.collectAsState()
    val inputMessage by viewModel.inputMessage.collectAsState()
    val lineEnding by viewModel.lineEnding.collectAsState()
    val isHexMode by viewModel.isHexMode.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new log
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    val quickMacros = listOf("AT", "AT+VERSION", "PING", "STATUS", "RESET")
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Serial & BLE Terminal",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Komunikasi raw data ASCII / HEX dengan mikrocontroller",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = { viewModel.clearLogs() }) {
                Icon(
                    imageVector = Icons.Default.ClearAll,
                    contentDescription = "Clear",
                    tint = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Options: Line Ending & Format
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(LineEnding.entries) { ending ->
                    FilterChip(
                        selected = lineEnding == ending,
                        onClick = { viewModel.setLineEnding(ending) },
                        label = { Text(ending.symbol, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TechCyan,
                            selectedLabelColor = DarkBackground
                        )
                    )
                }
            }

            FilterChip(
                selected = isHexMode,
                onClick = { viewModel.toggleHexMode() },
                label = { Text(if (isHexMode) "HEX" else "ASCII", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonGreen,
                    selectedLabelColor = DarkBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Console Log Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF090A0E))
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (logs.isEmpty()) {
                Text(
                    text = "Belum ada transmisi data serial...",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { entry ->
                        val timeStr = timeFormat.format(Date(entry.timestamp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "[$timeStr] ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (entry.isOutgoing) "TX > " else "RX < ",
                                color = if (entry.isOutgoing) TechCyan else NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = entry.message,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Macros Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(quickMacros) { macro ->
                FilterChip(
                    selected = false,
                    onClick = { viewModel.sendCommand(macro) },
                    label = { Text(macro, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = DarkSurfaceVariant,
                        labelColor = TechCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Box + Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { viewModel.updateInput(it) },
                placeholder = { Text("Ketik perintah...", fontSize = 13.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = TechCyan,
                    unfocusedBorderColor = CardBorder
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { viewModel.sendCommand() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechCyan,
                    contentColor = DarkBackground
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
