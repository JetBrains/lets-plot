package jetbrains.datalore.visualization.base.svgMapper.dom.domUtil

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import org.w3c.dom.events.MouseEvent

object DomEventUtil {
    fun getButton(e: MouseEvent): Button {
        return when(e.button.toInt()) {
            DomMouseButtons.BUTTON_LEFT -> Button.LEFT
            DomMouseButtons.BUTTON_MIDDLE -> Button.MIDDLE
            DomMouseButtons.BUTTON_RIGHT -> Button.RIGHT
            else -> Button.NONE
        }
    }

    fun getModifiers(e: MouseEvent) = KeyModifiers(e.ctrlKey, e.altKey, e.shiftKey, e.metaKey)
}