package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.render.Aes

// `open` for Mockito tests
open class ContextualMapping(
        val tooltipAes: List<Aes<*>>,
        val axisAes: List<Aes<*>>,
        val dataAccess: MappedDataAccess
)