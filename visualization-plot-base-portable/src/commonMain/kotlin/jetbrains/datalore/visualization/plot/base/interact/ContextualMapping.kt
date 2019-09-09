package jetbrains.datalore.visualization.plot.base.interact

import jetbrains.datalore.visualization.plot.base.Aes

// `open` for Mockito tests
open class ContextualMapping(
        val tooltipAes: List<Aes<*>>,
        val axisAes: List<Aes<*>>,
        val dataAccess: MappedDataAccess
)