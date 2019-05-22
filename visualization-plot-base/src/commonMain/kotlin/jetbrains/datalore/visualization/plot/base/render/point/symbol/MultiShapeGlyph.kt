package jetbrains.datalore.visualization.plot.base.render.point.symbol

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimShape

internal abstract class MultiShapeGlyph : Glyph {
    protected fun update(shape: SvgSlimShape?, fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double) {
        shape?.setFill(fill, fillAlpha)
        shape?.setStroke(stroke, strokeAlpha)
        shape?.setStrokeWidth(strokeWidth)
    }
}
