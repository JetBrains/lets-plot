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
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
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
            tooltipTitle?.let(::LinePattern),  // clone tooltip lines to not share DataContext between plots when facet is used
            // (issue #247 - With facet_grid tooltip shows data from last plot on all plots)
            tooltipLines.map(::LinePattern),
            dataAccess,
            dataFrame,
            tooltipBehavior.anchor,
            tooltipBehavior.minWidth,
            tooltipBehavior.ignoreInvisibleTargets,
            tooltipBehavior.isCrosshairEnabled,
            tooltipBehavior.tooltipGroup
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
                tooltipTitle = null,
                defaultTooltipLines,
                dataAccess,
                dataFrame,
                anchor = null,
                minWidth = null,
                ignoreInvisibleTargets = false,
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
            ignoreInvisibleTargets: Boolean,
            isCrosshairEnabled: Boolean,
            tooltipGroup: String?
        ): ContextualMapping {
            val mappedTooltipLines = LinePattern.prepareMappedLines(tooltipLines, dataAccess, dataFrame)

            tooltipTitle?.initDataContext(dataFrame, dataAccess)

            return ContextualMapping(
                mappedTooltipLines,
                anchor,
                minWidth,
                ignoreInvisibleTargets,
                isCrosshairEnabled,
                tooltipGroup,
                tooltipTitle
            )
        }
    }
}
