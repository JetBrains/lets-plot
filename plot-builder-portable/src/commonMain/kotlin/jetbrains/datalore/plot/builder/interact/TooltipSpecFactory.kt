/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.MappedDataAccess.MappedData
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
        private val myDataAccess: MappedDataAccess = contextualMapping.dataContext.mappedDataAccess
        private val myDataPoints = contextualMapping.getDataPoints(hitIndex())

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
                val linesForAes = outlierDataPoints.filter { aes == it.aes }.map { TooltipSpec.LabelValue(null, it.value) }
                if (linesForAes.isNotEmpty()) {
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = hint,
                            labelValues = linesForAes,
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
                Aes.X to axisDataPoints().filter { Aes.X == it.aes }.map { TooltipSpec.LabelValue(null, it.value) },
                Aes.Y to axisDataPoints().filter { Aes.Y == it.aes }.map { TooltipSpec.LabelValue(null, it.value) }
            )
            axis.forEach { (aes, lines) ->
                if (lines.isNotEmpty()) {
                    val layoutHint = createHintForAxis(aes)
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = layoutHint,
                            labelValues = lines,
                            fill = layoutHint.color!!,
                            isOutlier = true
                        )
                    )
                }
            }
            return tooltipSpecs
        }

        private fun generalTooltipSpec(): List<TooltipSpec> {
            val generalLines = generalDataPoints().map { TooltipSpec.LabelValue(it.label, it.value) }
            return if (generalLines.isNotEmpty()) {
                listOf(
                    TooltipSpec(
                        tipLayoutHint(),
                        labelValues = generalLines,
                        fill = tipLayoutHint().color!!,
                        isOutlier = false
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
            val generalAesList = removeDiscreteDuplicatedMappings(
                aesWithoutOutliers = nonOutlierDataPoints.mapNotNull(DataPoint::aes) - outliers
            )
            return nonOutlierDataPoints.filter { dataPoint ->
                when (dataPoint.aes){
                    null -> true                // get all not aes (variables, text)
                    in generalAesList -> true   // get all existed in prepared aes list (mapped aes)
                    else -> false               // skip others (axis)
                }
            }
        }

        private fun removeDiscreteDuplicatedMappings(aesWithoutOutliers: List<Aes<*>>): List<Aes<*>> {
            if (aesWithoutOutliers.isEmpty()) {
                return emptyList()
            }

            val mappingsToShow = HashMap<String, Pair<Aes<*>, MappedData<*>>>()
            for (aes in aesWithoutOutliers) {
                if (!isMapped(aes)) {
                    continue
                }

                val mappingToCheck = getMappedData(aes)
                if (!mappingsToShow.containsKey(mappingToCheck.label)) {
                    mappingsToShow[mappingToCheck.label] = Pair(aes, mappingToCheck)
                    continue
                }

                val mappingToShow = mappingsToShow[mappingToCheck.label]?.second
                if (!mappingToShow!!.isContinuous && mappingToCheck.isContinuous) {
                    mappingsToShow[mappingToCheck.label] = Pair(aes, mappingToCheck)
                }
            }
            return mappingsToShow.values.map { pair -> pair.first }
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

        private fun isMapped(aes: Aes<*>): Boolean {
            return myDataAccess.isMapped(aes)
        }

        private fun <T> getMappedData(aes: Aes<T>): MappedData<T> {
            return myDataAccess.getMappedData(aes, hitIndex())
        }
    }
}
