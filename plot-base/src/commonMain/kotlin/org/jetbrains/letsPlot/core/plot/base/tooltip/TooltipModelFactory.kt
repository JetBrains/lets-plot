/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color.Companion.WHITE
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec.DataPoint

class TooltipModelFactory(
    private val contextualMapping: ContextualMapping,
    private val axisOrigin: DoubleVector,
    private val flippedAxis: Boolean,
    private val xAxisTheme: AxisTheme,
    private val yAxisTheme: AxisTheme,
    private val ctx: PlotContext
) {
    fun create(geomTarget: GeomTarget): List<TooltipModel> {
        return ArrayList(Helper(geomTarget, flippedAxis, ctx).createTooltipModels())
    }

    private inner class Helper(
        private val myGeomTarget: GeomTarget,
        private val flippedAxis: Boolean,
        ctx: PlotContext
    ) {
        private val myDataPoints = contextualMapping.getDataPoints(hitIndex(), ctx)
        private val myTooltipAnchor = contextualMapping.tooltipAnchor
        private val myTooltipMinWidth = contextualMapping.tooltipMinWidth
        private val myIsCrosshairEnabled = contextualMapping.isCrosshairEnabled
        private val myTooltipTitle = contextualMapping.getTitle(hitIndex(), ctx)

        fun createTooltipModels(): List<TooltipModel> {
            val tooltipModels = ArrayList<TooltipModel>()
            tooltipModels += axisTooltipModel()
            tooltipModels += sideTooltipModel()
            tooltipModels += generalTooltipModel()
            return tooltipModels
        }

        private fun hitIndex() = myGeomTarget.hitIndex
        private fun tooltipHint() = myGeomTarget.tooltipHint
        private fun sideHints() = myGeomTarget.aesTooltipHint

        private fun sideTooltipModel(): List<TooltipModel> {
            val tooltipModels = ArrayList<TooltipModel>()
            val sideDataPoints = sideDataPoints()
            sideHints().forEach { (aes, hint) ->
                val linesForAes = sideDataPoints
                    .filter { aes == it.aes }
                    .map(DataPoint::value)
                    .map(TooltipModel.Line.Companion::withValue)
                if (linesForAes.isNotEmpty()) {
                    tooltipModels.add(
                        TooltipModel(
                            tooltipHint = hint,
                            title = null,
                            lines = linesForAes,
                            fill = hint.fillColor ?: tooltipHint().fillColor
                            ?: tooltipHint().markerColors.firstOrNull() ?: WHITE,
                            markerColors = emptyList(),
                            isSide = true
                        )
                    )
                }
            }
            return tooltipModels
        }


        private fun axisTooltipModel(): List<TooltipModel> {
            val tooltipModels = ArrayList<TooltipModel>()
            val axis = mapOf(
                Aes.X to axisDataPoints().filter { Aes.X == it.aes }
                    .map(DataPoint::value)
                    .map(TooltipModel.Line.Companion::withValue),
                Aes.Y to axisDataPoints().filter { Aes.Y == it.aes }
                    .map(DataPoint::value)
                    .map(TooltipModel.Line.Companion::withValue)
            )
            axis.forEach { (aes, lines) ->
                if (lines.isNotEmpty()) {
                    val layoutHint = createHintForAxis(aes, flippedAxis)
                    tooltipModels.add(
                        TooltipModel(
                            tooltipHint = layoutHint,
                            title = null,
                            lines = lines,
                            fill = layoutHint.fillColor!!,
                            markerColors = emptyList(),
                            isSide = true
                        )
                    )
                }
            }
            return tooltipModels
        }

        private fun generalTooltipModel(): List<TooltipModel> {
            val generalDataPoints = generalDataPoints()
            val generalLines = generalDataPoints.map { TooltipModel.Line.withLabelAndValue(it.label, it.value) }

            return if (generalLines.isNotEmpty()) {
                listOf(
                    TooltipModel(
                        tooltipHint(),
                        title = myTooltipTitle,
                        lines = generalLines,
                        fill = null,
                        markerColors = tooltipHint().markerColors,
                        isSide = false,
                        anchor = myTooltipAnchor,
                        minWidth = myTooltipMinWidth,
                        isCrosshairEnabled = myIsCrosshairEnabled
                    )
                )
            } else {
                emptyList()
            }
        }

        private fun sideDataPoints() = myDataPoints.filter { it.isSide && !it.isAxis }
        private fun axisDataPoints() = myDataPoints.filter(DataPoint::isAxis)

        private fun generalDataPoints(): List<DataPoint> {
            val nonSideDataPoints = myDataPoints.filterNot(DataPoint::isSide)
            val sideDataPoints = sideDataPoints().mapNotNull(DataPoint::aes)
            val generalAesList = nonSideDataPoints.mapNotNull(DataPoint::aes) - sideDataPoints
            return nonSideDataPoints.filter { dataPoint ->
                when (dataPoint.aes) {
                    null -> true                // get all not aes (variables, text)
                    in generalAesList -> true   // get all existed in prepared aes list (mapped aes)
                    else -> false               // skip others (axis)
                }
            }
        }

        private fun createHintForAxis(
            aes: Aes<*>,
            flippedAxis: Boolean
        ): TooltipHint {
            val axis = aes.let {
                when {
                    flippedAxis && it == Aes.X -> Aes.Y
                    flippedAxis && it == Aes.Y -> Aes.X
                    else -> it
                }
            }
            return when (axis) {
                Aes.X -> {
                    TooltipHint.xAxisTooltip(
                        coord = DoubleVector(tooltipHint().coord.x, axisOrigin.y),
                        axisRadius = xAxisTheme.lineWidth() / 2,
                        fillColor = xAxisTheme.tooltipFill()
                    )
                }

                Aes.Y -> {
                    TooltipHint.yAxisTooltip(
                        coord = DoubleVector(axisOrigin.x, tooltipHint().coord.y),
                        axisRadius = yAxisTheme.lineWidth() / 2,
                        fillColor = yAxisTheme.tooltipFill()
                    )
                }

                else -> error("Not an axis aes: $axis")
            }
        }
    }
}
