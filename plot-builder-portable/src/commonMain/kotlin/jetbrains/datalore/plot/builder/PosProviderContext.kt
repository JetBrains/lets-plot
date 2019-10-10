package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Aesthetics

interface PosProviderContext {
    val aesthetics: Aesthetics

    val groupCount: Int
}
