/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.event.KeyModifiers.Companion.emptyModifiers
import org.jetbrains.letsPlot.commons.geometry.Vector


open class MouseEvent(x: Int, y: Int, val button: Button, val modifiers: KeyModifiers) : PointEvent(x, y) {
    var preventDefault: Boolean = false

    constructor(v: Vector, button: Button, modifiers: KeyModifiers) : this(v.x, v.y, button, modifiers)

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
}
