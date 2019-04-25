package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

class GeomTarget(val hitIndex: Int, val tipLayoutHint: TipLayoutHint, val aesTipLayoutHints: Map<Aes<*>, TipLayoutHint>)
