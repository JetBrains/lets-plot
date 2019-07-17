package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.shape.SVGPath
import jetbrains.datalore.visualization.base.svg.SvgPathData
import jetbrains.datalore.visualization.base.svg.SvgPathElement

internal object SvgPathAttrMapping : SvgShapeMapping<SVGPath>() {
    override fun setAttribute(target: SVGPath, name: String, value: Any?) {
        when (name) {
            SvgPathElement.D.name -> {
                // Can be string (slim path) or SvgPathData
                val pathStr = when (value) {
                    is String -> value
                    is SvgPathData -> value.toString()
                    null -> throw IllegalArgumentException("Undefined `path data`")
                    else -> throw IllegalArgumentException("Unexpected `path data` type: ${value::class.simpleName}")
                }

                target.content = pathStr
            }
            else -> super.setAttribute(target, name, value)
        }
    }
}