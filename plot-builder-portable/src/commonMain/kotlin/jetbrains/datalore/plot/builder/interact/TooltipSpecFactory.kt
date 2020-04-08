/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.MappedDataAccess.MappedData
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_X1
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y
import jetbrains.datalore.plot.builder.map.GeoPositionField.POINT_Y1

class TooltipSpecFactory(
    contextualMapping: ContextualMapping,
    private val axisOrigin: DoubleVector) {

    private val myTooltipAes: List<Aes<*>> = contextualMapping.tooltipAes
    private val myAxisAes: List<Aes<*>> = contextualMapping.axisAes
    private val myDataAccess: MappedDataAccess = contextualMapping.dataAccess

    fun create(geomTarget: GeomTarget): List<TooltipSpec> {
        return ArrayList(Helper(geomTarget).tooltipSpecs)
    }

    private inner class Helper(private val myGeomTarget: GeomTarget) {
        internal val tooltipSpecs = ArrayList<TooltipSpec>()
        private val myShortLabels = ArrayList<String>()
        private val myAesWithoutHint = ArrayList(myTooltipAes)

        init {
            listOf(Aes.X, Aes.Y)
                    .forEach { aes ->
                        if (isMapped(aes)) {
                            myShortLabels.add(getMappedData(aes).label)
                        }
                    }

            initTooltipSpecs()
        }

        private fun initTooltipSpecs() {
            aesTipLayoutHints().forEach { (aes, hint) ->
                applyTipLayoutHint(
                    aes = listOf(aes),
                    layoutHint = hint,
                    isOutlier = true
                )
            }

            addAesTooltipSpec()

            addAxisTooltipSpec()
        }

        private fun hitIndex(): Int {
            return myGeomTarget.hitIndex
        }

        private fun tipLayoutHint(): TipLayoutHint {
            return myGeomTarget.tipLayoutHint
        }

        private fun aesTipLayoutHints(): Map<Aes<*>, TipLayoutHint> {
            return myGeomTarget.aesTipLayoutHints
        }

        private fun addAxisTooltipSpec() {
            for (aes in myAxisAes) {
                if (isAxisTooltipAllowed(aes)) {
                    val layoutHint = createHintForAxis(aes)
                    val text = makeText(listOf(aes))
                    tooltipSpecs.add(
                        TooltipSpec(
                            layoutHint = layoutHint,
                            lines = text,
                            fill = layoutHint.color!!,
                            isOutlier = true
                        )
                    )
                }
            }
        }

        private fun isAxisTooltipAllowed(aes: Aes<*>): Boolean {
            if (!isMapped(aes)) {
                return false
            }

            val label = getMappedData(aes).label
            return if (MAP_COORDINATE_NAMES.contains(label)) {
                false
            } else isVariableContinuous(aes)

        }

        private fun addAesTooltipSpec() {
            val aesListForTooltip = ArrayList(myAesWithoutHint)

            removeDiscreteDuplicatedMappings(aesListForTooltip)

            applyTipLayoutHint(aes = aesListForTooltip, layoutHint = tipLayoutHint(), isOutlier = false)
        }

        private fun removeDiscreteDuplicatedMappings(aesWithoutHint: MutableList<Aes<*>>) {
            if (aesWithoutHint.isEmpty()) {
                return
            }

            val mappingsToShow = HashMap<String, Pair<Aes<*>, MappedData<*>>>()
            for (aes in aesWithoutHint) {
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

            aesWithoutHint.clear()
            mappingsToShow.values.forEach { pair -> aesWithoutHint.add(pair.first) }
        }

        private fun isVariableContinuous(aes: Aes<*>): Boolean {
            if (!isMapped(aes)) {
                return false
            }

            val xData = getMappedData(aes)
            return xData.isContinuous
        }

        private fun createHintForAxis(aes: Aes<*>): TipLayoutHint {
            if (aes === Aes.X) {
                return TipLayoutHint.xAxisTooltip(DoubleVector(tipLayoutHint().coord!!.x, axisOrigin.y),
                    AXIS_TOOLTIP_COLOR
                )
            }

            if (aes === Aes.Y) {
                return TipLayoutHint.yAxisTooltip(DoubleVector(axisOrigin.x, tipLayoutHint().coord!!.y),
                    AXIS_TOOLTIP_COLOR
                )
            }

            throw IllegalArgumentException("Not an axis aes: $aes")
        }

        private fun applyTipLayoutHint(aes: List<Aes<*>>, layoutHint: TipLayoutHint, isOutlier: Boolean) {
            if (aes.isEmpty()) {
                return
            }

            val text = makeText(aes)
            val fill = layoutHint.color ?: tipLayoutHint().color!!
            tooltipSpecs.add(TooltipSpec(layoutHint, text, fill, isOutlier))
            myAesWithoutHint.removeAll(aes)
        }

        private fun format(mappedData: MappedData<*>): String {
            if (mappedData.label.isEmpty()) {
                return mappedData.value
            }

            return if (myShortLabels.contains(mappedData.label)) {
                mappedData.value
            } else mappedData.label + ": " + mappedData.value

        }

        private fun makeText(aesList: List<Aes<*>>): List<String> {
            val lines = ArrayList<String>()

            for (aes in aesList) {
                if (isMapped(aes)) {
                    val mappedData = getMappedData(aes)
                    val string = format(mappedData)
                    if (!lines.contains(string)) {
                        lines.add(string)
                    }
                }
            }

            return lines
        }

        private fun isMapped(aes: Aes<*>): Boolean {
            return myDataAccess.isMapped(aes)
        }

        private fun <T> getMappedData(aes: Aes<T>): MappedData<T> {
            return myDataAccess.getMappedData(aes, hitIndex())
        }
    }

    companion object {
        private val MAP_COORDINATE_NAMES = setOf(
                POINT_X,
                POINT_X1,
                POINT_Y,
                POINT_Y1)

        val AXIS_TOOLTIP_COLOR = Color.GRAY
    }
}
