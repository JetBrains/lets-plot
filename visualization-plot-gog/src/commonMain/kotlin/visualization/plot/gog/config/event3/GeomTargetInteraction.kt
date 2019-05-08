package jetbrains.datalore.visualization.plot.gog.config.event3

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.*
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.event3.TooltipAesSpecProvider

class GeomTargetInteraction internal constructor(builder: GeomInteractionBuilder) : TooltipAesSpecProvider {

    private val myLocatorLookupSpace: LookupSpace
    private val myLocatorLookupStrategy: LookupStrategy
    private val myDisplayableAes: List<Aes<*>>
    private val myAxisTooltipEnabled: Boolean
    private val myAxisAes: List<Aes<*>>

    val tooltipAesSpecProvider: TooltipAesSpecProvider
        get() = this

    init {
        myLocatorLookupSpace = builder.locatorLookupSpace!!
        myLocatorLookupStrategy = builder.locatorLookupStrategy!!
        myDisplayableAes = builder.displayableAes
        myAxisTooltipEnabled = builder.isAxisTooltipEnabled
        myAxisAes = builder.axisAes!!
    }

    fun createLocatorLookupSpec(): LookupSpec {
        return LookupSpec(myLocatorLookupSpace, myLocatorLookupStrategy)
    }

    override fun createTooltipAesSpec(dataAccess: MappedDataAccess): TooltipAesSpec {
        return TooltipAesSpec.create(myDisplayableAes, if (myAxisTooltipEnabled) myAxisAes else emptyList(), dataAccess)
    }

    open class TooltipAesSpec private constructor(override val tooltipAes: List<Aes<*>>, override val axisAes: List<Aes<*>>, override val dataAccess: MappedDataAccess) : ContextualMapping {
        companion object {
            fun create(displayableAes: List<Aes<*>>, axisAes: List<Aes<*>>, dataAccess: MappedDataAccess): TooltipAesSpec {
                val showInTip = ArrayList<Aes<*>>()
                for (aes in displayableAes) {
                    if (dataAccess.isMapped(aes)) {
                        showInTip.add(aes)
                    }
                }

                return TooltipAesSpec(showInTip, axisAes, dataAccess)
            }
        }
    }

    companion object {

        internal val AXIS_TOOLTIP_COLOR = Color.GRAY
    }
}
