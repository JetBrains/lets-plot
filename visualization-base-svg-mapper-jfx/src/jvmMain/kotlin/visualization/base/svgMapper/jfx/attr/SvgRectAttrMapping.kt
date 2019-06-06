package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Rectangle
import jetbrains.datalore.visualization.base.svg.SvgRectElement

internal class SvgRectAttrMapping(target: Rectangle) : SvgShapeMapping<Rectangle>(target) {
    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgRectElement.X.name -> target.x = value as Double
            SvgRectElement.Y.name -> target.y = value as Double
            SvgRectElement.WIDTH.name -> target.width = value as Double
            SvgRectElement.HEIGHT.name -> target.height = value as Double
            else -> super.setAttribute(name, value)
        }
    }
}