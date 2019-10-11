package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale

interface ScaleProvider<T> {
    fun createScale(data: DataFrame, variable: DataFrame.Variable): Scale<T>
}
