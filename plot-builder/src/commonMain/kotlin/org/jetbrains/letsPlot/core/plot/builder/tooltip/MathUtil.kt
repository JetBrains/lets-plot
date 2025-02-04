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

    internal fun distance(p1: DoubleVector, p2: DoubleVector): Double {
        return DoubleSegment(p1, p2).length()
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


    class ClosestPointChecker internal constructor(val target: DoubleVector) {
        var distance = -1.0
            private set
        var coord: DoubleVector? = null
            private set

        constructor(x: Double, y: Double) : this(DoubleVector(x, y))

        fun check(coord: DoubleVector): Boolean {
            val cmpResult = compare(coord)
            return cmpResult == COMPARISON_RESULT.NEW_CLOSER || cmpResult == COMPARISON_RESULT.EQUAL
        }

        fun compare(coord: DoubleVector): COMPARISON_RESULT {
            val newDistance = distance(target, coord)
            if (distance < 0) {
                setNewClosestCoord(coord, newDistance)
                return COMPARISON_RESULT.NEW_CLOSER
            }

            if (distance < newDistance) {
                return COMPARISON_RESULT.NEW_FARTHER
            }

            if (distance == newDistance) {
                return COMPARISON_RESULT.EQUAL
            }

            setNewClosestCoord(coord, newDistance)
            return COMPARISON_RESULT.NEW_CLOSER
        }

        private fun setNewClosestCoord(coord: DoubleVector, distance: Double) {
            this.distance = distance
            this.coord = coord
        }

        enum class COMPARISON_RESULT {
            NEW_CLOSER,
            NEW_FARTHER,
            EQUAL
        }
    }
}
