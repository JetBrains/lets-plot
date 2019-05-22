package jetbrains.datalore.visualization.plot.base.event3

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess

// `open` for Mockito tests
open class ContextualMapping(
        val tooltipAes: List<Aes<*>>,
        val axisAes: List<Aes<*>>,
        val dataAccess: MappedDataAccess
)