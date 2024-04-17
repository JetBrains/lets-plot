/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine
import org.jetbrains.letsPlot.commons.intern.util.VectorAdapter.Companion.DOUBLE_VECTOR_ADAPTER

fun padLineString(lineString: List<DoubleVector>, startPadding: Double, endPadding: Double): List<DoubleVector> {
    return PaddingHelper(DOUBLE_VECTOR_ADAPTER).padLineString(lineString, startPadding, endPadding)
}

internal class PaddingHelper<T>(
    private val adapter: VectorAdapter<T>,
) {
    private fun pad(lineString: List<T>, padding: Double): Pair<Int, T>? {
        if (lineString.size < 2) {
            return null
        }

        val start = lineString.first()
        val padding2 = padding * padding
        val indexOutsidePadding = lineString.indexOfFirst {
            distance2(start.x, start.y, it.x, it.y) >= padding2
        }
        if (indexOutsidePadding < 1) { // not found or first points already satisfy the padding
            return null
        }

        val adjustedStartPoint = run {
            val insidePadding = lineString[indexOutsidePadding - 1]
            val outsidePadding = lineString[indexOutsidePadding]
            val overPadding = distance(start.x, start.y, outsidePadding.x, outsidePadding.y) - padding

            pointOnLine(outsidePadding.x, outsidePadding.y, insidePadding.x, insidePadding.y, overPadding)
                .let { (x, y) -> adapter.create(x, y) }
        }

        return indexOutsidePadding to adjustedStartPoint
    }

    private fun padStart(lineString: List<T>, padding: Double): List<T> {
        val (index, adjustedStartPoint) = pad(lineString, padding) ?: return lineString
        return listOf(adjustedStartPoint) + lineString.subList(index, lineString.size)
    }

    private fun padEnd(lineString: List<T>, padding: Double): List<T> {
        val (index, adjustedEndPoint) = pad(lineString.asReversed(), padding) ?: return lineString
        return lineString.subList(0, lineString.size - index) + adjustedEndPoint
    }

    fun padLineString(lineString: List<T>, startPadding: Double, endPadding: Double): List<T> {
        val startPadded = padStart(lineString, startPadding)
        return padEnd(startPadded, endPadding)
    }

    val T.x get() = adapter.x(this)
    val T.y get() = adapter.y(this)
}
