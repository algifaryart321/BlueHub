package com.bluehub.app.presentation.screens.printer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluehub.app.core.printer.EscPosBuilder
import com.bluehub.app.domain.usecase.ConnectDeviceUseCase
import com.bluehub.app.domain.usecase.PrintThermalDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrinterViewModel @Inject constructor(
    private val printThermalDataUseCase: PrintThermalDataUseCase,
    private val connectDeviceUseCase: ConnectDeviceUseCase
) : ViewModel() {

    val isConnected = connectDeviceUseCase.isConnected
    val connectedDevice = connectDeviceUseCase.connectedDevice

    private val _storeName = MutableStateFlow("BLUEHUB SMART STORE")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _receiptContent = MutableStateFlow(
        "Kopi Susu Gula Aren     x1   18.000\nRoti Bakar Coklat Keju  x1   15.000\nEs Teh Manis Jumbo      x1    6.000"
    )
    val receiptContent: StateFlow<String> = _receiptContent.asStateFlow()

    private val _totalPrice = MutableStateFlow("39.000")
    val totalPrice: StateFlow<String> = _totalPrice.asStateFlow()

    private val _paperWidth = MutableStateFlow(EscPosBuilder.PaperWidth.MM58)
    val paperWidth: StateFlow<EscPosBuilder.PaperWidth> = _paperWidth.asStateFlow()

    private val _printStatus = MutableStateFlow<String?>(null)
    val printStatus: StateFlow<String?> = _printStatus.asStateFlow()

    fun updateStoreName(name: String) { _storeName.value = name }
    fun updateReceiptContent(content: String) { _receiptContent.value = content }
    fun updateTotalPrice(total: String) { _totalPrice.value = total }
    fun setPaperWidth(width: EscPosBuilder.PaperWidth) { _paperWidth.value = width }

    fun printReceipt() {
        viewModelScope.launch {
            _printStatus.value = "Mencetak struk..."
            val bytes = EscPosBuilder()
                .reset()
                .setAlignment(EscPosBuilder.Alignment.CENTER)
                .setTextSize(doubleWidth = true, doubleHeight = true)
                .setBold(true)
                .textLine(_storeName.value)
                .setTextSize(doubleWidth = false, doubleHeight = false)
                .setBold(false)
                .textLine("Jl. Teknologi No. 42, Jakarta")
                .textLine("Telp: 0812-3456-7890")
                .printDivider('-', _paperWidth.value)
                .setAlignment(EscPosBuilder.Alignment.LEFT)
                .textLine(_receiptContent.value)
                .printDivider('-', _paperWidth.value)
                .setBold(true)
                .printKeyValue("TOTAL:", "Rp ${_totalPrice.value}", _paperWidth.value)
                .setBold(false)
                .printDivider('=', _paperWidth.value)
                .setAlignment(EscPosBuilder.Alignment.CENTER)
                .textLine("Terima Kasih Atas Kunjungan Anda")
                .textLine("Barang yang dibeli tidak dapat ditukar")
                .feedAndCut()
                .build()

            val success = printThermalDataUseCase(bytes)
            _printStatus.value = if (success) "Struk berhasil dicetak!" else "Gagal mencetak. Cek koneksi printer."
        }
    }

    fun printSelfTest() {
        viewModelScope.launch {
            _printStatus.value = "Mencetak Self-Test..."
            val bytes = EscPosBuilder()
                .reset()
                .setAlignment(EscPosBuilder.Alignment.CENTER)
                .setBold(true)
                .textLine("*** BLUEHUB SELF TEST ***")
                .setBold(false)
                .textLine("Lebar: ${_paperWidth.value.name}")
                .textLine("Karakter per baris: ${_paperWidth.value.charsPerLine}")
                .printDivider('*', _paperWidth.value)
                .textLine("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
                .textLine("abcdefghijklmnopqrstuvwxyz")
                .textLine("0123456789 !@#$%^&*()")
                .printDivider('*', _paperWidth.value)
                .feedAndCut()
                .build()

            val success = printThermalDataUseCase(bytes)
            _printStatus.value = if (success) "Self-Test berhasil!" else "Gagal mengirim data."
        }
    }
}
