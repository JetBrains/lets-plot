package jetbrains.datalore.visualization.plot.base

interface Stat {
    fun apply(data: DataFrame, statCtx: StatContext): DataFrame

    fun requires(): List<Aes<*>>

    fun hasDefaultMapping(aes: Aes<*>): Boolean

    fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable
}
