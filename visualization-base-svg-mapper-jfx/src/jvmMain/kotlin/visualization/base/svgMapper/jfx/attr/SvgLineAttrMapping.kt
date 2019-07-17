package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Line
import jetbrains.datalore.visualization.base.svg.SvgLineElement

internal object SvgLineAttrMapping : SvgShapeMapping<Line>() {
    override fun setAttribute(target: Line, name: String, value: Any?) {
        when (name) {
            SvgLineElement.X1.name -> target.startX = asDouble(value)
            SvgLineElement.Y1.name -> target.startY = asDouble(value)
            SvgLineElement.X2.name -> target.endX = asDouble(value)
            SvgLineElement.Y2.name -> target.endY = asDouble(value)
            else -> super.setAttribute(target, name, value)
        }
    }
}