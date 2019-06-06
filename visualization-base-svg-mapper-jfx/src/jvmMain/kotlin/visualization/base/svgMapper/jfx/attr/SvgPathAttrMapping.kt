package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.SVGPath
import jetbrains.datalore.visualization.base.svg.SvgPathData
import jetbrains.datalore.visualization.base.svg.SvgPathElement

internal class SvgPathAttrMapping(target: SVGPath) : SvgShapeMapping<SVGPath>(target) {
    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgPathElement.D.name -> {
                val pathStr = (value as SvgPathData).toString()
                target.content = pathStr
            }
            else -> super.setAttribute(name, value)
        }
    }
}