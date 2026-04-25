/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMappingProvider
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource

class GeomInteraction(
    private val tooltipBehavior: TooltipBehavior,
    val tooltipLines: List<LinePattern>,
) : ContextualMappingProvider {

    fun createLookupSpec(): LookupSpec {
        return tooltipBehavior.lookupSpec
    }

    override fun createContextualMapping(
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame
    ): ContextualMapping {
        return createContextualMapping(
            tooltipBehavior.tooltipTitle?.let(::LinePattern),  // clone tooltip lines to not share DataContext between plots when facet is used
            // (issue #247 - With facet_grid tooltip shows data from last plot on all plots)
            tooltipLines.map(::LinePattern),
            dataAccess,
            dataFrame,
            tooltipBehavior.anchor,
            tooltipBehavior.minWidth,
            tooltipBehavior.isCrosshairEnabled,
            tooltipBehavior.tooltipGroup
        )
    }

    companion object {
        fun create(
            tooltipBehavior: TooltipBehavior,
            tooltipAes: List<Aes<*>>,
            tooltipAxisAes: List<Aes<*>>,
            sideTooltipAes: List<Aes<*>>,
            tooltipConstants: Map<Aes<*>, Any>? = null,
        ): GeomInteraction {
            return GeomInteraction(
                tooltipBehavior = tooltipBehavior,
                tooltipLines = GeomInteractionUtil.createTooltipLines(
                    tooltipBehavior,
                    tooltipAes = tooltipAes,
                    tooltipAxisAes = tooltipAxisAes,
                    sideTooltipAes = sideTooltipAes,
                    tooltipConstantAes = tooltipConstants
                )
            )
        }

        // For tests
        fun createTestContextualMapping(
            aesListForTooltip: List<Aes<*>>,
            axisAes: List<Aes<*>>,
            sideTooltipAes: List<Aes<*>>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            userDefinedValueSources: List<ValueSource>? = null
        ): ContextualMapping {
            val defaultTooltipLines = GeomInteractionUtil.defaultValueSourceTooltipLines(
                aesListForTooltip,
                axisAes,
                sideTooltipAes,
                userDefinedValueSources
            )
            return createContextualMapping(
                tooltipTitle = null,
                defaultTooltipLines,
                dataAccess,
                dataFrame,
                anchor = null,
                minWidth = null,
                isCrosshairEnabled = false,
                tooltipGroup = null
            )
        }

        private fun createContextualMapping(
            tooltipTitle: LinePattern?,
            tooltipLines: List<LinePattern>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            anchor: TooltipAnchor?,
            minWidth: Double?,
            isCrosshairEnabled: Boolean,
            tooltipGroup: String?
        ): ContextualMapping {
            val mappedTooltipLines = LinePattern.prepareMappedLines(tooltipLines, dataAccess, dataFrame)

            tooltipTitle?.initDataContext(dataFrame, dataAccess)

            return ContextualMapping(
                mappedTooltipLines,
                anchor,
                minWidth,
                isCrosshairEnabled,
                tooltipGroup,
                tooltipTitle
            )
        }
    }

    class DemoAndTest(
        private val supportedAes: List<Aes<*>>,
        private val axisAes: List<Aes<*>>? = null,
    ) {
        fun xUnivariateFunction(lookupStrategy: LookupStrategy): GeomInteraction {
            return createInteraction(
                TooltipBehavior(
                    lookupSpec = LookupSpec(
                        LookupSpace.X,
                        lookupStrategy
                    ),
                    axisAesFromFunctionKind = listOf(Aes.X),
                    axisTooltipEnabled = true,
                    isCrosshairEnabled = false,
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

        fun bivariateFunction(area: Boolean): GeomInteraction {
            val lookupStrategy = if (area) {
                LookupStrategy.HOVER
            } else {
                LookupStrategy.NEAREST
            }
            return createInteraction(
                TooltipBehavior(
                    lookupSpec = LookupSpec(
                        LookupSpace.XY,
                        lookupStrategy
                    ),
                    axisAesFromFunctionKind = listOf(Aes.X, Aes.Y),
                    axisTooltipEnabled = !area,
                    isCrosshairEnabled = false,
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

        private fun createInteraction(tooltipBehavior: TooltipBehavior): GeomInteraction {
            return create(
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
