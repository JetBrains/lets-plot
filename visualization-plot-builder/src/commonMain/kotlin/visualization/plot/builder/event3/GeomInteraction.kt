package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.*
import jetbrains.datalore.visualization.plot.base.render.Aes

class GeomInteraction(builder: GeomInteractionBuilder) : ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy
    private val myDisplayableAes: List<Aes<*>> = builder.displayableAes
    private val myAxisTooltipEnabled: Boolean = builder.isAxisTooltipEnabled
    private val myAxisAes: List<Aes<*>> = builder.axisAes

    fun createLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping {
        return createContextualMapping(
                myDisplayableAes,
                if (myAxisTooltipEnabled) myAxisAes else emptyList(),
                dataAccess)
    }

    companion object {
        fun createContextualMapping(displayableAes: List<Aes<*>>,
                                    axisAes: List<Aes<*>>,
                                    dataAccess: MappedDataAccess): ContextualMapping {

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
