/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.tooltip.MappedAes
import jetbrains.datalore.plot.builder.tooltip.TooltipLinesSpecification
import jetbrains.datalore.plot.builder.tooltip.ValueSource
import jetbrains.datalore.plot.builder.tooltip.ValueSourceTooltipLine

class GeomInteractionBuilder(private val mySupportedAesList: List<Aes<*>>) {
    lateinit var locatorLookupSpace: LookupSpace
        private set
    lateinit var locatorLookupStrategy: LookupStrategy
        private set
    private var myAxisTooltipVisibilityFromFunctionKind: Boolean = false
    private var myAxisTooltipVisibilityFromConfig: Boolean? = null
    private var myAxisAesFromFunctionKind: List<Aes<*>>? = null
    private lateinit var myTooltipAxisAes: List<Aes<*>>
    private lateinit var myTooltipAes: List<Aes<*>>
    private lateinit var myTooltipOutlierAesList: List<Aes<*>>
    private lateinit var myUserTooltipLinesSpec: TooltipLinesSpecification

    val getAxisFromFunctionKind: List<Aes<*>>
        get() = myAxisAesFromFunctionKind ?: emptyList()

    val isAxisTooltipEnabled: Boolean
        get() = if (myAxisTooltipVisibilityFromConfig == null)
            myAxisTooltipVisibilityFromFunctionKind
        else
            myAxisTooltipVisibilityFromConfig!!

    val tooltipLines: List<ValueSourceTooltipLine>
        get() = prepareTooltipValueSources()

    fun showAxisTooltip(isTrue: Boolean): GeomInteractionBuilder {
        myAxisTooltipVisibilityFromConfig = isTrue
        return this
    }

    fun tooltipAes(aes: List<Aes<*>>): GeomInteractionBuilder {
        myTooltipAes = aes
        return this
    }

    fun axisAes(axisAes: List<Aes<*>>): GeomInteractionBuilder {
        myTooltipAxisAes = axisAes
        return this
    }

    fun tooltipOutliers(aes: List<Aes<*>>): GeomInteractionBuilder {
        myTooltipOutlierAesList = aes
        return this
    }

    fun tooltipLinesSpec(tooltipLinesSpec: TooltipLinesSpecification): GeomInteractionBuilder {
        myUserTooltipLinesSpec = tooltipLinesSpec
        return this
    }

    fun multilayerLookupStrategy(): GeomInteractionBuilder {
        locatorLookupStrategy = LookupStrategy.NEAREST
        locatorLookupSpace = LookupSpace.XY
        return this
    }

    fun univariateFunction(lookupStrategy: LookupStrategy): GeomInteractionBuilder {
        myAxisAesFromFunctionKind = AES_X
        locatorLookupStrategy = lookupStrategy
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.X
        initDefaultTooltips()
        return this
    }

    fun bivariateFunction(area: Boolean): GeomInteractionBuilder {
        myAxisAesFromFunctionKind = AES_XY

        if (area) {
            locatorLookupStrategy = LookupStrategy.HOVER
            myAxisTooltipVisibilityFromFunctionKind = false
        } else {
            locatorLookupStrategy = LookupStrategy.NEAREST
            myAxisTooltipVisibilityFromFunctionKind = true
        }
        locatorLookupSpace = LookupSpace.XY
        initDefaultTooltips()
        return this
    }

    fun none(): GeomInteractionBuilder {
        myAxisAesFromFunctionKind = ArrayList(mySupportedAesList)
        locatorLookupStrategy = LookupStrategy.NONE
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.NONE
        initDefaultTooltips()
        return this
    }

    private fun initDefaultTooltips() {
        myTooltipAxisAes = if (!isAxisTooltipEnabled) emptyList() else getAxisFromFunctionKind
        myTooltipAes = mySupportedAesList - getAxisFromFunctionKind
        myTooltipOutlierAesList = emptyList()
        myUserTooltipLinesSpec = TooltipLinesSpecification.defaultTooltipLines()
    }

    private fun prepareTooltipValueSources(): List<ValueSourceTooltipLine> {

        return when {
            myUserTooltipLinesSpec.tooltipLinePatterns == null -> {
                // No user list => use default tooltips
                defaultValueSourceTooltipLines(myTooltipAes, myTooltipAxisAes, myTooltipOutlierAesList, myUserTooltipLinesSpec.valueSources)
            }
            myUserTooltipLinesSpec.tooltipLinePatterns!!.isEmpty() -> {
                // User list is empty => not show tooltips
                emptyList()
            }
            else -> {
                // Form value sources: user list + axis + outliers
                val geomOutliers = myTooltipOutlierAesList.toMutableList()

                val userTooltipLines = myUserTooltipLinesSpec.tooltipLinePatterns!!.map { line ->
                    if (line.data.size == 1 && line.data.single() is MappedAes && (line.data.single() as MappedAes).aes in myTooltipOutlierAesList) {
                        val valueSource = line.data.single() as MappedAes
                            // use formatted mapped aes as outlier and exclude its aes from the outlier aes list
                            geomOutliers.remove(valueSource.aes)
                            ValueSourceTooltipLine(
                                label = line.label,
                                linePattern = line.linePattern,
                                data = listOf(valueSource.toOutlier())
                            )
                    } else {
                        line
                    }
                }
                val axisValueSources = myTooltipAxisAes.map { aes -> MappedAes(aes, isOutlier = true, isAxis = true) }
                val geomOutlierValueSources = geomOutliers.map { aes ->
                    val formatted = myUserTooltipLinesSpec.valueSources.filterIsInstance<MappedAes>().find { it.aes == aes }
                    formatted?.toOutlier() ?: MappedAes(aes, isOutlier = true)
                }

                userTooltipLines + (axisValueSources + geomOutlierValueSources).map { valueSource ->
                    ValueSourceTooltipLine.defaultLineForValueSource(valueSource)
                }
            }
        }
    }

    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }

    companion object {
        const val AREA_GEOM = true
        const val NON_AREA_GEOM = false

        private val AES_X = listOf(Aes.X)
        private val AES_XY = listOf(Aes.X, Aes.Y)

        fun defaultValueSourceTooltipLines(
            aesListForTooltip: List<Aes<*>>,
            axisAes: List<Aes<*>>,
            outliers: List<Aes<*>>,
            formattedList: List<ValueSource>? = null
        ): List<ValueSourceTooltipLine> {
            val axisValueSources = axisAes.map { aes -> MappedAes(aes, isOutlier = true, isAxis = true) }
            val outlierValueSources = outliers.map { aes ->
                val formatted = formattedList?.filterIsInstance<MappedAes>()?.find { it.aes == aes }
                formatted?.toOutlier() ?: MappedAes(aes, isOutlier = true)
            }
            val aesValueSources = aesListForTooltip.map { aes ->
                val formatted = formattedList?.filterIsInstance<MappedAes>()?.find { it.aes == aes }
                formatted ?: MappedAes(aes)
            }
            return (aesValueSources + axisValueSources + outlierValueSources).map { valueSource ->
                ValueSourceTooltipLine.defaultLineForValueSource(valueSource)
            }
        }
    }
}
