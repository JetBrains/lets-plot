/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.math.rotateAround
import kotlin.math.atan2
import kotlin.math.sign
import kotlin.math.sin

object ArrowSupport {
    const val MIN_TAIL_LENGTH = 10.0
    const val MIN_HEAD_LENGTH = 5.0

    fun createArrowHeads(
        lineString: List<DoubleVector>,
        angle: Double,
        arrowLength: Double,
        onStart: Boolean,
        onEnd: Boolean,
        closed: Boolean,
        minTailLength: Double,
        minHeadLength: Double,
    ): Pair<List<DoubleVector>, List<DoubleVector>> {
        val arrowHelper = ArrowHelper(VectorAdapter.DOUBLE_VECTOR_ADAPTER)
        return arrowHelper.createArrowHeads(
            lineString = lineString,
            angle = angle,
            arrowLength = arrowLength,
            onStart = onStart,
            onEnd = onEnd,
            closed = closed,
            minTailLength = minTailLength,
            minHeadLength = minHeadLength
        )
    }

    fun arrowPadding(
        angle: Double,
        onStart: Boolean,
        onEnd: Boolean,
        atStart: Boolean,
        strokeSize: Double
    ): Double {
        val hasArrow = if (atStart) onStart else onEnd
        if (!hasArrow) return 0.0

        val miterLength = miterLength(angle, strokeSize)
        val miterSign = sign(sin(angle * 2))
        return miterLength * miterSign / 2
    }

    // Compute the length of the miter at the tip of the arrow head
    // Angle is the angle between the two sides of the arrow head in radians
    fun miterLength(angle: Double, strokeSize: Double): Double {
        return strokeSize / sin(angle)
    }

    internal class ArrowHelper<T>(
        private val vec: VectorAdapter<T>,
    ) {
        fun createArrowHeads(
            lineString: List<T>,
            angle: Double,
            arrowLength: Double,
            onStart: Boolean,
            onEnd: Boolean,
            closed: Boolean,
            minTailLength: Double,
            minHeadLength: Double,
        ): Pair<List<T>, List<T>> {
            val startHead = when (onStart) {
                true -> createArrowHeadGeometry(
                    lineString = lineString.asReversed(),
                    angle = angle,
                    arrowLength = arrowLength,
                    onStart = onStart,
                    onEnd = onEnd,
                    closed = closed,
                    minTailLength = minTailLength,
                    minHeadLength = minHeadLength
                )

                false -> emptyList()
            }

            val endHead = when (onEnd) {
                true -> createArrowHeadGeometry(
                    lineString = lineString,
                    angle = angle,
                    arrowLength = arrowLength,
                    onStart = onStart,
                    onEnd = onEnd,
                    closed = closed,
                    minTailLength = minTailLength,
                    minHeadLength = minHeadLength
                )

                false -> emptyList()
            }

            return startHead to endHead
        }


        private fun pointIndexAtDistance(lineString: List<T>, distanceFromEnd: Double): Int {
            var length = 0.0
            var i = lineString.lastIndex

            while (i > 0 && length < distanceFromEnd) {
                val cur = lineString[i]
                val prev = lineString[--i]
                length += distance(cur.x, cur.y, prev.x, prev.y)
            }
            return i
        }

        private fun createArrowHeadGeometry(
            lineString: List<T>,
            angle: Double,
            arrowLength: Double,
            onStart: Boolean,
            onEnd: Boolean,
            closed: Boolean,
            minTailLength: Double,
            minHeadLength: Double
        ): List<T> {
            if (lineString.size < 2) return emptyList()

            val lineLength = lineString.windowed(2).sumOf { (a, b) -> distance(a.x, a.y, b.x, b.y) }
            val headLength = adjustArrowHeadLength(
                lineLength = lineLength,
                onStart = onStart,
                onEnd = onEnd,
                arrowLength = arrowLength,
                minTailLength = minTailLength,
                minHeadLength = minHeadLength
            )

            // basePoint affects direction of the arrow head. Important for curves.
            val basePoint = when (lineString.size) {
                0, 1 -> error("Invalid geometry")
                2 -> lineString.first()
                else -> lineString[pointIndexAtDistance(lineString, distanceFromEnd = headLength)]
            }

            val tipPoint = lineString.last()

            val abscissa = tipPoint.x - basePoint.x
            val ordinate = tipPoint.y - basePoint.y
            if (abscissa == 0.0 && ordinate == 0.0) return emptyList()

            // Compute the angle that the vector defined by this segment makes with the
            // X-axis (radians)
            val polarAngle = atan2(ordinate, abscissa)

            val length = tipPoint - vec.create(headLength, 0)

            val side1 = rotateAround(length.x, length.y, tipPoint.x, tipPoint.y, polarAngle - angle).toVec()
            val side2 = rotateAround(length.x, length.y, tipPoint.x, tipPoint.y, polarAngle + angle).toVec()

            return when (closed) {
                true -> listOf(side1, tipPoint, side2, side1)
                false -> listOf(side1, tipPoint, side2)
            }
        }

        private fun adjustArrowHeadLength(
            lineLength: Double,
            onStart: Boolean,
            onEnd: Boolean,
            arrowLength: Double,
            minTailLength: Double,
            minHeadLength: Double
        ): Double {
            val headsCount = listOf(onStart, onEnd).count { it }
            val headsLength = arrowLength * headsCount
            val tailLength = lineLength - headsLength

            return when (tailLength < minTailLength) {
                true -> maxOf((lineLength - minTailLength) / headsCount, minHeadLength)
                false -> arrowLength
            }
        }

        val T.x get() = vec.x(this)
        val T.y get() = vec.y(this)
        private operator fun T.minus(other: T): T = vec.create(x - other.x, y - other.y)
        private fun DoubleVector.toVec(): T = vec.create(x, y)
    }
}
