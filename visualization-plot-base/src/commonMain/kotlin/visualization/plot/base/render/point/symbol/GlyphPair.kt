package jetbrains.datalore.visualization.plot.base.render.point.symbol

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimGroup

internal class GlyphPair(private val myG1: Glyph, private val myG2: Glyph) : Glyph {

    override fun update(fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double) {
        myG1.update(fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
        myG2.update(fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
    }

    override fun appendTo(g: SvgSlimGroup) {
        myG1.appendTo(g)
        myG2.appendTo(g)
    }
}
