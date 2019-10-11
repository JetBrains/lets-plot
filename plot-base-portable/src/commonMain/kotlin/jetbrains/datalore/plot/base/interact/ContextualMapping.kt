package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes

// `open` for Mockito tests
open class ContextualMapping(
    val tooltipAes: List<Aes<*>>,
    val axisAes: List<Aes<*>>,
    val dataAccess: MappedDataAccess
)