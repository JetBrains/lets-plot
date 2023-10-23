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
        fun toClient(v: DoubleRectangle): DoubleRectangle? = coord.toClient(if (flipAxis) v.flip() else v)
        fun toClient(breaks: List<Double>): List<Double?> {
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

            if (coord.isLinear) {
                val clientGrid = domainGrid.map { line -> line.mapNotNull(::toClient)  }
                val gridArea = toClient(domain)?.inflate(-6.0) ?: error("Cannot transform domain")

                return clientGrid.filter { line ->
                    line.any {
                        if (horizontal) {
                            it.x in gridArea.xRange()
                        } else {
                            it.y in gridArea.yRange()
                        }
                    }
                }

            } else {
                return domainGrid.map { line -> resample(line, 0.5, ::toClient) }
            }
        }

        val majorDomainBreak = scaleBreaks.transformedValues
        val minorDomainBreak = if (majorDomainBreak.size > 1) {
            val step = (majorDomainBreak[1] - majorDomainBreak[0])
            val start = majorDomainBreak[0] - step / 2.0
            (0..majorDomainBreak.size).map { start + it * step }
        } else {
            emptyList()
        }

        val majorClientBreaks = toClient(majorDomainBreak)
        val minorClientBreaks = toClient(minorDomainBreak)

        val majorGrid = buildGrid(majorDomainBreak)
        val minorGrid = buildGrid(minorDomainBreak)

        return AxisComponent.BreaksData(
            majorBreaks = majorClientBreaks.filterNotNull(),
            minorBreaks = minorClientBreaks.filterNotNull(),
            majorLabels = pickAtIndices(scaleBreaks.labels, matchingIndices(majorClientBreaks) { it != null }),
            majorGrid = majorGrid,
            minorGrid = minorGrid
        )
    }
}
