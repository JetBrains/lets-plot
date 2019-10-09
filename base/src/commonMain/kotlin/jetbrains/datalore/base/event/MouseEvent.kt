package jetbrains.datalore.base.event

import jetbrains.datalore.base.event.KeyModifiers.Companion.emptyModifiers
import jetbrains.datalore.base.geometry.Vector


class MouseEvent(x: Int, y: Int, val button: Button?, val modifiers: KeyModifiers) : PointEvent(x, y) {

    companion object {

        fun noButton(v: Vector): MouseEvent {
            return MouseEvent(v, Button.NONE, emptyModifiers())
        }

        fun leftButton(v: Vector): MouseEvent {
            return MouseEvent(v, Button.LEFT, emptyModifiers())
        }

        fun middleButton(v: Vector): MouseEvent {
            return MouseEvent(v, Button.MIDDLE, emptyModifiers())
        }

        fun rightButton(v: Vector): MouseEvent {
            return MouseEvent(v, Button.RIGHT, emptyModifiers())
        }
    }

    init {
        requireNotNull(button) { "Null button" }
    }

    constructor(v: Vector, button: Button, modifiers: KeyModifiers) : this(v.x, v.y, button, modifiers)
}
