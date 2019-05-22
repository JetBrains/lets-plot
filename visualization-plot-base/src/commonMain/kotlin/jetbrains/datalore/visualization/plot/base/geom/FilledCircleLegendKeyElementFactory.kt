package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.point.NamedShape

internal class FilledCircleLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val location = DoubleVector(size.x / 2, size.y / 2)
        val slimObject = SHAPE.create(location, p)
        val slimGroup = SvgSlimElements.g(1)
        slimObject.appendTo(slimGroup)
        return GeomBase.wrap(slimGroup)
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val shapeSize = SHAPE.size(p)
        val strokeWidth = SHAPE.strokeWidth(p)
        val size = shapeSize + strokeWidth + 2.0
        return DoubleVector(size, size)
    }

    companion object {
        private val SHAPE = NamedShape.FILLED_CIRCLE
    }
}
