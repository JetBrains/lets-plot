package jetbrains.datalore.visualization.plot.builder.scale

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.scale.Scale2

interface ScaleProvider<T> {
    fun createScale(data: DataFrame, variable: DataFrame.Variable): Scale2<T>
}
