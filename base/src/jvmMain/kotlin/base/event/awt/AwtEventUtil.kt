package jetbrains.datalore.base.event.awt

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.base.event.MouseEvent

object AwtEventUtil {

    fun translate(e: java.awt.event.MouseEvent): MouseEvent {
        return MouseEvent(e.x, e.y, getButton(e), getModifiers(e))
    }

    private fun getButton(e: java.awt.event.MouseEvent): Button {
        when (e.button) {
            java.awt.event.MouseEvent.BUTTON1 -> return Button.LEFT
            java.awt.event.MouseEvent.BUTTON2 -> return Button.MIDDLE
            java.awt.event.MouseEvent.BUTTON3 -> return Button.RIGHT
            else -> return Button.NONE
        }
    }

    private fun getModifiers(e: java.awt.event.MouseEvent): KeyModifiers {
        val controlDown = e.isControlDown
        val altDown = e.isAltDown
        val shiftDown = e.isShiftDown
        val metaDown = e.isMetaDown
        return KeyModifiers(controlDown, altDown, shiftDown, metaDown)
    }
}
