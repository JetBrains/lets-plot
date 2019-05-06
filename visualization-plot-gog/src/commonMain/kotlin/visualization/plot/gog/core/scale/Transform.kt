package jetbrains.datalore.visualization.plot.gog.core.scale

interface Transform {
    fun apply(rawData: List<*>): List<Double>?

    fun applyInverse(v: Double): Any?
}
