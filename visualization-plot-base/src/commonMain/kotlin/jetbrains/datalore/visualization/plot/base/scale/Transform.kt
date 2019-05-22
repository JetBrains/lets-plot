package jetbrains.datalore.visualization.plot.base.scale

interface Transform {
    fun apply(rawData: List<*>): List<Double?>

    fun applyInverse(v: Double?): Any?
}
