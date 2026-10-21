package com.bluehub.app.presentation.screens.printer

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bluehub.app.core.printer.EscPosBuilder
import com.bluehub.app.presentation.theme.AlertRed
import com.bluehub.app.presentation.theme.CardBorder
import com.bluehub.app.presentation.theme.DarkBackground
import com.bluehub.app.presentation.theme.DarkSurface
import com.bluehub.app.presentation.theme.DarkSurfaceVariant
import com.bluehub.app.presentation.theme.NeonGreen
import com.bluehub.app.presentation.theme.TechCyan
import com.bluehub.app.presentation.theme.TextMuted
import com.bluehub.app.presentation.theme.TextPrimary
import com.bluehub.app.presentation.theme.TextSecondary

@Composable
fun PrinterScreen(
    viewModel: PrinterViewModel
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()
    val storeName by viewModel.storeName.collectAsState()
    val receiptContent by viewModel.receiptContent.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()
    val paperWidth by viewModel.paperWidth.collectAsState()
    val printStatus by viewModel.printStatus.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Thermal Printer (ESC/POS)",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Editor struk dan pencetakan langsung via Bluetooth SPP",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Paper Width Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = paperWidth == EscPosBuilder.PaperWidth.MM58,
                onClick = { viewModel.setPaperWidth(EscPosBuilder.PaperWidth.MM58) },
                label = { Text("58mm Standard (32 Chars)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TechCyan,
                    selectedLabelColor = DarkBackground
                )
            )
            FilterChip(
                selected = paperWidth == EscPosBuilder.PaperWidth.MM80,
                onClick = { viewModel.setPaperWidth(EscPosBuilder.PaperWidth.MM80) },
                label = { Text("80mm Large (48 Chars)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = TechCyan,
                    selectedLabelColor = DarkBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Receipt Editor Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Konfigurasi Struk",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = storeName,
                    onValueChange = { viewModel.updateStoreName(it) },
                    label = { Text("Nama Toko / Judul") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = receiptContent,
                    onValueChange = { viewModel.updateReceiptContent(it) },
                    label = { Text("Daftar Item / Rincian Belanja") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = totalPrice,
                    onValueChange = { viewModel.updateTotalPrice(it) },
                    label = { Text("Total Harga (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TechCyan,
                        unfocusedBorderColor = CardBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Paper Preview
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFB)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = storeName,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "--------------------------------",
                    color = Color.DarkGray,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = receiptContent,
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "--------------------------------",
                    color = Color.DarkGray,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "TOTAL: Rp $totalPrice",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Print Status message
        printStatus?.let { status ->
            Text(
                text = status,
                color = if (status.contains("berhasil", ignoreCase = true)) NeonGreen else AlertRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.printReceipt() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TechCyan, contentColor = DarkBackground),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cetak Struk", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.printSelfTest() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
            ) {
                Text("Self-Test")
            }
        }
    }
}
