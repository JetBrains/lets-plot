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
    val locatorLookupSpace: LookupSpace,
    val locatorLookupStrategy: LookupStrategy,
    private val tooltipAes: List<Aes<*>>,
    private val tooltipAxisAes: List<Aes<*>>,
    private val tooltipOutlierAes: List<Aes<*>>,
) {

    private var myIgnoreInvisibleTargets: Boolean = false
    private var myTooltipConstantsAesList: Map<Aes<*>, Any>? = null
    private var myUserTooltipSpec: TooltipSpecification? = null
    private var myIsCrosshairEnabled: Boolean = false
    private var isAxisTooltipEnabled: Boolean = true

    val tooltipLines: List<TooltipLine>
        get() = createTooltipLines(
            myUserTooltipSpec,
            tooltipAes = tooltipAes,
            tooltipAxisAes = tooltipAxisAes,
            sideTooltipAes = tooltipOutlierAes,
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


    fun showAxisTooltip(v: Boolean): GeomInteractionBuilder {
        isAxisTooltipEnabled = v
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

    fun ignoreInvisibleTargets(isTrue: Boolean): GeomInteractionBuilder {
        myIgnoreInvisibleTargets = isTrue
        return this
    }

    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }


    class DemoAndTest(
        private val supportedAes: List<Aes<*>>,
        private val axisAes: List<Aes<*>>? = null,
    ) {
        fun univariateFunction(lookupStrategy: LookupStrategy): GeomInteractionBuilder {
            return createBuilder(GeomTooltipSetup.univariateFunction(lookupStrategy))
        }

        fun bivariateFunction(area: Boolean): GeomInteractionBuilder {
            return createBuilder(GeomTooltipSetup.bivariateFunction(area))
        }

        private fun createBuilder(geomTooltipSetup: GeomTooltipSetup): GeomInteractionBuilder {
            return GeomInteractionBuilder(
                locatorLookupSpace = geomTooltipSetup.locatorLookupSpace,
                locatorLookupStrategy = geomTooltipSetup.locatorLookupStrategy,
                tooltipAes = supportedAes - geomTooltipSetup.axisAesFromFunctionKind,
                tooltipAxisAes = axisAes
                    ?: if (!geomTooltipSetup.axisTooltipEnabled) emptyList()
                    else geomTooltipSetup.axisAesFromFunctionKind,
                tooltipOutlierAes = emptyList()
            )
        }
    }
}
