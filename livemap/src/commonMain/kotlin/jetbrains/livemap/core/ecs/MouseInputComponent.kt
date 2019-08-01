package jetbrains.livemap.core.ecs

import jetbrains.datalore.base.geometry.Vector
import jetbrains.livemap.core.input.InputMouseEvent
import jetbrains.livemap.core.input.MouseEventType
import jetbrains.livemap.core.input.MouseEventType.*

class MouseInputComponent : EcsComponent {
    var location: Vector? = null
    var dragDistance: Vector? = null
    var press: InputMouseEvent? = null
    var click: InputMouseEvent? = null
    var doubleClick: InputMouseEvent? = null

    fun getEvent(type: MouseEventType): InputMouseEvent? {
        return when (type) {
            PRESS -> press
            CLICK -> click
            DOUBLE_CLICK -> doubleClick
        }
    }
}