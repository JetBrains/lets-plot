package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.*
import jetbrains.datalore.plot.base.interact.MappedDataAccess

class GeomInteraction(builder: jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder) :
    jetbrains.datalore.plot.builder.interact.ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private val myDisplayableAes: List<Aes<*>> = builder.displayableAes
    private val myAxisTooltipEnabled: Boolean = builder.isAxisTooltipEnabled
    private val myAxisAes: List<Aes<*>> = builder.axisAes

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping {
        return jetbrains.datalore.plot.builder.interact.GeomInteraction.Companion.createContextualMapping(
            myDisplayableAes,
            if (myAxisTooltipEnabled) myAxisAes else emptyList(),
            dataAccess
        )
    }

    companion object {
        fun createContextualMapping(displayableAes: List<Aes<*>>,
                                    axisAes: List<Aes<*>>,
                                    dataAccess: MappedDataAccess
        ): ContextualMapping {

            val showInTip = ArrayList<Aes<*>>()
            for (aes in displayableAes) {
                if (dataAccess.isMapped(aes)) {
                    showInTip.add(aes)
                }
            }

            return ContextualMapping(showInTip, axisAes, dataAccess)
        }
    }
}
