/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object MathUtil {

    internal fun polygonContainsCoordinate(points: List<DoubleVector>, coord: DoubleVector): Boolean {
        var intersectionCount = 0

        for (i in 1 until points.size) {
            val start = points[i - 1]
            val end = points[i]

            if (start.y >= coord.y && end.y >= coord.y || start.y < coord.y && end.y < coord.y) {
                continue
            }

            val x = start.x + (coord.y - start.y) * (end.x - start.x) / (end.y - start.y)

            if (x <= coord.x) {
                intersectionCount++
            }
        }

        return intersectionCount % 2 != 0
    }

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
            return compare(coord) == COMPARISON_RESULT.NEW_CLOSER
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

    class DoubleRange private constructor(private val myStart: Double, private val myLength: Double) {

        init {
            if (myLength < 0) {
                throw IllegalStateException("Length should be positive")
            }
        }

        fun length(): Double {
            return myLength
        }

        fun overlaps(v: DoubleRange): Boolean {
            return start() <= v.end() && v.start() <= end()
        }

        fun inside(v: DoubleRange): Boolean {
            return start() >= v.start() && end() <= v.end()
        }

        operator fun contains(value: Double): Boolean {
            return value >= start() && value <= end()
        }

        fun start(): Double {
            return myStart
        }

        fun end(): Double {
            return myStart + length()
        }

        fun move(delta: Double): DoubleRange {
            return withStartAndLength(
                start() + delta,
                length()
            )
        }

        fun moveLeft(delta: Double): DoubleRange {
            if (delta < 0) {
                throw IllegalStateException("Value should be positive")
            }

            return withStartAndLength(
                start() - delta,
                length()
            )
        }

        fun moveRight(delta: Double): DoubleRange {
            if (delta < 0) {
                throw IllegalStateException("Value should be positive")
            }

            return withStartAndLength(
                start() + delta,
                length()
            )
        }

        companion object {
            fun withStartAndEnd(start: Double, end: Double): DoubleRange {
                val rangeStart = min(start, end)
                val rangeLength = max(start, end) - rangeStart
                return DoubleRange(rangeStart, rangeLength)
            }

            fun withStartAndLength(start: Double, length: Double): DoubleRange {
                return DoubleRange(start, length)
            }
        }

    }
}
