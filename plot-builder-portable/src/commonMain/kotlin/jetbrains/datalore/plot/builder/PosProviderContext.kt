package jetbrains.datalore.plot.builder

import jetbrains.datalore.visualization.plot.base.Aesthetics

interface PosProviderContext {
    val aesthetics: Aesthetics

    val groupCount: Int
}
