package com.bluehub.app.core.hid

import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import com.bluehub.app.domain.model.GamepadInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class GamepadInputManager @Inject constructor() {

    private val _inputState = MutableStateFlow(GamepadInputState())
    val inputState: StateFlow<GamepadInputState> = _inputState.asStateFlow()

    var deadzone: Float = 0.12f

    fun handleGenericMotionEvent(event: MotionEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK &&
            event.action == MotionEvent.ACTION_MOVE
        ) {
            val startTime = System.currentTimeMillis()

            val lx = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_X))
            val ly = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_Y))
            val rx = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_Z))
            val ry = applyDeadzone(event.getAxisValue(MotionEvent.AXIS_RZ))

            val dpadX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            val dpadY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            val l2 = event.getAxisValue(MotionEvent.AXIS_BRAKE).coerceAtLeast(event.getAxisValue(MotionEvent.AXIS_LTRIGGER))
            val r2 = event.getAxisValue(MotionEvent.AXIS_GAS).coerceAtLeast(event.getAxisValue(MotionEvent.AXIS_RTRIGGER))

            val latency = System.currentTimeMillis() - startTime

            _inputState.update { current ->
                current.copy(
                    leftStickX = lx,
                    leftStickY = ly,
                    rightStickX = rx,
                    rightStickY = ry,
                    dpadLeft = dpadX < -0.5f,
                    dpadRight = dpadX > 0.5f,
                    dpadUp = dpadY < -0.5f,
                    dpadDown = dpadY > 0.5f,
                    triggerL2 = l2,
                    triggerR2 = r2,
                    latencyMs = latency
                )
            }
            return true
        }
        return false
    }

    fun handleKeyEvent(event: KeyEvent): Boolean {
        val isPressed = event.action == KeyEvent.ACTION_DOWN
        val keyCode = event.keyCode
        val startTime = System.currentTimeMillis()

        var handled = true
        _inputState.update { current ->
            val updated = when (keyCode) {
                KeyEvent.KEYCODE_BUTTON_A -> current.copy(buttonA = isPressed)
                KeyEvent.KEYCODE_BUTTON_B -> current.copy(buttonB = isPressed)
                KeyEvent.KEYCODE_BUTTON_X -> current.copy(buttonX = isPressed)
                KeyEvent.KEYCODE_BUTTON_Y -> current.copy(buttonY = isPressed)
                KeyEvent.KEYCODE_BUTTON_L1 -> current.copy(triggerL1 = isPressed)
                KeyEvent.KEYCODE_BUTTON_R1 -> current.copy(triggerR1 = isPressed)
                KeyEvent.KEYCODE_DPAD_UP -> current.copy(dpadUp = isPressed)
                KeyEvent.KEYCODE_DPAD_DOWN -> current.copy(dpadDown = isPressed)
                KeyEvent.KEYCODE_DPAD_LEFT -> current.copy(dpadLeft = isPressed)
                KeyEvent.KEYCODE_DPAD_RIGHT -> current.copy(dpadRight = isPressed)
                else -> {
                    handled = false
                    current
                }
            }
            val latency = System.currentTimeMillis() - startTime
            updated.copy(lastKeyCode = keyCode, latencyMs = latency)
        }
        return handled
    }

    private fun applyDeadzone(value: Float): Float {
        return if (abs(value) < deadzone) 0f else value
    }
}
