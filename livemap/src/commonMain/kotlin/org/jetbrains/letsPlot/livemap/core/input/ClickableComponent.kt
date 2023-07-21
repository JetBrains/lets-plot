/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.input

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.graphics.RenderBox
import org.jetbrains.letsPlot.livemap.core.util.Geometries

class ClickableComponent(private val myRenderBox: RenderBox) : EcsComponent {
    val rect
        get() = DoubleRectangle(myRenderBox.origin, myRenderBox.dimension)

    val origin get() = myRenderBox.origin
    val dimension get() = myRenderBox.dimension
}

fun Vector.inside(box: ClickableComponent) = Geometries.inside(x, y, box.origin, box.dimension)
