package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.visualization.plot.base.render.Aesthetics

interface PosProviderContext {
    val aesthetics: Aesthetics

    val groupCount: Int
}
