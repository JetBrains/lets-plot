package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes

// `open` - for Mockito tests
open class GeomTarget(
    val hitIndex: Int,
    open val tipLayoutHint: TipLayoutHint,
    open val aesTipLayoutHints: Map<Aes<*>, TipLayoutHint>)
