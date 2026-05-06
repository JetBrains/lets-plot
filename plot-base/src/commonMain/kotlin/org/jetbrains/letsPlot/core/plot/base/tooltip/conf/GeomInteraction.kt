/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMappingProvider
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpec
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.MappedDataAccess
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource

class GeomInteraction(builder: GeomInteractionBuilder) :
    ContextualMappingProvider {

    private val tooltipBehavior: TooltipBehavior = builder.tooltipBehavior
    private val tooltipLines: List<LinePattern> = builder.tooltipLines
    private val tooltipTitle: LinePattern? = builder.tooltipTitle

    fun createLookupSpec(): LookupSpec {
        return tooltipBehavior.lookupSpec
    }

    override fun createContextualMapping(
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame
    ): ContextualMapping {
        return createContextualMapping(
            tooltipBehavior,
            tooltipTitle?.let(::LinePattern),  // clone tooltip lines to not share DataContext between plots when facet is used
            // (issue #247 - With facet_grid tooltip shows data from last plot on all plots)
            tooltipLines.map(::LinePattern),
            dataAccess,
            dataFrame,
        )
    }

    companion object {
        // For tests
        fun createTestContextualMapping(
            aesListForTooltip: List<Aes<*>>,
            axisAes: List<Aes<*>>,
            sideTooltipAes: List<Aes<*>>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            userDefinedValueSources: List<ValueSource>? = null
        ): ContextualMapping {
            val defaultTooltipLines = GeomInteractionBuilderUtil.defaultValueSourceTooltipLines(
                aesListForTooltip,
                axisAes,
                sideTooltipAes,
                userDefinedValueSources
            )
            return createContextualMapping(
                TooltipBehavior(
                    valueSources = emptyList(),
                    tooltipLinePatterns = null,
                    anchor = null,
                    minWidth = null,
                    tooltipTitle = null,
                    disableSplitting = false,
                    tooltipGroup = null,
                ),
                tooltipTitle = null,
                defaultTooltipLines,
                dataAccess,
                dataFrame,
            )
        }

        private fun createContextualMapping(
            tooltipBehavior: TooltipBehavior,
            tooltipTitle: LinePattern?,
            tooltipLines: List<LinePattern>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
        ): ContextualMapping {
            val mappedTooltipLines = LinePattern.prepareMappedLines(tooltipLines, dataAccess, dataFrame)

            tooltipTitle?.initDataContext(dataFrame, dataAccess)

            return ContextualMapping(tooltipBehavior, mappedTooltipLines, tooltipTitle)
        }
    }
}
