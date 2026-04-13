/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.GeomInteractionBuilderUtil.createTooltipLines
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern

class GeomInteractionBuilder constructor(
    tooltipBehavior: TooltipBehavior,
    private val tooltipAes: List<Aes<*>>,
    private val tooltipAxisAes: List<Aes<*>>,
    private val sideTooltipAes: List<Aes<*>>,
) {
    var tooltipBehavior: TooltipBehavior = tooltipBehavior
        private set

    var tooltipConstants: Map<Aes<*>, Any>? = null
        private set

    val tooltipLines: List<LinePattern>
        get() = createTooltipLines(
            tooltipBehavior,
            tooltipAes = tooltipAes,
            tooltipAxisAes = tooltipAxisAes,
            sideTooltipAes = sideTooltipAes,
            tooltipConstantAes = tooltipConstants
        )

    val tooltipTitle: LinePattern?
        get() = tooltipBehavior.tooltipTitle

    fun tooltipConstants(v: Map<Aes<*>, Any>): GeomInteractionBuilder {
        tooltipConstants = v
        return this
    }

    fun ignoreInvisibleTargets(v: Boolean): GeomInteractionBuilder {
        tooltipBehavior = tooltipBehavior.copy(ignoreInvisibleTargets = v)
        return this
    }

    fun build(): GeomInteraction {
        return GeomInteraction(this)
    }


    class DemoAndTest(
        private val supportedAes: List<Aes<*>>,
        private val axisAes: List<Aes<*>>? = null,
    ) {
        fun xUnivariateFunction(lookupStrategy: org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy): GeomInteractionBuilder {
            return createBuilder(
                TooltipBehavior(
                    lookupSpec = org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpec(
                        org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace.X,
                        lookupStrategy
                    ),
                    axisAesFromFunctionKind = listOf(Aes.X),
                    axisTooltipEnabled = true,
                    isCrosshairEnabled = false,
                    ignoreInvisibleTargets = false,
                    valueSources = emptyList(),
                    tooltipLinePatterns = null,
                    anchor = null,
                    minWidth = null,
                    tooltipTitle = null,
                    disableSplitting = false,
                    tooltipGroup = null,
                )
            )
        }

        fun bivariateFunction(area: Boolean): GeomInteractionBuilder {
            val lookupStrategy = if (area) {
                org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy.HOVER
            } else {
                org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy.NEAREST
            }
            return createBuilder(
                TooltipBehavior(
                    lookupSpec = org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpec(
                        org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace.XY,
                        lookupStrategy
                    ),
                    axisAesFromFunctionKind = listOf(Aes.X, Aes.Y),
                    axisTooltipEnabled = !area,
                    isCrosshairEnabled = false,
                    ignoreInvisibleTargets = false,
                    valueSources = emptyList(),
                    tooltipLinePatterns = null,
                    anchor = null,
                    minWidth = null,
                    tooltipTitle = null,
                    disableSplitting = false,
                    tooltipGroup = null,
                )
            )
        }

        private fun createBuilder(tooltipBehavior: TooltipBehavior): GeomInteractionBuilder {
            return GeomInteractionBuilder(
                tooltipBehavior = tooltipBehavior,
                tooltipAes = supportedAes - tooltipBehavior.axisAesFromFunctionKind,
                tooltipAxisAes = axisAes
                    ?: if (!tooltipBehavior.axisTooltipEnabled) emptyList()
                    else tooltipBehavior.axisAesFromFunctionKind,
                sideTooltipAes = emptyList()
            )
        }
    }
}
