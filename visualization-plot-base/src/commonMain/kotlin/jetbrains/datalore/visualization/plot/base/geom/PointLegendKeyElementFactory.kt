package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory

internal class PointLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val location = DoubleVector(size.x / 2, size.y / 2)
        val shape = p.shape()!!
        val slimObject = shape.create(location, p)
        val slimGroup = SvgSlimElements.g(1)
        slimObject.appendTo(slimGroup)
        return GeomBase.wrap(slimGroup)
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val shape = p.shape()!!
        val shapeSize = shape.size(p)
        val strokeWidth = shape.strokeWidth(p)
        val size = shapeSize + strokeWidth + 2.0
        return DoubleVector(size, size)
    }
}
