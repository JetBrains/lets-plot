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
import jetbrains.datalore.plot.builder.tooltip.MappingValue
import jetbrains.datalore.plot.builder.tooltip.TooltipLine

class GeomInteraction(builder: GeomInteractionBuilder) :
    ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private var myTooltipLines: List<TooltipLine> = builder.tooltipLines

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(dataAccess: MappedDataAccess, dataFrame: DataFrame): ContextualMapping {
        return createContextualMapping(
            myTooltipLines,
            dataAccess,
            dataFrame
        )
    }

    companion object {
        fun createContextualMapping(
            aesListForTooltip: List<Aes<*>>,
            axisAes: List<Aes<*>>,
            outliers: List<Aes<*>>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame
        ): ContextualMapping {
            val defaultTooltipLines = GeomInteractionBuilder.defaultValueSourceTooltipLines(
                aesListForTooltip,
                axisAes,
                outliers
            )
            return createContextualMapping(defaultTooltipLines, dataAccess, dataFrame)
        }

        private fun createContextualMapping(
            tooltipLines: List<TooltipLine>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame
        ): ContextualMapping {
            val dataContext = DataContext(dataFrame = dataFrame, mappedDataAccess = dataAccess)

            val mappedTooltipLines = tooltipLines.filter { line ->
                val dataAesList = line.fields.filterIsInstance<MappingValue>()
                dataAesList.all { mappedAes -> dataAccess.isMapped(mappedAes.aes) }
            }
            mappedTooltipLines.forEach { it.setDataContext(dataContext) }

            return ContextualMapping(dataContext, mappedTooltipLines)
        }
    }
}
