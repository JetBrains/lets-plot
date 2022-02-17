/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification
import jetbrains.datalore.plot.builder.tooltip.TooltipLine
import jetbrains.datalore.plot.builder.tooltip.ValueSource
import jetbrains.datalore.plot.builder.tooltip.MappingValue
import jetbrains.datalore.plot.builder.tooltip.ConstantValue

class GeomInteractionBuilder(private val mySupportedAesList: List<Aes<*>>) {
    private var myIgnoreInvisibleTargets: Boolean = false
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
    private var myTooltipConstantsAesList: Map<Aes<*>, Any>? = null
    private var myUserTooltipSpec: TooltipSpecification? = null
    private var myIsCrosshairEnabled: Boolean = false

    val getAxisFromFunctionKind: List<Aes<*>>
        get() = myAxisAesFromFunctionKind ?: emptyList()

    val isAxisTooltipEnabled: Boolean
        get() = if (myAxisTooltipVisibilityFromConfig == null)
            myAxisTooltipVisibilityFromFunctionKind
        else
            myAxisTooltipVisibilityFromConfig!!

    val tooltipLines: List<TooltipLine>
        get() = prepareTooltipValueSources()

    val tooltipProperties: TooltipSpecification.TooltipProperties
        get() = myUserTooltipSpec?.tooltipProperties ?: TooltipSpecification.TooltipProperties.NONE

    val tooltipTitle: TooltipLine?
        get() = myUserTooltipSpec?.tooltipTitle

    val isCrosshairEnabled: Boolean
        get() = myIsCrosshairEnabled

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

    fun tooltipConstants(constantsMap:  Map<Aes<*>, Any>): GeomInteractionBuilder {
        myTooltipConstantsAesList = constantsMap
        return this
    }

    fun tooltipLinesSpec(tooltipSpec: TooltipSpecification): GeomInteractionBuilder {
        myUserTooltipSpec = tooltipSpec
        return this
    }

    fun setIsCrosshairEnabled(isTrue: Boolean): GeomInteractionBuilder {
        myIsCrosshairEnabled = isTrue
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
    }

    private fun prepareTooltipValueSources(): List<TooltipLine> {

        return when {
            myUserTooltipSpec == null -> {
                // No user tooltip specification => use default tooltips
                defaultValueSourceTooltipLines(
                    myTooltipAes,
                    myTooltipAxisAes,
                    myTooltipOutlierAesList,
                    userDefinedValueSources = null,
                    constantsMap = myTooltipConstantsAesList
                )
            }
            myUserTooltipSpec!!.useDefaultTooltips() -> {
                // No user line patterns => use default tooltips with the given formatted valueSources
                defaultValueSourceTooltipLines(
                    myTooltipAes,
                    myTooltipAxisAes,
                    myTooltipOutlierAesList,
                    myUserTooltipSpec!!.valueSources,
                    myTooltipConstantsAesList
                )
            }
            myUserTooltipSpec!!.hideTooltips() -> {
                // User list is empty => not show tooltips
                emptyList()
            }
            else -> {
                // Form value sources: user list + axis + outliers
                val geomOutliers = myTooltipOutlierAesList.toMutableList()

                // Remove outlier tooltip if the mappedAes is used in the general tooltip
                myUserTooltipSpec!!.tooltipLinePatterns!!.forEach { line ->
                    val userDataAesList = line.fields.filterIsInstance<MappingValue>().map { it.aes }
                    geomOutliers.removeAll(userDataAesList)
                }
                val axisValueSources = myTooltipAxisAes.map { aes ->
                    getMappingValueSource(aes, isOutlier = true, isAxis = true, myUserTooltipSpec!!.valueSources)
                }
                val geomOutlierValueSources = geomOutliers.map { aes ->
                    getMappingValueSource(aes, isOutlier = true, isAxis = false, myUserTooltipSpec!!.valueSources)
                }

                myUserTooltipSpec!!.tooltipLinePatterns!! + (axisValueSources + geomOutlierValueSources).map(TooltipLine.Companion::defaultLineForValueSource)
            }
        }
    }

    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }

    fun ignoreInvisibleTargets(isTrue: Boolean): GeomInteractionBuilder {
        myIgnoreInvisibleTargets = isTrue
        return this
    }

    fun isIgnoringInvisibleTargets(): Boolean {
        return myIgnoreInvisibleTargets
    }


    companion object {
        const val AREA_GEOM = true
        const val NON_AREA_GEOM = false

        private val AES_X = listOf(Aes.X)
        private val AES_XY = listOf(Aes.X, Aes.Y)

        private fun getMappingValueSource(
            aes: Aes<*>,
            isOutlier: Boolean,
            isAxis: Boolean,
            userDefinedValueSources: List<ValueSource>?
        ): ValueSource {
            val userDefined = userDefinedValueSources?.filterIsInstance<MappingValue>()?.find { it.aes == aes }
            return userDefined?.withFlags(isOutlier, isAxis) ?: MappingValue(aes, isOutlier = isOutlier, isAxis = isAxis)
        }

        fun defaultValueSourceTooltipLines(
            aesListForTooltip: List<Aes<*>>,
            axisAes: List<Aes<*>>,
            outliers: List<Aes<*>>,
            userDefinedValueSources: List<ValueSource>? = null,
            constantsMap: Map<Aes<*>, Any>? = null
        ): List<TooltipLine> {
            val axisValueSources = axisAes.map { aes ->
                getMappingValueSource(aes, isOutlier = true, isAxis = true, userDefinedValueSources)
            }
            val outlierValueSources = outliers.map { aes ->
                getMappingValueSource(aes, isOutlier = true, isAxis = false, userDefinedValueSources)
            }
            val aesValueSources = aesListForTooltip.map { aes ->
                getMappingValueSource(aes, isOutlier = false, isAxis = false, userDefinedValueSources)
            }
            val constantValues = constantsMap?.map { (_, value) -> ConstantValue(value, format = null) } ?: emptyList()
            return (aesValueSources + axisValueSources + outlierValueSources + constantValues).map(TooltipLine.Companion::defaultLineForValueSource)
        }
    }
}
