package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Circle
import jetbrains.datalore.visualization.base.svg.SvgCircleElement

internal object SvgCircleAttrMapping : SvgShapeMapping<Circle>() {
    override fun setAttribute(target: Circle, name: String, value: Any?) {
        when (name) {
            SvgCircleElement.CX.name -> target.centerX = asDouble(value)
            SvgCircleElement.CY.name -> target.centerY = asDouble(value)
            SvgCircleElement.R.name -> target.radius = asDouble(value)
            else -> super.setAttribute(target, name, value)
        }
    }
}