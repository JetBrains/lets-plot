/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
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
    ): PolarBreaksData {
        return Helper(scaleBreaks, coord, gridDomain, flipAxis, orientation, labelAdjustments, axisTheme).breaksData()
    }

    private class Helper(
        val scaleBreaks: ScaleBreaks,
        val coord: PolarCoordinateSystem,
        val gridDomain: DoubleRectangle,
        val flipAxis: Boolean,
        val orientation: Orientation,
        val labelAdjustments: AxisComponent.TickLabelAdjustments,
        axisTheme: AxisTheme,
    ) {
        val center = coord.toClient(gridDomain.origin.flipIf(flipAxis)) ?: error("Failed to get center of the polar coordinate system")
        val tickLabelBaseOffset = AxisUtil.tickLabelBaseOffset(axisTheme, orientation)
        fun breaksData(): PolarBreaksData {
            check(scaleBreaks.transformedValues.size == scaleBreaks.labels.size) {
                "Breaks and labels must have the same size"
            }

            val majorBreaks = breaksToClient(scaleBreaks.transformedValues)
                .map { (i, clientTick) ->
                    MajorBreak(
                        index = i,
                        label = scaleBreaks.labels[i],
                        domValue = scaleBreaks.transformedValues[i],
                        coord = clientTick
                    )
                }
                .let {
                    if (it.size < 2) return@let it

                    val firstBr = it.first()
                    val lastBr = it.last()

                    if (firstBr.coord.subtract(lastBr.coord).length() > 3.0) return@let it

                    val cleaned = it.toMutableList()
                    cleaned[0] = MajorBreak(
                        index = firstBr.index,
                        label = "${lastBr.label}/${firstBr.label}", // Merge first and last label
                        domValue = firstBr.domValue,
                        coord = firstBr.coord
                    )
                    cleaned[cleaned.lastIndex] = MajorBreak(
                        index = lastBr.index,
                        label = "", // Empty label to not duplicate the merged label
                        domValue = lastBr.domValue,
                        coord = lastBr.coord
                    )
                    cleaned
                }

            val majorBreaksData = majorBreaks.mapNotNull { br ->
                val clientLine = buildGridLine(br.domValue) ?: return@mapNotNull null
                val labelOffset = labelAdjustments.labelOffset(tickLabelBaseOffset, br.index)
                RenderedMajorBreak(br.label, br.coord, labelOffset, clientLine)
            }

            val minorBreaks = minorDomainBreaks(majorBreaks.map { it.domValue })
                .let { minorDomainBreaks ->
                    breaksToClient(minorDomainBreaks)
                        .map { (i, clientBreak) -> Pair(minorDomainBreaks[i], clientBreak) }
                }

            val minorBreaksData = minorBreaks.mapNotNull { (domainTick, clientTick) ->
                val clientLine = buildGridLine(domainTick) ?: return@mapNotNull null
                Pair(clientTick, clientLine)
            }

            val axisLine = when (!orientation.isHorizontal) {
                true -> listOf(gridDomain.xRange().upperEnd).mapNotNull(::buildAngleGridLine).single()
                false -> listOf(gridDomain.yRange().upperEnd).mapNotNull(::buildRadiusGridLine).single()
            }

            return PolarBreaksData(
                majorBreaks = majorBreaksData.map { it.clientTick },
                majorLabelOffsets = majorBreaksData.map { it.labelOffset },
                majorGrid = majorBreaksData.map { it.gridLine },
                majorLabels = majorBreaksData.map { it.label },
                minorBreaks = minorBreaksData.map { (tick, _) -> tick },
                minorGrid = minorBreaksData.map { (_, gridLine) -> gridLine },
                axisLine = axisLine,
                center = center,
                startAngle = coord.startAngle,
            )
        }

        private class MajorBreak(
            val index: Int,
            val label: String,
            val domValue: Double,
            val coord: DoubleVector,
        )

        private class RenderedMajorBreak(
            val label: String,
            val clientTick: DoubleVector,
            val labelOffset: DoubleVector,
            val gridLine: List<DoubleVector>,
        )

        private fun breaksToClient(breaks: List<Double>) =
            toClient(breaks, gridDomain, coord, flipAxis, orientation.isHorizontal)
                .mapIndexedNotNull { i, clientTick ->
                    if (clientTick == null) return@mapIndexedNotNull null
                    val adjustedClientTick = when (orientation.isHorizontal) {
                        true -> clientTick.subtract(center)
                        false -> clientTick.rotateAround(center, coord.startAngle * coord.direction)
                    }
                    IndexedValue(i, adjustedClientTick)
                }


        /**
         * FixMe: polar hack:
         *   The generic `AxisUtil.toClient()` doesn't work because the `dataDomain` here might
         *   be "flipped" for polar `theta=Y`.
         *
         *  Duplicates AxisUtil.toClient()
         */
        private fun toClient(
            breaks: List<Double>,
            dataDomain: DoubleRectangle,
            coordinateSystem: CoordinateSystem,
            flipAxis: Boolean,
            horizontal: Boolean
        ): List<DoubleVector?> {
//            val hvDomain = dataDomain.flipIf(flipAxis)
            val hvDomain = dataDomain

            return breaks.map { breakValue ->
                when (horizontal) {
                    true -> DoubleVector(breakValue, hvDomain.yRange().upperEnd)
                    else -> DoubleVector(hvDomain.xRange().lowerEnd, breakValue)
                }
            }.map {
                val pointInDataDomain = it.flipIf(flipAxis)
                finiteOrNull(coordinateSystem.toClient(pointInDataDomain))
                    ?: return@map null
            }
        }

        private fun toClient(v: DoubleVector): DoubleVector {
            return coord.toClient(v.flipIf(flipAxis)) ?: error("Unexpected null value")
        }

        private fun buildAngleGridLine(breakCoord: Double): List<DoubleVector>? {
            if (breakCoord !in gridDomain.xRange()) {
                return null
            }

            return listOf(
                toClient(DoubleVector(breakCoord, gridDomain.yRange().lowerEnd)),
                toClient(DoubleVector(breakCoord, gridDomain.yRange().upperEnd))
            )
        }

        private fun buildRadiusGridLine(br: Double): List<DoubleVector>? {
            if (br !in gridDomain.yRange()) {
                return null
            }

            val line = listOf(
                DoubleVector(gridDomain.xRange().lowerEnd, br),
                DoubleVector(gridDomain.xRange().upperEnd, br)
            )
            return AdaptiveResampler.resample(line, AdaptiveResampler.PIXEL_PRECISION, this::toClient)
        }

        private fun buildGridLine(br: Double): List<DoubleVector>? {
            return when (orientation.isHorizontal) {
                true -> buildAngleGridLine(br)
                false -> buildRadiusGridLine(br)
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
        val majorLabelOffsets: List<DoubleVector>,
        val minorBreaks: List<DoubleVector>,
        val majorGrid: List<List<DoubleVector>>,
        val minorGrid: List<List<DoubleVector>>,
        val axisLine: List<DoubleVector>,
    )
}
