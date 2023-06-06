/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color.Companion.WHITE
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_RADIUS
import jetbrains.datalore.plot.builder.theme.AxisTheme

class TooltipSpecFactory(
    private val contextualMapping: ContextualMapping,
    private val axisOrigin: DoubleVector,
    private val flippedAxis: Boolean,
    private val xAxisTheme: AxisTheme,
    private val yAxisTheme: AxisTheme
) {
    fun create(geomTarget: GeomTarget, ctx: PlotContext): List<TooltipSpec> {
        return ArrayList(Helper(geomTarget, flippedAxis, ctx).createTooltipSpecs())
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

        internal fun createTooltipSpecs(): List<TooltipSpec> {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            tooltipSpecs += axisTooltipSpec()
            tooltipSpecs += sideTooltipSpec()
            tooltipSpecs += generalTooltipSpec()
            return tooltipSpecs
        }

        private fun hitIndex() = myGeomTarget.hitIndex
        private fun tipLayoutHint() = myGeomTarget.tipLayoutHint
        private fun sideHints() = myGeomTarget.aesTipLayoutHints

        private fun sideTooltipSpec(): List<TooltipSpec> {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            val sideDataPoints = sideDataPoints()
            sideHints().forEach { (aes, hint) ->
                val linesForAes = sideDataPoints
                    .filter { aes == it.aes }
                    .map(DataPoint::value)
                    .map(TooltipSpec.Line.Companion::withValue)
                if (linesForAes.isNotEmpty()) {
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = hint,
                            title = null,
                            lines = linesForAes,
                            fill = hint.fillColor ?: tipLayoutHint().fillColor
                            ?: tipLayoutHint().markerColors.firstOrNull() ?: WHITE,
                            markerColors = emptyList(),
                            isSide = true
                        )
                    )
                }
            }
            return tooltipSpecs
        }


        private fun axisTooltipSpec(): List<TooltipSpec> {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            val axis = mapOf(
                Aes.X to axisDataPoints().filter { Aes.X == it.aes }.map(DataPoint::value)
                    .map(TooltipSpec.Line.Companion::withValue),
                Aes.Y to axisDataPoints().filter { Aes.Y == it.aes }.map(DataPoint::value)
                    .map(TooltipSpec.Line.Companion::withValue)
            )
            axis.forEach { (aes, lines) ->
                if (lines.isNotEmpty()) {
                    val layoutHint = createHintForAxis(aes, flippedAxis)
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = layoutHint,
                            title = null,
                            lines = lines,
                            fill = layoutHint.fillColor!!,
                            markerColors = emptyList(),
                            isSide = true
                        )
                    )
                }
            }
            return tooltipSpecs
        }

        private fun generalTooltipSpec(): List<TooltipSpec> {
            val generalDataPoints = generalDataPoints()
            val generalLines = generalDataPoints.map { TooltipSpec.Line.withLabelAndValue(it.label, it.value) }

            return if (generalLines.isNotEmpty()) {
                listOf(
                    TooltipSpec(
                        tipLayoutHint(),
                        title = myTooltipTitle,
                        lines = generalLines,
                        fill = null,
                        markerColors = tipLayoutHint().markerColors,
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

        private fun createHintForAxis(aes: Aes<*>, flippedAxis: Boolean): TipLayoutHint {
            val axis = aes.let {
                when {
                    flippedAxis && it == Aes.X -> Aes.Y
                    flippedAxis && it == Aes.Y -> Aes.X
                    else -> it
                }
            }
            return when (axis) {
                Aes.X -> {
                    TipLayoutHint.xAxisTooltip(
                        coord = DoubleVector(tipLayoutHint().coord!!.x, axisOrigin.y),
                        axisRadius = AXIS_RADIUS,
                        fillColor = xAxisTheme.tooltipFill()
                    )
                }

                Aes.Y -> {
                    TipLayoutHint.yAxisTooltip(
                        coord = DoubleVector(axisOrigin.x, tipLayoutHint().coord!!.y),
                        axisRadius = AXIS_RADIUS,
                        fillColor = yAxisTheme.tooltipFill()
                    )
                }

                else -> error("Not an axis aes: $axis")
            }
        }
    }
}
