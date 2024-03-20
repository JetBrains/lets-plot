/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import kotlin.math.atan2
import kotlin.math.sin

/**
 * @param angle  The angle of the arrow head in radians (smaller numbers produce narrower, pointier arrows).
 * Essentially describes the width of the arrow head.
 * @param length The length of the arrow head (px).
 */
class ArrowSpec(
    val angle: Double,
    val length: Double,
    val end: End,
    val type: Type,
    val minTailLength: Double
) {

    val isOnFirstEnd: Boolean
        get() = end == End.FIRST || end == End.BOTH

    val isOnLastEnd: Boolean
        get() = end == End.LAST || end == End.BOTH

    enum class End {
        LAST, FIRST, BOTH
    }

    enum class Type {
        OPEN, CLOSED
    }

    companion object {

        fun createArrowHeads(
            geometry: List<DoubleVector>,
            arrowSpec: ArrowSpec
        ): Pair<List<DoubleVector>, List<DoubleVector>> {
            val startHead = when (arrowSpec.isOnFirstEnd) {
                true -> createArrowHeadGeometry(arrowSpec, geometry.asReversed())
                false -> emptyList()
            }

            val endHead = when (arrowSpec.isOnLastEnd) {
                true -> createArrowHeadGeometry(arrowSpec, geometry)
                false -> emptyList()
            }

            return startHead to endHead
        }

        private fun pointIndexAtDistance(curve: List<DoubleVector>, distanceFromEnd: Double): Int {
            var length = 0.0
            var i = curve.lastIndex

            while (i > 0 && length < distanceFromEnd) {
                val cur = curve[i]
                val prev = curve[--i]
                length += distance(cur, prev)
            }
            return i
        }

        private fun createArrowHeadGeometry(
            arrowSpec: ArrowSpec,
            geometry: List<DoubleVector>
        ): List<DoubleVector> {
            if (geometry.size < 2) return emptyList()

            val lineLength = geometry.windowed(2).sumOf { (a, b) -> distance(a, b) }
            val headLength = adjustArrowHeadLength(lineLength, arrowSpec)

            // basePoint affects direction of the arrow head. Important for curves.
            val basePoint = when (geometry.size) {
                0, 1 -> error("Invalid geometry")
                2 -> geometry.first()
                else -> geometry[pointIndexAtDistance(geometry, distanceFromEnd = headLength)]
            }

            val tipPoint = geometry.last()

            val abscissa = tipPoint.x - basePoint.x
            val ordinate = tipPoint.y - basePoint.y
            if (abscissa == 0.0 && ordinate == 0.0) return emptyList()

            // Compute the angle that the vector defined by this segment makes with the
            // X-axis (radians)
            val polarAngle = atan2(ordinate, abscissa)

            val length = tipPoint.subtract(DoubleVector(headLength, 0))

            val leftSide = length.rotateAround(tipPoint, polarAngle - arrowSpec.angle)
            val rightSide = length.rotateAround(tipPoint, polarAngle + arrowSpec.angle)

            return when (arrowSpec.type) {
                Type.CLOSED -> listOf(leftSide, tipPoint, rightSide, leftSide)
                Type.OPEN -> listOf(leftSide, tipPoint, rightSide)
            }
        }

        fun adjustArrowHeadLength(lineLength: Double, arrowSpec: ArrowSpec): Double {
            val headsCount = listOf(arrowSpec.isOnFirstEnd, arrowSpec.isOnLastEnd).count { it }
            val headsLength = arrowSpec.length * headsCount
            val tailLength = lineLength - headsLength

            return when (tailLength < arrowSpec.minTailLength) {
                true -> maxOf((lineLength - arrowSpec.minTailLength) / headsCount, 5.0) // 5.0 so the arrow head never disappears
                false -> arrowSpec.length
            }
        }

        internal fun ArrowSpec.toArrowAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {
                private val filled = (type == Type.CLOSED)

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.FILL -> if (filled) super.get(Aes.COLOR) else Color.TRANSPARENT
                        Aes.LINETYPE -> NamedLineType.SOLID // avoid ugly patterns if linetype is other than 'solid'
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }

        fun miterLength(
            arrowSpec: ArrowSpec,
            p: DataPointAesthetics,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
        ): Double {
            return strokeScaler(p) / sin(arrowSpec.angle)
        }
    }
}
