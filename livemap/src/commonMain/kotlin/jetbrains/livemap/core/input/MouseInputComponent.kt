/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.Vector
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.input.MouseEventType.*

class MouseInputComponent : EcsComponent {
    var location: Vector? = null
    var dragDistance: Vector? = null
    var press: InputMouseEvent? = null
    var click: InputMouseEvent? = null
    var doubleClick: InputMouseEvent? = null
    var over: InputMouseEvent? = null
    var out: InputMouseEvent? = null

    var isOver: Boolean = false

    fun getEvent(type: MouseEventType): InputMouseEvent? {
        return when (type) {
            PRESS -> press
            CLICK -> click
            DOUBLE_CLICK -> doubleClick
            OVER -> over
            OUT -> out
        }
    }
}