/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.AxisUtil.minorDomainBreaks
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import kotlin.math.PI

object PolarAxisUtil {
    fun breaksData(
        scaleBreaks: ScaleBreaks,
        coord: PolarCoordinateSystem,
        gridDomain: DoubleRectangle,
        flipAxis: Boolean,
        orientation: Orientation,
        axisTheme: AxisTheme,
        labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation),
    ): AxisComponent.BreaksData {
        check(scaleBreaks.transformedValues.size == scaleBreaks.labels.size) {
            "Breaks and labels must have the same size"
        }

        val majorDomainBreaks = scaleBreaks.transformedValues
        val majorLabels = scaleBreaks.labels
        val minorDomainBreaks = minorDomainBreaks(majorDomainBreaks)

        val majorClientBreaks = toClient(majorDomainBreaks, gridDomain, coord, flipAxis, orientation.isHorizontal)
        val minorClientBreaks = toClient(minorDomainBreaks, gridDomain, coord, flipAxis, orientation.isHorizontal)

        val axisDataRange = if (orientation.isHorizontal) gridDomain.xRange() else gridDomain.yRange()
        val majorGrid = buildGrid(majorDomainBreaks.filter { it in axisDataRange } + axisDataRange.upperEnd, gridDomain, coord, flipAxis, orientation.isHorizontal)
        val minorGrid = buildGrid(minorDomainBreaks.filter { it in axisDataRange }, gridDomain, coord, flipAxis, orientation.isHorizontal)

        // For coord_polar squash first and last labels into one to avoid overlapping.
        val labels = if (majorClientBreaks.size > 1 && majorClientBreaks.first().subtract(majorClientBreaks.last()).length() <= 3.0) {
            val labels = majorLabels.toMutableList()
            labels[labels.lastIndex] = "${labels[labels.lastIndex]}/${labels[0]}"
            labels[0] = ""
            labels
        } else {
            majorLabels
        }

        return AxisComponent.BreaksData(
            majorBreaks = majorClientBreaks,
            minorBreaks = minorClientBreaks,
            majorLabels = labels,
            majorGrid = majorGrid,
            minorGrid = minorGrid
        )
    }

    private fun toClient(v: DoubleVector, coordinateSystem: CoordinateSystem, flipAxis: Boolean): DoubleVector {
        return coordinateSystem.toClient(v.flipIf(flipAxis)) ?: error("Unexpected null value")
    }

    private fun toClient(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: PolarCoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<DoubleVector> {
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
        }.map { toClient(it, coordinateSystem, flipAxis) }
    }

    private fun buildGrid(
        breaks: List<Double>,
        gridDomain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<List<DoubleVector>> {
        val domainGrid = breaks.map { breakCoord ->
            when (horizontal) {
                true -> listOf(
                    DoubleVector(breakCoord, gridDomain.yRange().lowerEnd),
                    DoubleVector(breakCoord, gridDomain.yRange().upperEnd) // dataDomain to not go beyond the last major circle
                )

                false -> listOf(
                    DoubleVector(gridDomain.xRange().lowerEnd, breakCoord),
                    DoubleVector(gridDomain.xRange().upperEnd, breakCoord)
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