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
        val center = coord.toClient(gridDomain.origin) ?: error("Failed to get center of the polar coordinate system")
        val IndexedValue<Triple<String, Double, DoubleVector>>.label get() = value.first
        val IndexedValue<Triple<String, Double, DoubleVector>>.domValue get() = value.second
        val IndexedValue<Triple<String, Double, DoubleVector>>.coord get() = value.third

        fun breaksData(): PolarBreaksData {
            check(scaleBreaks.transformedValues.size == scaleBreaks.labels.size) {
                "Breaks and labels must have the same size"
            }

            val majorBreaks = breaksToClient(scaleBreaks.transformedValues)
                .mapIndexed { i, clientTick ->
                    IndexedValue(i, Triple(scaleBreaks.labels[i], scaleBreaks.transformedValues[i], clientTick))
                }
                .let {
                    if (it.size < 2) return@let it

                    val firstBr = it.first()
                    val lastBr = it.last()

                    if (firstBr.coord.subtract(lastBr.coord).length() > 3.0) return@let it

                    val newFirstBr = IndexedValue(
                        index = firstBr.index,
                        value = Triple(
                            "${lastBr.label}/${firstBr.label}", // Merge first and last label
                            firstBr.domValue,
                            firstBr.coord
                        )
                    )
                    val newLastBr = IndexedValue(
                        index = lastBr.index,
                        value = Triple(
                            "", // Empty label to not duplicate the merged label
                            lastBr.domValue,
                            lastBr.coord
                        )
                    )

                    val cleaned = it.toMutableList()
                    cleaned[newFirstBr.index] = newFirstBr
                    cleaned[newLastBr.index] = newLastBr
                    cleaned
                }

            val majorBreaksData = majorBreaks.mapNotNull { (_, br) ->
                val (label, domainTick, clientTick) = br
                val clientLine = buildGridLine(domainTick) ?: return@mapNotNull null
                Triple(label, clientTick, clientLine)
            }

            val minorBreaks = minorDomainBreaks(majorBreaks.map { it.value.second })
                .let { minorDomainBreaks ->
                    breaksToClient(minorDomainBreaks)
                        .mapIndexed { i, clientBreak -> Pair(minorDomainBreaks[i], clientBreak) }
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
                majorBreaks = majorBreaksData.map { (_, tick, _) -> tick },
                majorGrid = majorBreaksData.map { (_, _, gridLine) -> gridLine },
                majorLabels = majorBreaksData.map { (label, _, _) -> label },
                minorBreaks = minorBreaksData.map { (tick, _) -> tick },
                minorGrid = minorBreaksData.map { (_, gridLine) -> gridLine },
                axisLine = axisLine,
                center = center,
                startAngle = coord.startAngle,
            )
        }

        private fun breaksToClient(breaks: List<Double>) =
            toClient(breaks, gridDomain, coord, flipAxis, orientation.isHorizontal)
                .filterNotNull()
                .map {
                    when (orientation.isHorizontal) {
                        true -> it.subtract(center)
                        false -> it.rotateAround(center, coord.startAngle * coord.direction)
                    }
                }


        /**
         * FixMe: polar hack:
         *   The generic `AxisUtil.toClient()` doesn't work bekause the `dataDomain` here might
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
        val minorBreaks: List<DoubleVector>,
        val majorGrid: List<List<DoubleVector>>,
        val minorGrid: List<List<DoubleVector>>,
        val axisLine: List<DoubleVector>,
    )
}
