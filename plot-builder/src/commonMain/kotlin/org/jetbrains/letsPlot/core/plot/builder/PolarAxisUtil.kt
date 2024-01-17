/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import kotlin.math.PI

object PolarAxisUtil {
    fun breaksData(
        scaleBreaks: ScaleBreaks,
        coord: PolarCoordinateSystem,
        dataDomain: DoubleRectangle,
        plotDomain: DoubleRectangle,
        flipAxis: Boolean,
        orientation: Orientation,
        axisTheme: AxisTheme,
        labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation),
    ): AxisComponent.BreaksData {
        val majorClientBreaks = toClient(scaleBreaks.transformedValues, plotDomain, coord, flipAxis, orientation.isHorizontal)

        val visibleBreaks = majorClientBreaks.indices.toList()

        val axisDataRange = if (orientation.isHorizontal) {
            dataDomain.xRange()
        } else {
            dataDomain.yRange()
        }

        val visibleMajorLabels = SeriesUtil.pickAtIndices(scaleBreaks.labels, visibleBreaks)
        val visibleMajorDomainBreak = SeriesUtil.pickAtIndices(scaleBreaks.transformedValues, visibleBreaks)
            // TODO: refactor and add tests
            .map {
                    if (it < axisDataRange.lowerEnd) {
                        axisDataRange.lowerEnd
                    } else if (it > axisDataRange.upperEnd) {
                        axisDataRange.upperEnd
                    } else {
                        it
                    }
            } + axisDataRange.upperEnd // always add last major circle

        val visibleMinorDomainBreak = if (visibleMajorDomainBreak.size > 2) {
            val step = (visibleMajorDomainBreak[1] - visibleMajorDomainBreak[0])
            val start = visibleMajorDomainBreak[0] - step / 2.0
            (0 .. (visibleMajorDomainBreak.size )).map { start + it * step }.filter { it in axisDataRange }
        } else {
            emptyList()
        }

        val visibleMajorClientBreaks = SeriesUtil.pickAtIndices(majorClientBreaks, visibleBreaks)
            .map { checkNotNull(it) { "Nulls are not allowed. Properly clean and sync breaks, grids and labels." } }

        val visibleMinorClientBreaks =
            toClient(visibleMinorDomainBreak, plotDomain, coord, flipAxis, orientation.isHorizontal)
                .map { checkNotNull(it) { "Nulls are not allowed. Properly clean and sync breaks, grids and labels." } }

        val majorGrid = buildGrid(visibleMajorDomainBreak, dataDomain, plotDomain, coord, flipAxis, orientation.isHorizontal)
        val minorGrid = buildGrid(visibleMinorDomainBreak, dataDomain, plotDomain, coord, flipAxis, orientation.isHorizontal)

        // For coord_polar squash first and last labels into one to avoid overlapping.
        val labels = if (visibleMajorClientBreaks.size > 1 && visibleMajorClientBreaks.first()
                .subtract(visibleMajorClientBreaks.last()).length() <= 3.0
        ) {
            val labels = visibleMajorLabels.toMutableList()
            labels[labels.lastIndex] = "${labels[labels.lastIndex]}/${labels[0]}"
            labels[0] = ""
            labels
        } else {
            visibleMajorLabels
        }

        return AxisComponent.BreaksData(
            majorBreaks = visibleMajorClientBreaks,
            minorBreaks = visibleMinorClientBreaks,
            majorLabels = labels,
            majorGrid = majorGrid,
            minorGrid = minorGrid
        )
    }

    private fun toClient(v: DoubleVector, coordinateSystem: CoordinateSystem, flipAxis: Boolean): DoubleVector? {
        return SeriesUtil.finiteOrNull(coordinateSystem.toClient(v.flipIf(flipAxis)))
    }

    private fun toClient(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: PolarCoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<DoubleVector?> {
        return breaks.map { breakValue ->
            when (horizontal) {
                true -> DoubleVector(breakValue, domain.yRange().upperEnd)
                false -> {
                    val startAnglePercent = (coordinateSystem.startAngle % (2 * PI)) / (2 * PI)
                    val startAngleOffset = domain.xRange().length * startAnglePercent
                    val verticalAngleValue = (domain.xRange().lowerEnd - startAngleOffset).let {
                        when { // non-normalized domain value
                            it < domain.xRange().lowerEnd -> it + domain.xRange().length
                            it > domain.xRange().upperEnd -> it - domain.xRange().length
                            else -> it
                        }
                    }
                    DoubleVector(verticalAngleValue, breakValue)
                }
            }
        }.map { toClient(it, coordinateSystem, flipAxis) ?: return@map null }
    }

    private fun buildGrid(
        breaks: List<Double>,
        dataDomain: DoubleRectangle,
        plotDomain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<List<DoubleVector>> {
        val domainGrid = breaks.map { breakCoord ->
            when (horizontal) {
                true -> listOf(
                    DoubleVector(breakCoord, plotDomain.yRange().lowerEnd),
                    DoubleVector(breakCoord, dataDomain.yRange().upperEnd) // dataDomain to not go beyond the last major circle
                )

                false -> listOf(
                    DoubleVector(plotDomain.xRange().lowerEnd, breakCoord),
                    DoubleVector(plotDomain.xRange().upperEnd, breakCoord)
                )
            }
        }

        return domainGrid.map { line ->
            AdaptiveResampler.resample(line, 0.5) {
                toClient(
                    it,
                    coordinateSystem,
                    flipAxis
                )
            }
        }
    }
}