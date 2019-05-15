package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.visualization.plot.gog.config.event3.GeomTargetInteraction.TooltipAesSpec
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess

interface TooltipAesSpecProvider {
    fun createTooltipAesSpec(dataAccess: MappedDataAccess): TooltipAesSpec

    companion object {
        val NONE = object : TooltipAesSpecProvider {
            override fun createTooltipAesSpec(dataAccess: MappedDataAccess): TooltipAesSpec {
                return TooltipAesSpec.empty(dataAccess)
            }
        }
    }
}
