package jetbrains.datalore.visualization.plot.builder.scale

import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.data.DataFrame

interface ScaleProvider<T> {
    fun createScale(data: DataFrame, variable: DataFrame.Variable): Scale<T>
}
