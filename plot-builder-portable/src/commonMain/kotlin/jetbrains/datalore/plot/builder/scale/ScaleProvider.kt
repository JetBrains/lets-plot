package jetbrains.datalore.plot.builder.scale

import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Scale

interface ScaleProvider<T> {
    fun createScale(data: DataFrame, variable: DataFrame.Variable): Scale<T>
}
