package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Rectangle
import jetbrains.datalore.visualization.base.svg.SvgRectElement

internal object SvgRectAttrMapping : SvgShapeMapping<Rectangle>() {
    override fun setAttribute(target: Rectangle, name: String, value: Any?) {
        when (name) {
            SvgRectElement.X.name -> target.x = asDouble(value)
            SvgRectElement.Y.name -> target.y = asDouble(value)
            SvgRectElement.WIDTH.name -> target.width = asDouble(value)
            SvgRectElement.HEIGHT.name -> target.height = asDouble(value)
            else -> super.setAttribute(target, name, value)
        }
    }
}