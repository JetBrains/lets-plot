package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.ContextualMapping
import jetbrains.datalore.visualization.plot.gog.core.render.Aes

interface TooltipAesSpecProvider {
    fun createTooltipAesSpec(dataAccess: MappedDataAccess): ContextualMapping

    companion object {
        val NONE = object : TooltipAesSpecProvider {
            override fun createTooltipAesSpec(dataAccess: MappedDataAccess): ContextualMapping {
                return object : ContextualMapping {
                    override val axisAes: List<Aes<*>> = emptyList()
                    override val tooltipAes: List<Aes<*>> = emptyList()
                    override val dataAccess: MappedDataAccess = dataAccess
                }
            }
        }
    }
}
