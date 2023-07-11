/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.event

import org.jetbrains.letsPlot.commons.geometry.Vector

open class PointEvent(val x: Int, val y: Int) : Event() {

    val location: Vector
        get() = Vector(x, y)

    override fun toString(): String {
        return "{x=$x,y=$y}"
    }
}
