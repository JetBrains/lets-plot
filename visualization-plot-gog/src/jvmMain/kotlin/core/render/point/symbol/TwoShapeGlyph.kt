package jetbrains.datalore.visualization.plot.gog.core.render.point.symbol

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimGroup
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimShape

internal abstract class TwoShapeGlyph : MultiShapeGlyph() {
    private var myS1: SvgSlimShape? = null
    private var myS2: SvgSlimShape? = null

    protected fun setShapes(s1: SvgSlimShape, s2: SvgSlimShape) {
        myS1 = s1
        myS2 = s2
    }

    override fun update(fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double) {
        update(myS1, fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
        update(myS2, fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
    }

    override fun appendTo(g: SvgSlimGroup) {
        myS1!!.appendTo(g)
        myS2!!.appendTo(g)
    }
}
