/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.abs

object MathUtil {

    fun liesOnSegment(p1: DoubleVector, p2: DoubleVector, c: DoubleVector, epsilon: Double): Boolean {
        return DoubleSegment(p1, p2).distance(c) < epsilon
    }

    internal fun areEqual(p1: DoubleVector, p2: DoubleVector, epsilon: Double): Boolean {
        return p1.subtract(p2).length() < epsilon
    }

    internal fun areEqual(a: Double, b: Double, epsilon: Double): Boolean {
        return abs(a - b) < epsilon
    }

    internal fun subtractX(v: DoubleVector, x: Double): DoubleVector {
        return DoubleVector(v.x - x, v.y)
    }

    internal fun addX(v: DoubleVector, x: Double): DoubleVector {
        return DoubleVector(v.x + x, v.y)
    }

    fun leftEdgeOf(rect: DoubleRectangle): DoubleSegment {
        return DoubleSegment(
            DoubleVector(rect.left, rect.top),
            DoubleVector(rect.left, rect.bottom)
        )
    }

    fun topEdgeOf(rect: DoubleRectangle): DoubleSegment {
        return DoubleSegment(
            DoubleVector(rect.left, rect.top),
            DoubleVector(rect.right, rect.top)
        )
    }

    fun rightEdgeOf(rect: DoubleRectangle): DoubleSegment {
        return DoubleSegment(
            DoubleVector(rect.right, rect.top),
            DoubleVector(rect.right, rect.bottom)
        )
    }

    fun bottomEdgeOf(rect: DoubleRectangle): DoubleSegment {
        return DoubleSegment(
            DoubleVector(rect.left, rect.bottom),
            DoubleVector(rect.right, rect.bottom)
        )
    }
}
