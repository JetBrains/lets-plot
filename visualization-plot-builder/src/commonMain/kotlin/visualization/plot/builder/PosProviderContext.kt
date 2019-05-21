package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.visualization.plot.base.render.Aesthetics

interface PosProviderContext {
    val aesthetics: Aesthetics

    val groupCount: Int
}
