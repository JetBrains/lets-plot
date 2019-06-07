package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Line
import jetbrains.datalore.visualization.base.svg.SvgLineElement

internal class SvgLineAttrMapping(target: Line) : SvgShapeMapping<Line>(target) {
    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgLineElement.X1.name -> target.startX = value as Double
            SvgLineElement.Y1.name -> target.startY = value as Double
            SvgLineElement.X2.name -> target.endX = value as Double
            SvgLineElement.Y2.name -> target.endY = value as Double
            else -> super.setAttribute(name, value)
        }
    }
}