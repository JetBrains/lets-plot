package jetbrains.datalore.visualization.plot.base.render.point

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimObject
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.aes.AestheticsUtil

internal class TinyRectangleShape private constructor() : PointShape {

    override// ASCII dot
    val code: Int
        get() = 46

    override fun size(dataPoint: DataPointAesthetics): Double {
        return 1.0
    }

    override fun strokeWidth(dataPoint: DataPointAesthetics): Double {
        return 0.0
    }

    override fun create(location: DoubleVector, dataPoint: DataPointAesthetics): SvgSlimObject {
        val r = SvgSlimElements.rect(location.x - 0.5, location.y - 0.5, 1.0, 1.0)
        val color = dataPoint.color()!!
        val alpha = AestheticsUtil.alpha(color, dataPoint)
        r.setFill(color, alpha)
        r.setStrokeWidth(0.0)
        return r
    }

    companion object {
        val INSTANCE = TinyRectangleShape()
    }
}
