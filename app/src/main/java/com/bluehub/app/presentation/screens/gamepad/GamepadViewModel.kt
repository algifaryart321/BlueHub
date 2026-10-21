package com.bluehub.app.presentation.screens.gamepad

import androidx.lifecycle.ViewModel
import com.bluehub.app.core.hid.GamepadInputManager
import com.bluehub.app.domain.model.GamepadInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GamepadViewModel @Inject constructor(
    private val gamepadInputManager: GamepadInputManager
) : ViewModel() {

    val inputState: StateFlow<GamepadInputState> = gamepadInputManager.inputState

    private val _deadzone = MutableStateFlow(gamepadInputManager.deadzone)
    val deadzone: StateFlow<Float> = _deadzone.asStateFlow()

    fun updateDeadzone(value: Float) {
        _deadzone.value = value
        gamepadInputManager.deadzone = value
    }
}
