package jetbrains.datalore.visualization.plot.gog.core.render.point

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimObject
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics

interface PointShape {
    val code: Int

    fun size(dataPoint: DataPointAesthetics): Double

    fun strokeWidth(dataPoint: DataPointAesthetics): Double

    fun create(location: DoubleVector, dataPoint: DataPointAesthetics): SvgSlimObject
}
