package com.bluehub.app.presentation.screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluehub.app.domain.model.SerialLogEntry
import com.bluehub.app.domain.repository.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LineEnding(val symbol: String, val value: String) {
    CRLF("CR+LF", "\r\n"),
    LF("LF", "\n"),
    CR("CR", "\r"),
    NONE("None", "")
}

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val repository: BluetoothRepository
) : ViewModel() {

    val isConnected = repository.isConnected
    val connectedDevice = repository.connectedDevice

    private val _logs = MutableStateFlow<List<SerialLogEntry>>(emptyList())
    val logs: StateFlow<List<SerialLogEntry>> = _logs.asStateFlow()

    private val _inputMessage = MutableStateFlow("")
    val inputMessage: StateFlow<String> = _inputMessage.asStateFlow()

    private val _lineEnding = MutableStateFlow(LineEnding.CRLF)
    val lineEnding: StateFlow<LineEnding> = _lineEnding.asStateFlow()

    private val _isHexMode = MutableStateFlow(false)
    val isHexMode: StateFlow<Boolean> = _isHexMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.incomingSerialStream.collect { msg ->
                val entry = SerialLogEntry(
                    isOutgoing = false,
                    message = msg
                )
                _logs.value = (_logs.value + entry).takeLast(200)
            }
        }
    }

    fun updateInput(text: String) {
        _inputMessage.value = text
    }

    fun setLineEnding(ending: LineEnding) {
        _lineEnding.value = ending
    }

    fun toggleHexMode() {
        _isHexMode.value = !_isHexMode.value
    }

    fun sendCommand(cmd: String = _inputMessage.value) {
        if (cmd.isEmpty()) return
        val fullMessage = cmd + _lineEnding.value.value

        viewModelScope.launch {
            val entry = SerialLogEntry(
                isOutgoing = true,
                message = cmd
            )
            _logs.value = (_logs.value + entry).takeLast(200)
            repository.sendSerialData(fullMessage)
            if (cmd == _inputMessage.value) {
                _inputMessage.value = ""
            }
        }
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }
}
