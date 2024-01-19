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

        val majorClientBreaks = buildBreaks(majorDomainBreaks, gridDomain, coord, flipAxis, orientation.isHorizontal)
        val minorClientBreaks = buildBreaks(minorDomainBreaks, gridDomain, coord, flipAxis, orientation.isHorizontal)

        val majorGrid = buildGrid(majorDomainBreaks, gridDomain, coord, flipAxis, orientation.isHorizontal)
        val minorGrid = buildGrid(minorDomainBreaks, gridDomain, coord, flipAxis, orientation.isHorizontal)

        // For coord_polar squash first and last labels into one to avoid overlapping.
        val labels = if (
            majorClientBreaks.size > 1 &&
            majorClientBreaks.first().subtract(majorClientBreaks.last()).length() <= 3.0
        ) {
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

    private fun buildRadiusBreaks(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: PolarCoordinateSystem,
        flipAxis: Boolean
    ): List<DoubleVector> {
        val center = coordinateSystem.toClient(domain.origin)!!
        return breaks
            .map { breakValue -> DoubleVector(domain.xRange().lowerEnd, breakValue) }
            .map { toClient(it, coordinateSystem, flipAxis) }
            // revert angle to align breaks vertically, or they will not match the grid (or even collapse into one)
            .map { it.rotateAround(center, coordinateSystem.startAngle * coordinateSystem.direction) }
    }

    private fun buildAngleBreaks(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: PolarCoordinateSystem,
        flipAxis: Boolean
    ): List<DoubleVector> {
        val center = coordinateSystem.toClient(domain.origin)!!
        return breaks
            .map { DoubleVector(it, domain.yRange().upperEnd) }
            .map { toClient(it, coordinateSystem, flipAxis) }
            .map { it.subtract(center).mul(1.05).add(center) }
    }

    private fun buildBreaks(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: PolarCoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<DoubleVector> {
        return when (horizontal) {
            true -> buildAngleBreaks(breaks, domain, coordinateSystem, flipAxis)
            false -> buildRadiusBreaks(breaks, domain, coordinateSystem, flipAxis)
        }
    }

    private fun buildAngleGrid(
        breaks: List<Double>,
        gridDomain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
    ): List<List<DoubleVector>> {
        return breaks
            .filter { it in gridDomain.xRange() }
            .map { breakCoord ->
            listOf(
                toClient(DoubleVector(breakCoord, gridDomain.yRange().lowerEnd), coordinateSystem, flipAxis),
                toClient(DoubleVector(breakCoord, gridDomain.yRange().upperEnd), coordinateSystem, flipAxis)
            )
        }
    }

    private fun buildRadiusGrid(
        breaks: List<Double>,
        gridDomain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
    ): List<List<DoubleVector>> {
        return (breaks + gridDomain.yRange().upperEnd)
            .filter { it in gridDomain.yRange() }
            .map {
                listOf(
                    DoubleVector(gridDomain.xRange().lowerEnd, it),
                    DoubleVector(gridDomain.xRange().upperEnd, it)
                )
            }.map { line -> AdaptiveResampler.resample(line, 0.5) { toClient(it, coordinateSystem, flipAxis) } }
    }

    private fun buildGrid(
        breaks: List<Double>,
        gridDomain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<List<DoubleVector>> {
        return when (horizontal) {
            true -> buildAngleGrid(breaks, gridDomain, coordinateSystem, flipAxis)
            false -> buildRadiusGrid(breaks, gridDomain, coordinateSystem, flipAxis)
        }
    }
}