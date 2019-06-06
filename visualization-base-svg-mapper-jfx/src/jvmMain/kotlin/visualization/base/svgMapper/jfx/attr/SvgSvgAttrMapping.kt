package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.Group
import jetbrains.datalore.visualization.base.svg.SvgRectElement

internal class SvgSvgAttrMapping(target: Group) : SvgAttrMapping<Group>(target) {

    override fun setAttribute(name: String, value: Any?) {
        when (name) {
//            SvgRectElement.X.name -> target.x = value as Double
//            SvgRectElement.Y.name -> target.y = value as Double
            SvgRectElement.WIDTH.name,
            SvgRectElement.HEIGHT.name -> {
            } // ignore
            else -> super.setAttribute(name, value)
        }
    }
}