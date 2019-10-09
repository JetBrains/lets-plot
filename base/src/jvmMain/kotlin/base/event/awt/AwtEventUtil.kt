package jetbrains.datalore.base.event.awt

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEvent
import java.awt.event.MouseEvent as AwtMouseEvent

object AwtEventUtil {

    fun translate(e: AwtMouseEvent): MouseEvent {
        return MouseEvent(e.x, e.y, getButton(e), getModifiers(e))
    }

    private fun getButton(e: AwtMouseEvent): Button {
        return when (e.button) {
            AwtMouseEvent.BUTTON1 -> Button.LEFT
            AwtMouseEvent.BUTTON2 -> Button.MIDDLE
            AwtMouseEvent.BUTTON3 -> Button.RIGHT
            else -> Button.NONE
        }
    }

    private fun getModifiers(e: AwtMouseEvent): KeyModifiers {
        return KeyModifiers(e.isControlDown, e.isAltDown, e.isShiftDown, e.isMetaDown)
    }
}
