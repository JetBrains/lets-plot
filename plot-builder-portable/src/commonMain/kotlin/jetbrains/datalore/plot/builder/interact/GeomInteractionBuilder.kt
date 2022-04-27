/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilderUtil.createTooltipLines
import jetbrains.datalore.plot.builder.tooltip.TooltipLine
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification

class GeomInteractionBuilder constructor(
    private val supportedAesList: List<Aes<*>>
) {

    private var myIgnoreInvisibleTargets: Boolean = false
    private var myAxisTooltipVisibilityFromFunctionKind: Boolean = false
    private var myAxisTooltipVisibilityFromConfig: Boolean? = null
    private var myAxisAesFromFunctionKind: List<Aes<*>>? = null
    private lateinit var myTooltipAxisAes: List<Aes<*>>
    private lateinit var myTooltipAes: List<Aes<*>>
    private lateinit var myTooltipOutlierAesList: List<Aes<*>>
    private var myTooltipConstantsAesList: Map<Aes<*>, Any>? = null
    private var myUserTooltipSpec: TooltipSpecification? = null
    private var myIsCrosshairEnabled: Boolean = false

    // ToDo: TMP public - used as intermediate builder result.
    val axisAesFromFunctionKind: List<Aes<*>>
        get() = myAxisAesFromFunctionKind ?: emptyList()


    lateinit var locatorLookupSpace: LookupSpace
        private set

    lateinit var locatorLookupStrategy: LookupStrategy
        private set

    val isAxisTooltipEnabled: Boolean
        get() = if (myAxisTooltipVisibilityFromConfig == null)
            myAxisTooltipVisibilityFromFunctionKind
        else
            myAxisTooltipVisibilityFromConfig!!

    val tooltipLines: List<TooltipLine>
        get() = createTooltipLines(
            myUserTooltipSpec,
            tooltipAes = myTooltipAes,
            tooltipAxisAes = myTooltipAxisAes,
            sideTooltipAes = myTooltipOutlierAesList,
            tooltipConstantAes = myTooltipConstantsAesList
        )

    val tooltipProperties: TooltipSpecification.TooltipProperties
        get() = myUserTooltipSpec?.tooltipProperties ?: TooltipSpecification.TooltipProperties.NONE

    val tooltipTitle: TooltipLine?
        get() = myUserTooltipSpec?.tooltipTitle

    val isCrosshairEnabled: Boolean
        get() = myIsCrosshairEnabled

    val ignoreInvisibleTargets: Boolean
        get() = myIgnoreInvisibleTargets


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

    fun tooltipConstants(constantsMap: Map<Aes<*>, Any>): GeomInteractionBuilder {
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
        myAxisAesFromFunctionKind = ArrayList(supportedAesList)
        locatorLookupStrategy = LookupStrategy.NONE
        myAxisTooltipVisibilityFromFunctionKind = true
        locatorLookupSpace = LookupSpace.NONE
        initDefaultTooltips()
        return this
    }

    private fun initDefaultTooltips() {
        myTooltipAxisAes = if (!isAxisTooltipEnabled) emptyList() else axisAesFromFunctionKind
        myTooltipAes = supportedAesList - axisAesFromFunctionKind
        myTooltipOutlierAesList = emptyList()
    }


    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }

    fun ignoreInvisibleTargets(isTrue: Boolean): GeomInteractionBuilder {
        myIgnoreInvisibleTargets = isTrue
        return this
    }

    companion object {
        const val AREA_GEOM = true
        const val NON_AREA_GEOM = false

        private val AES_X = listOf(Aes.X)
        private val AES_XY = listOf(Aes.X, Aes.Y)

    }
}
