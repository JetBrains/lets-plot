/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.math.distance2
import org.jetbrains.letsPlot.commons.intern.math.pointOnLine

private fun pad(lineString: List<DoubleVector>, padding: Double): Pair<Int, DoubleVector>? {
    if (lineString.size < 2) {
        return null
    }

    val padding2 = padding * padding
    val indexOutsidePadding = lineString.indexOfFirst { distance2(lineString.first(), it) >= padding2 }
    if (indexOutsidePadding < 1) { // not found or first points already satisfy the padding
        return null
    }

    val adjustedStartPoint = run {
        val insidePadding = lineString[indexOutsidePadding - 1]
        val outsidePadding = lineString[indexOutsidePadding]
        val overPadding = distance(lineString.first(), outsidePadding) - padding

        pointOnLine(outsidePadding, insidePadding, overPadding)
    }

    return indexOutsidePadding to adjustedStartPoint
}

private fun padStart(lineString: List<DoubleVector>, padding: Double): List<DoubleVector> {
    val (index, adjustedStartPoint) = pad(lineString, padding) ?: return lineString
    return listOf(adjustedStartPoint) + lineString.subList(index, lineString.size)
}

private fun padEnd(lineString: List<DoubleVector>, padding: Double): List<DoubleVector> {
    val (index, adjustedEndPoint) = pad(lineString.asReversed(), padding) ?: return lineString
    return lineString.subList(0, lineString.size - index) + adjustedEndPoint
}

fun padLineString(
    lineString: List<DoubleVector>,
    startPadding: Double,
    endPadding: Double
): List<DoubleVector> {
    val startPadded = padStart(lineString, startPadding)
    return padEnd(startPadded, endPadding)
}
