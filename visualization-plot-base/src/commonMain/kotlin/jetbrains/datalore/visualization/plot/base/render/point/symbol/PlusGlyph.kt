package jetbrains.datalore.visualization.plot.base.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements

internal class PlusGlyph(location: DoubleVector, size: Double) : TwoShapeGlyph() {

    init {
        val half = size / 2
        val ox = location.x - half
        val oy = location.y - half
        val hLine = SvgSlimElements.line(
                0 + ox,
                half + oy,
                size + ox,
                half + oy)
        val vLine = SvgSlimElements.line(
                half + ox,
                0 + oy,
                half + ox,
                size + oy)

        setShapes(hLine, vLine)
    }
}
