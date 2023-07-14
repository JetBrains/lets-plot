/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.MappedDataAccess
import jetbrains.datalore.plot.builder.tooltip.data.MappingField
import jetbrains.datalore.plot.builder.tooltip.TooltipLine
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification.TooltipProperties
import jetbrains.datalore.plot.builder.tooltip.data.ValueSource
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMappingProvider

class GeomInteraction(builder: GeomInteractionBuilder) :
    ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private val myTooltipLines: List<TooltipLine> = builder.tooltipLines
    private val myTooltipProperties: TooltipProperties = builder.tooltipProperties
    private val myIgnoreInvisibleTargets = builder.ignoreInvisibleTargets
    private val myIsCrosshairEnabled: Boolean = builder.isCrosshairEnabled
    private val myTooltipTitle: TooltipLine? = builder.tooltipTitle

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(
        dataAccess: MappedDataAccess,
        dataFrame: DataFrame
    ): ContextualMapping {
        return createContextualMapping(
            myTooltipLines.map(::TooltipLine),  // clone tooltip lines to not share DataContext between plots when facet is used
            // (issue #247 - With facet_grid tooltip shows data from last plot on all plots)
            dataAccess,
            dataFrame,
            myTooltipProperties,
            myIgnoreInvisibleTargets,
            myIsCrosshairEnabled,
            myTooltipTitle?.let(::TooltipLine)
        )
    }

    companion object {
        // For tests
        fun createTestContextualMapping(
            aesListForTooltip: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
            axisAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
            sideTooltipAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
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
                defaultTooltipLines,
                dataAccess,
                dataFrame,
                TooltipProperties.NONE,
                ignoreInvisibleTargets = false,
                isCrosshairEnabled = false,
                tooltipTitle = null
            )
        }

        private fun createContextualMapping(
            tooltipLines: List<TooltipLine>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame,
            tooltipProperties: TooltipProperties,
            ignoreInvisibleTargets: Boolean,
            isCrosshairEnabled: Boolean,
            tooltipTitle: TooltipLine?
        ): ContextualMapping {
            val mappedTooltipLines = tooltipLines.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingField>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedTooltipLines.forEach { it.initDataContext(dataFrame, dataAccess) }

            val hasGeneralTooltip = mappedTooltipLines.any { line ->
                line.fields.none(ValueSource::isSide)
            }
            val hasAxisTooltip = mappedTooltipLines.any { line ->
                line.fields.any(ValueSource::isAxis)
            }

            tooltipTitle?.initDataContext(dataFrame, dataAccess)

            return ContextualMapping(
                mappedTooltipLines,
                tooltipProperties.anchor,
                tooltipProperties.minWidth,
                ignoreInvisibleTargets,
                hasGeneralTooltip,
                hasAxisTooltip,
                isCrosshairEnabled,
                tooltipTitle
            )
        }
    }
}
