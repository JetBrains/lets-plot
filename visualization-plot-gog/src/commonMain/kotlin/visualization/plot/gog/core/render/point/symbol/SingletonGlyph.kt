package jetbrains.datalore.visualization.plot.gog.core.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimGroup
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimShape

abstract class SingletonGlyph : Glyph {
    private val myShape: SvgSlimShape

    protected constructor(shape: SvgSlimShape) {
        myShape = shape
    }

    protected constructor(location: DoubleVector, width: Double) {
        myShape = createShape(location, width)
    }

    protected abstract fun createShape(location: DoubleVector, width: Double): SvgSlimShape

    override fun update(fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double) {
        myShape.setFill(fill, fillAlpha)
        myShape.setStroke(stroke, strokeAlpha)
        myShape.setStrokeWidth(strokeWidth)
    }

    override fun appendTo(g: SvgSlimGroup) {
        myShape.appendTo(g)
    }
}
