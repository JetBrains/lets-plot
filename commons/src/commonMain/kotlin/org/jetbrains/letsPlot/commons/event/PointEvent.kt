/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.geometry.Vector

open class PointEvent(val x: Int, val y: Int) : Event() {

    val location: Vector
        get() = Vector(x, y)

    open fun at(location: Vector): PointEvent {
        return at(location.x, location.y)
    }

    open fun at(x: Int, y: Int): PointEvent {
        if (this.x == x && this.y == y) {
            return this
        }

        return PointEvent(x, y)
    }

    override fun toString(): String {
        return "{x=$x,y=$y}"
    }
}
