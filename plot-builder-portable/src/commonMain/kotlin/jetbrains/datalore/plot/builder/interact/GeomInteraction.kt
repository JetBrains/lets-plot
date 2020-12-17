/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.*
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipAnchor
import jetbrains.datalore.plot.builder.tooltip.MappingValue
import jetbrains.datalore.plot.builder.tooltip.TooltipLine
import jetbrains.datalore.plot.builder.tooltip.ValueSource

class GeomInteraction(builder: GeomInteractionBuilder) :
    ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private val myTooltipLines: List<TooltipLine> = builder.tooltipLines
    private val myIgnoreInvisibleTargets = builder.isIgnoringInvisibleTargets()

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame,
        tooltipAnchor: TooltipAnchor?,
        tooltipMinWidth: Double?
    ): ContextualMapping {
        return createContextualMapping(
            myTooltipLines.map(::TooltipLine),  // clone tooltip lines to not share DataContext between plots when facet is used
                                                // (issue #247 - With facet_grid tooltip shows data from last plot on all plots)
            dataAccess,
            dataFrame,
            tooltipAnchor,
            tooltipMinWidth,
            myIgnoreInvisibleTargets
        )
    }

    companion object {
        // For tests
        fun createTestContextualMapping(
            aesListForTooltip: List<Aes<*>>,
            axisAes: List<Aes<*>>,
            outliers: List<Aes<*>>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            userDefinedValueSources: List<ValueSource>? = null
        ): ContextualMapping {
            val defaultTooltipLines = GeomInteractionBuilder.defaultValueSourceTooltipLines(
                aesListForTooltip,
                axisAes,
                outliers,
                userDefinedValueSources
            )
            return createContextualMapping(
                defaultTooltipLines,
                dataAccess,
                dataFrame,
                tooltipAnchor = null,
                tooltipMinWidth = null,
                ignoreInvisibleTargets = false
            )
        }

        private fun createContextualMapping(
            tooltipLines: List<TooltipLine>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            tooltipAnchor: TooltipAnchor?,
            tooltipMinWidth: Double?,
            ignoreInvisibleTargets: Boolean
        ): ContextualMapping {
            val dataContext = DataContext(dataFrame = dataFrame, mappedDataAccess = dataAccess)

            val mappedTooltipLines = tooltipLines.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingValue>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedTooltipLines.forEach { it.initDataContext(dataContext) }

            return ContextualMapping(mappedTooltipLines, tooltipAnchor, tooltipMinWidth, ignoreInvisibleTargets)
        }
    }
}
