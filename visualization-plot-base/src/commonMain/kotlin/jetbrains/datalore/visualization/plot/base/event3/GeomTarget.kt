package jetbrains.datalore.visualization.plot.base.event3

import jetbrains.datalore.visualization.plot.base.render.Aes

// `open` - for Mockito tests
open class GeomTarget(
        val hitIndex: Int,
        open val tipLayoutHint: TipLayoutHint,
        open val aesTipLayoutHints: Map<Aes<*>, TipLayoutHint>)
