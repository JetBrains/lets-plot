/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.Vector
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.input.MouseEventType.*

class MouseInputComponent : EcsComponent {
    var moveEvent: InputMouseEvent? = null
    var pressEvent: InputMouseEvent? = null
    var clickEvent: InputMouseEvent? = null
    var doubleClickEvent: InputMouseEvent? = null
    var dragState: DragState? = null

    fun getEvent(type: MouseEventType): InputMouseEvent? {
        return when (type) {
            PRESS -> pressEvent
            CLICK -> clickEvent
            DOUBLE_CLICK -> doubleClickEvent
        }
    }
}

data class DragState(
    val origin: Vector = Vector.ZERO,
    val location: Vector = Vector.ZERO,
    val started: Boolean = false,
    val dragging: Boolean = false,
    val stopped: Boolean = false,
)
