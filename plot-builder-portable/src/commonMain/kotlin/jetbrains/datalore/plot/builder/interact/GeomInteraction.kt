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
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.builder.tooltip.MappedAes

class GeomInteraction(builder: GeomInteractionBuilder) :
    ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private var myValueSourcesForTooltip: List<ValueSource> = builder.valueSourcesForTooltip

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(dataAccess: MappedDataAccess, dataFrame: DataFrame): ContextualMapping {
        return createContextualMapping(
            myValueSourcesForTooltip,
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
            val valueSources = GeomInteractionBuilder.defaultValueSourceList(
                aesListForTooltip,
                axisAes,
                outliers
            )
            return createContextualMapping(valueSources, dataAccess, dataFrame)
        }

        private fun createContextualMapping(
            tooltipValueSources: List<ValueSource>,
            dataAccess: MappedDataAccess,
            dataFrame: DataFrame
        ): ContextualMapping {
            val dataContext = DataContext(dataFrame = dataFrame, mappedDataAccess = dataAccess)

            val mappedValueSources = tooltipValueSources.filter { valueSource ->
                when (valueSource) {
                    is MappedAes -> dataAccess.isMapped(valueSource.aes)
                    else -> true
                }
            }
            mappedValueSources.forEach { it.setDataContext(dataContext) }

            return ContextualMapping(dataContext, mappedValueSources)
        }
    }
}
