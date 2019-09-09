package jetbrains.datalore.visualization.plot.base.render.point

import jetbrains.datalore.visualization.plot.base.DataPointAesthetics

interface PointShape {
    val code: Int

    fun size(dataPoint: DataPointAesthetics): Double

    fun strokeWidth(dataPoint: DataPointAesthetics): Double

//    fun create(location: DoubleVector, dataPoint: DataPointAesthetics): SvgSlimObject
}
