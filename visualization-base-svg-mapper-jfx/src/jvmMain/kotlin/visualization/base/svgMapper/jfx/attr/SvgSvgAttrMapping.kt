package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.layout.Pane
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

internal object SvgSvgAttrMapping : SvgAttrMapping<Pane>() {

    override fun setAttribute(target: Pane, name: String, value: Any?) {
        when (name) {
            SvgSvgElement.X.name -> target.layoutX = asDouble(value)
            SvgSvgElement.Y.name -> target.layoutY = asDouble(value)
            SvgSvgElement.WIDTH.name -> target.setWidth(asDouble(value))
            SvgSvgElement.HEIGHT.name -> target.setHeight(asDouble(value))
//            SvgSvgElement.VIEW_BOX  ??
            else -> super.setAttribute(target, name, value)
        }
    }

    private fun Pane.setWidth(width: Double) {
        minWidth = width
        maxWidth = width
        prefWidth = width
    }

    private fun Pane.setHeight(height: Double) {
        minHeight = height
        maxHeight = height
        prefHeight = height
    }
}