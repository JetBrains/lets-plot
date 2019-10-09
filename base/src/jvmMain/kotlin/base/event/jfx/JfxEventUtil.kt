package jetbrains.datalore.base.event.jfx

import javafx.scene.input.MouseButton
import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEvent
import kotlin.math.roundToInt
import javafx.scene.input.MouseEvent as JfxMouseEvent

object JfxEventUtil {
    fun translate(e: JfxMouseEvent): MouseEvent {
        return MouseEvent(e.x.roundToInt(), e.y.roundToInt(), getButton(e), getModifiers(e))
    }

    private fun getModifiers(e: JfxMouseEvent): KeyModifiers {
        return KeyModifiers(e.isControlDown, e.isAltDown, e.isShiftDown, e.isMetaDown)
    }

    private fun getButton(e: JfxMouseEvent): Button {
        return when (e.button) {
            MouseButton.PRIMARY -> Button.LEFT
            MouseButton.MIDDLE -> Button.MIDDLE
            MouseButton.SECONDARY -> Button.RIGHT
            else -> Button.NONE
        }
    }
}