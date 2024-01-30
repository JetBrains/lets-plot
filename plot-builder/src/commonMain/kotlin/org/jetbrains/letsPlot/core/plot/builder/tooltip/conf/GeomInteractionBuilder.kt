/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.tooltip.LinePattern
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TooltipSpecification
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteractionBuilderUtil.createTooltipLines

class GeomInteractionBuilder constructor(
    val locatorLookupSpace: LookupSpace,
    val locatorLookupStrategy: LookupStrategy,
    private val tooltipAes: List<Aes<*>>,
    private val tooltipAxisAes: List<Aes<*>>,
    private val sideTooltipAes: List<Aes<*>>,
) {

    private var myUserTooltipSpec: TooltipSpecification = TooltipSpecification.defaultTooltip()

    var ignoreInvisibleTargets: Boolean = false
        private set

    var tooltipConstants: Map<Aes<*>, Any>? = null
        private set

    var isCrosshairEnabled: Boolean = false
        private set

    val tooltipLines: List<LinePattern>
        get() = createTooltipLines(
            myUserTooltipSpec,
            tooltipAes = tooltipAes,
            tooltipAxisAes = tooltipAxisAes,
            sideTooltipAes = sideTooltipAes,
            tooltipConstantAes = tooltipConstants
        )

    val tooltipProperties: TooltipSpecification.TooltipProperties
        get() = myUserTooltipSpec.tooltipProperties

    val tooltipTitle: LinePattern?
        get() = myUserTooltipSpec.tooltipTitle


    fun tooltipConstants(v: Map<Aes<*>, Any>): GeomInteractionBuilder {
        tooltipConstants = v
        return this
    }

    fun tooltipLinesSpec(v: TooltipSpecification): GeomInteractionBuilder {
        myUserTooltipSpec = v
        return this
    }

    fun enableCrosshair(v: Boolean): GeomInteractionBuilder {
        isCrosshairEnabled = v
        return this
    }

    fun ignoreInvisibleTargets(v: Boolean): GeomInteractionBuilder {
        ignoreInvisibleTargets = v
        return this
    }

    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }


    class DemoAndTest(
        private val supportedAes: List<Aes<*>>,
        private val axisAes: List<Aes<*>>? = null,
    ) {
        fun xUnivariateFunction(lookupStrategy: LookupStrategy): GeomInteractionBuilder {
            return createBuilder(GeomTooltipSetup.xUnivariateFunction(lookupStrategy))
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
                sideTooltipAes = emptyList()
            )
        }
    }
}
