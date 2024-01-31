/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
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
        labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation),
    ): PolarBreaksData {
        return Helper(scaleBreaks, coord, gridDomain, flipAxis, orientation, labelAdjustments).breaksData()
    }


    private class Helper(
        val scaleBreaks: ScaleBreaks,
        val coord: PolarCoordinateSystem,
        val gridDomain: DoubleRectangle,
        val flipAxis: Boolean,
        val orientation: Orientation,
        val labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation),
    ) {
        fun breaksData(
        ): PolarBreaksData {
            check(scaleBreaks.transformedValues.size == scaleBreaks.labels.size) {
                "Breaks and labels must have the same size"
            }

            val majorDomainBreaks = scaleBreaks.transformedValues
            val majorLabels = scaleBreaks.labels
            val minorDomainBreaks = minorDomainBreaks(majorDomainBreaks)

            val majorClientBreaks = buildBreaks(majorDomainBreaks)
            val minorClientBreaks = buildBreaks(minorDomainBreaks)

            val majorGrid = buildGrid(majorDomainBreaks)
            val minorGrid = buildGrid(minorDomainBreaks)
            val axisLine = buildAxis()

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

            val center = toClient(gridDomain.origin)
            return PolarBreaksData(
                center = center,
                startAngle = coord.startAngle,
                majorBreaks = majorClientBreaks.map { it.subtract(center) },
                minorBreaks = minorClientBreaks.map { it.subtract(center) },
                majorLabels = labels,
                majorGrid = majorGrid,
                minorGrid = minorGrid,
                axisLine = axisLine,
            )
        }

        private fun toClient(v: DoubleVector): DoubleVector {
            return coord.toClient(v.flipIf(flipAxis)) ?: error("Unexpected null value")
        }

        private fun buildRadiusBreaks(breaks: List<Double>): List<DoubleVector> {
            val center = coord.toClient(gridDomain.origin)!!
            return breaks
                .map { breakValue -> DoubleVector(gridDomain.xRange().lowerEnd, breakValue) }
                .map { toClient(it) }
                // revert angle to align breaks vertically, or they will not match the grid (or even collapse into one)
                .map { it.rotateAround(center, coord.startAngle * coord.direction) }
        }

        private fun buildAngleBreaks(
            breaks: List<Double>
        ): List<DoubleVector> {
            val center = coord.toClient(gridDomain.origin)!!
            return breaks
                .map { DoubleVector(it, gridDomain.yRange().upperEnd) }
                .map { toClient(it) }
                .map { it.subtract(center).mul(1.0).add(center) }
        }

        private fun buildBreaks(breaks: List<Double>): List<DoubleVector> {
            return when (orientation.isHorizontal) {
                true -> buildAngleBreaks(breaks)
                false -> buildRadiusBreaks(breaks)
            }
        }

        private fun buildAngleGrid(breaks: List<Double>): List<List<DoubleVector>> {
            return breaks
                .filter { it in gridDomain.xRange() }
                .map { breakCoord ->
                    listOf(
                        toClient(DoubleVector(breakCoord, gridDomain.yRange().lowerEnd)),
                        toClient(DoubleVector(breakCoord, gridDomain.yRange().upperEnd))
                    )
                }
        }

        private fun buildRadiusGrid(breaks: List<Double>): List<List<DoubleVector>> {
            return (breaks + gridDomain.yRange().upperEnd)
                .filter { it in gridDomain.yRange() }
                .map {
                    listOf(
                        DoubleVector(gridDomain.xRange().lowerEnd, it),
                        DoubleVector(gridDomain.xRange().upperEnd, it)
                    )
                }.map { line -> AdaptiveResampler.resample(line, 0.5) { toClient(it) } }
        }

        private fun buildAxis(): List<DoubleVector> {
            return when (!orientation.isHorizontal) {
                true -> buildAngleGrid(listOf(gridDomain.xRange().upperEnd)).single()
                false -> buildRadiusGrid(emptyList()).single() // buildRadiusGrid always builds axis grid line even without breaks
            }
        }

        private fun buildGrid(breaks: List<Double>): List<List<DoubleVector>> {
            return when (orientation.isHorizontal) {
                true -> buildAngleGrid(breaks)
                false -> buildRadiusGrid(breaks)
            }
        }
    }

    // TODO: grids: use a single DoubleVector to denote a radius vector or an angle grid line
    // TODO: Build grid using SvgCircleElement and SvgLineElement
    class PolarBreaksData(
        val center: DoubleVector,
        val startAngle: Double,
        val majorBreaks: List<DoubleVector>,
        val majorLabels: List<String>,
        val minorBreaks: List<DoubleVector>,
        val majorGrid: List<List<DoubleVector>>,
        val minorGrid: List<List<DoubleVector>>,
        val axisLine: List<DoubleVector>,
    )
}
