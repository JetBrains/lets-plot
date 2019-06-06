package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Ellipse
import jetbrains.datalore.visualization.base.svg.SvgEllipseElement

internal class SvgEllipseAttrMapping(target: Ellipse) : SvgShapeMapping<Ellipse>(target) {
    override fun setAttribute(name: String, value: Any?) {
        target.stroke
        when (name) {
            SvgEllipseElement.CX.name -> target.centerX = value as Double
            SvgEllipseElement.CY.name -> target.centerY = value as Double
            SvgEllipseElement.RX.name -> target.radiusX = value as Double
            SvgEllipseElement.RY.name -> target.radiusY = value as Double
            else -> super.setAttribute(name, value)
        }
    }
}