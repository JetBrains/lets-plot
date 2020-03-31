/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.*
import jetbrains.datalore.plot.base.interact.MappedDataAccess

class GeomInteraction(builder: GeomInteractionBuilder) :
    ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private val myAesListForTooltip: List<Aes<*>> = builder.aesListForTooltip
    private val myAxisAes: List<Aes<*>> = builder.axisAesListForTooltip

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping {
        return createContextualMapping(
            myAesListForTooltip,
            myAxisAes,
            dataAccess
        )
    }

    companion object {
        fun createContextualMapping(aesListForTooltip: List<Aes<*>>,
                                    axisAes: List<Aes<*>>,
                                    dataAccess: MappedDataAccess
        ): ContextualMapping {

            val showInTip = aesListForTooltip.filter(dataAccess::isMapped)
            return ContextualMapping(showInTip, axisAes, dataAccess)
        }
    }
}
