package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.render.Aes

interface ContextualMapping {
    val axisAes: List<Aes<*>>
    val tooltipAes: List<Aes<*>>
    val dataAccess: MappedDataAccess
}