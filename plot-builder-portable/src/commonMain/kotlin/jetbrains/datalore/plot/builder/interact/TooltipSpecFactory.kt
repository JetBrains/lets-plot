/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_RADIUS
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR

class TooltipSpecFactory(
    private val contextualMapping: ContextualMapping,
    private val axisOrigin: DoubleVector
) {
    fun create(geomTarget: GeomTarget): List<TooltipSpec> {
        return ArrayList(Helper(geomTarget).createTooltipSpecs())
    }

    private inner class Helper(private val myGeomTarget: GeomTarget) {
        private val myDataPoints = contextualMapping.getDataPoints(hitIndex())
        private val myTooltipAnchor = contextualMapping.tooltipAnchor
        private val myTooltipMinWidth = contextualMapping.tooltipMinWidth
        private val myTooltipColor = contextualMapping.tooltipColor

        internal fun createTooltipSpecs(): List<TooltipSpec> {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            tooltipSpecs += outlierTooltipSpec()
            tooltipSpecs += generalTooltipSpec()
            tooltipSpecs += axisTooltipSpec()
            return tooltipSpecs
        }

        private fun hitIndex(): Int {
            return myGeomTarget.hitIndex
        }

        private fun tipLayoutHint(): TipLayoutHint {
            return myGeomTarget.tipLayoutHint
        }

        private fun outlierHints(): Map<Aes<*>, TipLayoutHint> {
            return myGeomTarget.aesTipLayoutHints
        }

        private fun outlierTooltipSpec(): List<TooltipSpec> {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            val outlierDataPoints = outlierDataPoints()
            outlierHints().forEach { (aes, hint) ->
                val linesForAes = outlierDataPoints
                    .filter { aes == it.aes }
                    .map(DataPoint::value)
                    .map(TooltipSpec.Line.Companion::withValue)
                if (linesForAes.isNotEmpty()) {
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = hint,
                            lines = linesForAes,
                            fill = hint.color ?: tipLayoutHint().color!!,
                            isOutlier = true
                        )
                    )
                }
            }
            return tooltipSpecs
        }


        private fun axisTooltipSpec(): List<TooltipSpec>  {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            val axis = mapOf(
                Aes.X to axisDataPoints().filter { Aes.X == it.aes }.map(DataPoint::value).map(TooltipSpec.Line.Companion::withValue),
                Aes.Y to axisDataPoints().filter { Aes.Y == it.aes }.map(DataPoint::value).map(TooltipSpec.Line.Companion::withValue)
            )
            axis.forEach { (aes, lines) ->
                if (lines.isNotEmpty()) {
                    val layoutHint = createHintForAxis(aes)
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = layoutHint,
                            lines = lines,
                            fill = layoutHint.color!!,
                            isOutlier = true
                        )
                    )
                }
            }
            return tooltipSpecs
        }

        private fun generalTooltipSpec(): List<TooltipSpec> {
            val generalLines = generalDataPoints().map { TooltipSpec.Line.withLabelAndValue(it.label, it.value) }
            return if (generalLines.isNotEmpty()) {
                listOf(
                    TooltipSpec(
                        tipLayoutHint(),
                        lines = generalLines,
                        fill = myTooltipColor ?: tipLayoutHint().color!!,
                        isOutlier = false,
                        anchor = myTooltipAnchor,
                        minWidth = myTooltipMinWidth
                    )
                )
            } else {
                emptyList()
            }
        }

        private fun outlierDataPoints(): List<DataPoint> {
            return myDataPoints.filter { it.isOutlier && !it.isAxis }
        }

        private fun axisDataPoints(): List<DataPoint> {
            return myDataPoints.filter(DataPoint::isAxis)
        }

        private fun generalDataPoints(): List<DataPoint> {
            val nonOutlierDataPoints = myDataPoints.filterNot(DataPoint::isOutlier)
            val outliers = outlierDataPoints().mapNotNull(DataPoint::aes)
            val generalAesList = nonOutlierDataPoints.mapNotNull(DataPoint::aes) - outliers
            return nonOutlierDataPoints.filter { dataPoint ->
                when (dataPoint.aes){
                    null -> true                // get all not aes (variables, text)
                    in generalAesList -> true   // get all existed in prepared aes list (mapped aes)
                    else -> false               // skip others (axis)
                }
            }
        }

        private fun createHintForAxis(aes: Aes<*>): TipLayoutHint {
            return when(aes) {
                 Aes.X -> TipLayoutHint.xAxisTooltip(
                     coord = DoubleVector(tipLayoutHint().coord!!.x, axisOrigin.y),
                     color = AXIS_TOOLTIP_COLOR,
                     axisRadius = AXIS_RADIUS
                 )
                Aes.Y -> TipLayoutHint.yAxisTooltip(
                    coord = DoubleVector(axisOrigin.x, tipLayoutHint().coord!!.y),
                    color = AXIS_TOOLTIP_COLOR,
                    axisRadius = AXIS_RADIUS
                )
                else -> error("Not an axis aes: $aes")
            }
        }
    }
}
