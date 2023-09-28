/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.matchingIndices
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.pickAtIndices
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent

object AxisUtil {
    fun breaksData(
        scaleBreaks: ScaleBreaks,
        coord: CoordinateSystem,
        domain: DoubleRectangle,
        flipAxis: Boolean,
        horizontal: Boolean
    ): AxisComponent.BreaksData {
        fun toClient(v: DoubleVector): DoubleVector? = finiteOrNull(coord.toClient(if (flipAxis) v.flip() else v))

        val majorBreakCoords = scaleBreaks.transformedValues
        val minorBreakCoords = majorBreakCoords
            .windowed(size = 2, step = 1)
            .map { (prev, next) -> prev + ((next - prev) / 2.0) }

        fun buildGrid(breaks: List<Double>): List<List<DoubleVector>> {
            val domainGrid = breaks.map { breakCoord ->
                when (horizontal) {
                    true -> listOf(
                        DoubleVector(breakCoord, domain.yRange().lowerEnd),
                        DoubleVector(breakCoord, domain.yRange().upperEnd)
                    )

                    false -> listOf(
                        DoubleVector(domain.xRange().lowerEnd, breakCoord),
                        DoubleVector(domain.xRange().upperEnd, breakCoord)
                    )
                }
            }

            return domainGrid.map { line -> resample(line, 0.5, ::toClient) }
        }

        fun buildBreaks(breaks: List<Double>): List<Double?> {
            return breaks.map { breakValue ->
                val breakCoord = when (horizontal) {
                    true -> DoubleVector(breakValue, domain.yRange().lowerEnd)
                    false -> DoubleVector(domain.xRange().lowerEnd, breakValue)
                }

                val breakClientCoord = toClient(breakCoord) ?: return@map null

                when (horizontal) {
                    true -> breakClientCoord.x
                    false -> breakClientCoord.y
                }
            }
        }

        val majorBreaks = buildBreaks(majorBreakCoords)
        val minorBreaks = buildBreaks(minorBreakCoords)

        val majorGrid = buildGrid(majorBreakCoords)
        val minorGrid = buildGrid(minorBreakCoords)
        return AxisComponent.BreaksData(
            majorBreaks = majorBreaks.filterNotNull(),
            minorBreaks = minorBreaks.filterNotNull(),
            majorLabels = pickAtIndices(scaleBreaks.labels, matchingIndices(majorBreaks) { it != null }),
            majorGrid = majorGrid,
            minorGrid = minorGrid
        )
    }
}
