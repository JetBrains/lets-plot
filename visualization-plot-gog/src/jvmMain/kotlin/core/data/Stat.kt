package jetbrains.datalore.visualization.plot.gog.core.data

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

interface Stat {
    fun apply(data: DataFrame, statCtx: StatContext): DataFrame

    fun requires(): List<Aes<*>>

    fun hasDefaultMapping(aes: Aes<*>): Boolean

    fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable
}
