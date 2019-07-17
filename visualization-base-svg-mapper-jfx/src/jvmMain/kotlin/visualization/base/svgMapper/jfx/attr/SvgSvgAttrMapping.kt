package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.layout.Pane
import jetbrains.datalore.visualization.base.svg.SvgSvgElement

internal object SvgSvgAttrMapping : SvgAttrMapping<Pane>() {

    override fun setAttribute(target: Pane, name: String, value: Any?) {
        when (name) {
            SvgSvgElement.WIDTH.name,
            SvgSvgElement.HEIGHT.name -> Unit // ignore
//            SvgSvgElement.VIEW_BOX  ??
            else -> super.setAttribute(target, name, value)
        }
    }
}