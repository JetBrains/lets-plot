/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory.axisTick
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec
import kotlin.math.abs

object AxisUtil {
    internal fun minorDomainBreaks(majorDomainBreaks: List<Double>) =
        if (majorDomainBreaks.size > 2) {
            val step = (majorDomainBreaks[1] - majorDomainBreaks[0])
            val start = majorDomainBreaks[0] - step / 2.0
            (0..(majorDomainBreaks.size)).map { start + it * step }
        } else {
            emptyList()
        }

    fun breaksData(
        scaleBreaks: ScaleBreaks,
        coord: CoordinateSystem,
        domain: DoubleRectangle,
        flipAxis: Boolean,
        orientation: Orientation,
        axisTheme: AxisTheme,
        labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation)
    ): AxisComponent.BreaksData {
        val tickLabelBaseOffset = tickLabelBaseOffset(axisTheme, orientation)
        val labelsMap = TickLabelsMap(orientation.isHorizontal, axisTick(axisTheme), labelAdjustments.rotationDegree)

        val gridRect = toClient(domain, coord, flipAxis)
            ?: DoubleRectangle.LTRB(-1_000_000, -1_000_000, 1_000_000, 1_000_000) // better than fail

        val majorBreaks = toClient(scaleBreaks.transformedValues, domain, coord, flipAxis, orientation.isHorizontal)
            .mapIndexedNotNull { i, clientTick ->
                when (clientTick) {
                    null, !in gridRect -> null
                    else -> IndexedValue(i, Triple(scaleBreaks.labels[i], scaleBreaks.transformedValues[i], clientTick))
                }
            }
            .filter { (i, br) ->
                val (label, _, clientBreak) = br
                val labelOffset = tickLabelBaseOffset.add(labelAdjustments.additionalOffset(i))
                val loc = if (orientation.isHorizontal) clientBreak.x else clientBreak.y
                labelsMap.haveSpace(loc, label, labelOffset)
            }

        val minorBreaks = minorDomainBreaks(majorBreaks.map { it.value.second })
            .let { minorDomainBreaks ->
                toClient(minorDomainBreaks, domain, coord, flipAxis, orientation.isHorizontal)
                    .mapIndexedNotNull { i, clientBreak ->
                        when (clientBreak) {
                            null, !in gridRect -> null
                            else -> Pair(minorDomainBreaks[i], clientBreak)
                        }
                    }
            }

        val majorBreaksData = majorBreaks.mapNotNull { (_, br) ->
            val (label, domainTick, clientTick) = br
            val clientLine = buildGridLine(domainTick, domain, coord, flipAxis, orientation.isHorizontal)
                ?: return@mapNotNull null
            Triple(label, clientTick, clientLine)
        }

        val minorBreaksData = minorBreaks.mapNotNull { (domainTick, clientTick) ->
            val clientLine = buildGridLine(domainTick, domain, coord, flipAxis, orientation.isHorizontal)
                ?: return@mapNotNull null
            Pair(clientTick, clientLine)
        }

        return AxisComponent.BreaksData(
            majorBreaks = majorBreaksData.map { (_, tick, _) -> tick },
            majorGrid = majorBreaksData.map { (_, _, gridLine) -> gridLine },
            majorLabels = majorBreaksData.map { (label, _, _) -> label },
            minorBreaks = minorBreaksData.map { (tick, _) -> tick },
            minorGrid = minorBreaksData.map { (_, gridLine) -> gridLine }
        )
    }

    private fun toClient(v: DoubleVector, coordinateSystem: CoordinateSystem, flipAxis: Boolean): DoubleVector? {
        return finiteOrNull(coordinateSystem.toClient(v.flipIf(flipAxis)))
    }

    private fun toClient(v: DoubleRectangle, coordinateSystem: CoordinateSystem, flipAxis: Boolean): DoubleRectangle? {
        return coordinateSystem.toClient(v.flipIf(flipAxis))
    }

    internal fun toClient(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<DoubleVector?> {
        return breaks.map { breakValue ->
            when (horizontal) {
                true -> DoubleVector(breakValue, domain.yRange().upperEnd)
                false -> DoubleVector(domain.xRange().lowerEnd, breakValue)
            }
        }.map { toClient(it, coordinateSystem, flipAxis) ?: return@map null }
    }

    private fun buildGridLine(
        tick: Double,
        domain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean
    ): List<DoubleVector>? {
        val domainLine = when (horizontal) {
            true -> listOf(
                DoubleVector(tick, domain.yRange().lowerEnd),
                DoubleVector(tick, domain.yRange().upperEnd)
            )

            false -> listOf(
                DoubleVector(domain.xRange().lowerEnd, tick),
                DoubleVector(domain.xRange().upperEnd, tick)
            )
        }

        val clientLine = domainLine.map { toClient(it, coordinateSystem, flipAxis) }

        return when {
            null in clientLine -> null
            else -> clientLine.requireNoNulls()
        }
    }

    fun tickLabelBaseOffset(axisTheme: AxisTheme, orientation: Orientation): DoubleVector {
        val distance = axisTheme.tickLabelDistance(orientation.isHorizontal)
        return when (orientation) {
            Orientation.LEFT -> DoubleVector(axisTheme.tickLabelMargins().left - distance, 0.0)
            Orientation.RIGHT -> DoubleVector(distance - axisTheme.tickLabelMargins().right, 0.0)
            Orientation.TOP -> DoubleVector(0.0, axisTheme.tickLabelMargins().top - distance)
            Orientation.BOTTOM -> DoubleVector(0.0, distance - axisTheme.tickLabelMargins().bottom)
        }
    }

    internal class TickLabelsMap(
        private val horizontalAxis: Boolean,
        private val labelSpec: LabelSpec,
        private val rotationDegree: Double
    ) {
        private val filledAreas = ArrayList<DoubleRectangle>()

        fun haveSpace(loc: Double, label: String, labelOffset: DoubleVector): Boolean {
            if (!isRelevant(rotationDegree)) return true

            val rect = labelRect(loc, label, rotationDegree, labelOffset)
            // find overlap
            if (filledAreas.any { it.intersects(rect) }) {
                // overlap - don't add this label
                return false
            }
            filledAreas.add(rect)
            return true
        }

        private fun isRelevant(rotationDegree: Double): Boolean {
            return isVertical(rotationDegree) || isHorizontal(rotationDegree)
        }

        private fun isHorizontal(rotationDegree: Double): Boolean {
            return rotationDegree % 180 == 0.0
        }

        private fun isVertical(rotationDegree: Double): Boolean {
            return abs(rotationDegree / 90) % 2 == 1.0
        }

        private fun labelRect(
            loc: Double,
            label: String,
            rotationDegree: Double,
            labelOffset: DoubleVector
        ): DoubleRectangle {
            val labelNormalSize = labelSpec.dimensions(label)
            val wh = labelNormalSize.flipIf(isVertical(rotationDegree))
            val origin = if (horizontalAxis) DoubleVector(loc, 0.0) else DoubleVector(0.0, loc)
            return DoubleRectangle(origin, wh)
                .subtract(wh.mul(0.5)) // labels use central adjustments
                .add(labelOffset)
        }
    }

}
