package jetbrains.datalore.plot.base

interface Transform {
    fun apply(rawData: List<*>): List<Double?>

    fun applyInverse(v: Double?): Any?
}
