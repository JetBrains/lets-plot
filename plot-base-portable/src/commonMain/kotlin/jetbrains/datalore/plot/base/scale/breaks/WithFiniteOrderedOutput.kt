package jetbrains.datalore.plot.base.scale.breaks

interface WithFiniteOrderedOutput<T> {
    val outputValues: List<T>

    fun getOutputValueIndex(domainValue: Any): Int

    fun getOutputValue(domainValue: Any): T?
}
