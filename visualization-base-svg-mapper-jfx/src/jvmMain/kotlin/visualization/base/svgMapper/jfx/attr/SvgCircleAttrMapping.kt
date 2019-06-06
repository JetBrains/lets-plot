package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.Circle
import jetbrains.datalore.visualization.base.svg.SvgCircleElement

internal class SvgCircleAttrMapping(target: Circle) : SvgShapeMapping<Circle>(target) {
    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgCircleElement.CX.name -> target.centerX = value as Double
            SvgCircleElement.CY.name -> target.centerY = value as Double
            SvgCircleElement.R.name -> target.radius = value as Double
            else -> super.setAttribute(name, value)
        }
    }
}