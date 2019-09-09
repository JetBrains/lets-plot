package jetbrains.datalore.visualization.plot.base.interact

import jetbrains.datalore.visualization.plot.base.Aes

// `open` - for Mockito tests
open class GeomTarget(
        val hitIndex: Int,
        open val tipLayoutHint: TipLayoutHint,
        open val aesTipLayoutHints: Map<Aes<*>, TipLayoutHint>)
