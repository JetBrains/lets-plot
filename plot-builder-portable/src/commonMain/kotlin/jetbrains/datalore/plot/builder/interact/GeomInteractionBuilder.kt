/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupSpace
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupStrategy

class GeomInteractionBuilder(private val mySupportedAesList: List<Aes<*>>) {
    lateinit var locatorLookupSpace: LookupSpace
        private set
    lateinit var locatorLookupStrategy: LookupStrategy
        private set
    private var myAxisTooltipVisibilityFromFunctionKind: Boolean = false
    private var myAxisTooltipVisibilityFromConfig: Boolean? = null
    private var myAxisAesFromFunctionKind: List<Aes<*>>? = null
    private lateinit var myToolipAxisAes: List<Aes<*>>
    private lateinit var myTooltipAes: List<Aes<*>>

    val axisAesListForTooltip: List<Aes<*>>
        get() = myToolipAxisAes

    val aesListForTooltip: List<Aes<*>>
        get() = myTooltipAes

    val getAxisFromFunctionKind: List<Aes<*>>
        get() = myAxisAesFromFunctionKind ?: emptyList()

    val isAxisTooltipEnabled: Boolean
        get() = if (myAxisTooltipVisibilityFromConfig == null)
            myAxisTooltipVisibilityFromFunctionKind
        else
            myAxisTooltipVisibilityFromConfig!!

    fun showAxisTooltip(isTrue: Boolean): GeomInteractionBuilder {
        myAxisTooltipVisibilityFromConfig = isTrue
        return this
    }

    fun tooltipAes(aes: List<Aes<*>>): GeomInteractionBuilder {
        myTooltipAes = aes
        return this
    }

    fun axisAes(axisAes: List<Aes<*>>): GeomInteractionBuilder {
        myToolipAxisAes = axisAes
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
        myToolipAxisAes = if (!isAxisTooltipEnabled) emptyList() else getAxisFromFunctionKind
        myTooltipAes = mySupportedAesList - getAxisFromFunctionKind
    }

    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }

    companion object {
        const val AREA_GEOM = true
        const val NON_AREA_GEOM = false

        private val AES_X = listOf(Aes.X)
        private val AES_XY = listOf(Aes.X, Aes.Y)
    }
}
