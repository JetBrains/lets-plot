package jetbrains.datalore.visualization.plot.gog.config.event3

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.*
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.event3.ContextualMappingProvider

class GeomTargetInteraction(builder: GeomInteractionBuilder) : ContextualMappingProvider {

    private val myLocatorLookupSpace: LookupSpace = builder.locatorLookupSpace!!
    private val myLocatorLookupStrategy: LookupStrategy = builder.locatorLookupStrategy!!
    private val myDisplayableAes: List<Aes<*>> = builder.displayableAes
    private val myAxisTooltipEnabled: Boolean = builder.isAxisTooltipEnabled
    private val myAxisAes: List<Aes<*>> = builder.axisAes!!

    fun createLocatorLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping {
        return createContextualMapping(
                myDisplayableAes,
                if (myAxisTooltipEnabled) myAxisAes else emptyList(),
                dataAccess)
    }

    companion object {
        internal val AXIS_TOOLTIP_COLOR = Color.GRAY

        fun createContextualMapping(displayableAes: List<Aes<*>>, axisAes: List<Aes<*>>, dataAccess: MappedDataAccess): ContextualMapping {
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
