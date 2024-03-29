/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.pickAtIndices
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PanelTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLabelSpecFactory
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec
import kotlin.math.abs

object AxisUtil {
    internal fun minorDomainBreaks(visibleMajorDomainBreak: List<Double>) =
        if (visibleMajorDomainBreak.size > 2) {
            val step = (visibleMajorDomainBreak[1] - visibleMajorDomainBreak[0])
            val start = visibleMajorDomainBreak[0] - step / 2.0
            (0..(visibleMajorDomainBreak.size)).map { start + it * step }
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
        panelTheme: PanelTheme,
        labelAdjustments: AxisComponent.TickLabelAdjustments = AxisComponent.TickLabelAdjustments(orientation)
    ): AxisComponent.BreaksData {
        val majorClientBreaks = toClient(scaleBreaks.transformedValues, domain, coord, flipAxis, orientation.isHorizontal)

        // cleanup overlapping labels
        // WARNING: highly coupled with AxisComponent
        val tickLabelBaseOffset = tickLabelBaseOffset(axisTheme, orientation)
        val labelsMap = TickLabelsMap(
            orientation.isHorizontal,
            PlotLabelSpecFactory.axisTick(axisTheme),
            labelAdjustments.rotationDegree
        )
        val visibleBreaks = scaleBreaks.labels.zip(majorClientBreaks).mapIndexedNotNull { i, pair ->
            val br = pair.second ?: return@mapIndexedNotNull null
            val label = pair.first
            val labelOffset = tickLabelBaseOffset.add(labelAdjustments.additionalOffset(i))

            val loc = if (orientation.isHorizontal) br.x else br.y
            if (labelsMap.haveSpace(loc, label, labelOffset)) {
                return@mapIndexedNotNull i
            } else {
                return@mapIndexedNotNull null
            }
        }

        val visibleMajorLabels = pickAtIndices(scaleBreaks.labels, visibleBreaks)
        val visibleMajorDomainBreak = pickAtIndices(scaleBreaks.transformedValues, visibleBreaks)
        val visibleMinorDomainBreak = if (visibleMajorDomainBreak.size > 1) {
            val step = (visibleMajorDomainBreak[1] - visibleMajorDomainBreak[0])
            val start = visibleMajorDomainBreak[0] - step / 2.0
            (0..visibleMajorDomainBreak.size).map { start + it * step }
        } else {
            emptyList()
        }

        val visibleMajorClientBreaks = pickAtIndices(majorClientBreaks, visibleBreaks)
            .map { checkNotNull(it) { "Nulls are not allowed. Properly clean and sync breaks, grids and labels." } }

        val visibleMinorClientBreaks =
            toClient(visibleMinorDomainBreak, domain, coord, flipAxis, orientation.isHorizontal)
                .map { checkNotNull(it) { "Nulls are not allowed. Properly clean and sync breaks, grids and labels." } }

        val majorGrid = buildGrid(visibleMajorDomainBreak, domain, coord, flipAxis, orientation.isHorizontal, panelTheme)
        val minorGrid = buildGrid(visibleMinorDomainBreak, domain, coord, flipAxis, orientation.isHorizontal, panelTheme)

        return AxisComponent.BreaksData(
            majorBreaks = visibleMajorClientBreaks,
            minorBreaks = visibleMinorClientBreaks,
            majorLabels = visibleMajorLabels,
            majorGrid = majorGrid,
            minorGrid = minorGrid
        )
    }

    private fun toClient(v: DoubleVector, coordinateSystem: CoordinateSystem, flipAxis: Boolean): DoubleVector? {
        return finiteOrNull(coordinateSystem.toClient(v.flipIf(flipAxis)))
    }

    private fun toClient(v: DoubleRectangle, coordinateSystem: CoordinateSystem, flipAxis: Boolean): DoubleRectangle? {
        return coordinateSystem.toClient(v.flipIf(flipAxis))
    }

    private fun toClient(
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

    private fun buildGrid(
        breaks: List<Double>,
        domain: DoubleRectangle,
        coordinateSystem: CoordinateSystem,
        flipAxis: Boolean,
        horizontal: Boolean,
        panelTheme: PanelTheme
    ): List<List<DoubleVector>> {
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

        val clientGrid = domainGrid.map { line -> line.mapNotNull { toClient(it, coordinateSystem, flipAxis) } }
        val gridArea = toClient(domain, coordinateSystem, flipAxis)?.let { rect ->
            when (panelTheme.showRect() || panelTheme.showBorder()) {
                true -> rect.inflate(-6.0)
                false -> rect
            }
        } ?: error("Cannot transform domain")

        return clientGrid.filter { line ->
            line.any {
                if (horizontal) {
                    it.x in gridArea.xRange()
                } else {
                    it.y in gridArea.yRange()
                }
            }
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
