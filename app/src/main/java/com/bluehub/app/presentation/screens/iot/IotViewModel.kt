package com.bluehub.app.presentation.screens.iot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluehub.app.domain.model.TelemetryPoint
import com.bluehub.app.domain.repository.BluetoothRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActuatorState(
    val relay1: Boolean = false,
    val relay2: Boolean = false,
    val ledBrightness: Float = 0.5f
)

data class AutomationRule(
    val isEnabled: Boolean = false,
    val thresholdValue: Float = 35.0f,
    val commandToTrigger: String = "RELAY1:OFF"
)

@HiltViewModel
class IotViewModel @Inject constructor(
    private val repository: BluetoothRepository
) : ViewModel() {

    val isConnected = repository.isConnected

    private val _temperatureHistory = MutableStateFlow<List<TelemetryPoint>>(emptyList())
    val temperatureHistory: StateFlow<List<TelemetryPoint>> = _temperatureHistory.asStateFlow()

    private val _actuators = MutableStateFlow(ActuatorState())
    val actuators: StateFlow<ActuatorState> = _actuators.asStateFlow()

    private val _automationRule = MutableStateFlow(AutomationRule())
    val automationRule: StateFlow<AutomationRule> = _automationRule.asStateFlow()

    init {
        viewModelScope.launch {
            repository.telemetryStream.collect { point ->
                // Add to history and keep last 30 points
                val updated = (_temperatureHistory.value + point).takeLast(30)
                _temperatureHistory.value = updated

                // Check rule engine
                val rule = _automationRule.value
                if (rule.isEnabled && point.value > rule.thresholdValue) {
                    repository.sendSerialData(rule.commandToTrigger)
                }
            }
        }
    }

    fun toggleRelay1() {
        val newState = !_actuators.value.relay1
        _actuators.value = _actuators.value.copy(relay1 = newState)
        viewModelScope.launch {
            repository.sendSerialData(if (newState) "RELAY1:ON\n" else "RELAY1:OFF\n")
        }
    }

    fun toggleRelay2() {
        val newState = !_actuators.value.relay2
        _actuators.value = _actuators.value.copy(relay2 = newState)
        viewModelScope.launch {
            repository.sendSerialData(if (newState) "RELAY2:ON\n" else "RELAY2:OFF\n")
        }
    }

    fun setLedBrightness(value: Float) {
        _actuators.value = _actuators.value.copy(ledBrightness = value)
        val pwm = (value * 255).toInt()
        viewModelScope.launch {
            repository.sendSerialData("LED:$pwm\n")
        }
    }

    fun updateRule(enabled: Boolean, threshold: Float, command: String) {
        _automationRule.value = AutomationRule(
            isEnabled = enabled,
            thresholdValue = threshold,
            commandToTrigger = command
        )
    }
}
