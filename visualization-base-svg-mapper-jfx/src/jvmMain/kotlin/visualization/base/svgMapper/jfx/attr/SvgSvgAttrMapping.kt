package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.Group
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

internal class SvgSvgAttrMapping(target: Group) : SvgAttrMapping<Group>(target) {

    override fun setAttribute(name: String, value: Any?) {
        when (name) {
//            SvgSvgElement.X.name -> target.x = value as Double
//            SvgSvgElement.Y.name -> target.y = value as Double
            SvgSvgElement.WIDTH.name,
            SvgSvgElement.HEIGHT.name -> {
            } // ignore
//            SvgSvgElement.VIEW_BOX  ??
            else -> super.setAttribute(name, value)
        }
    }
}