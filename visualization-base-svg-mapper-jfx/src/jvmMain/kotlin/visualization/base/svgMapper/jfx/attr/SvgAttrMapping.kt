package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.Node
import jetbrains.datalore.visualization.base.svg.SvgConstants
import jetbrains.datalore.visualization.base.svg.SvgStylableElement

internal abstract class SvgAttrMapping<TargetT : Node>(val target: TargetT) {
    open fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgConstants.SVG_STYLE_ATTRIBUTE -> {
                println("style: ${value as String?}")
                target.style = value as String}
            SvgStylableElement.CLASS.name -> setStyleClass(value as String?, target)
            else -> throw IllegalArgumentException("Unsupported attribute `$name` in ${target.javaClass.simpleName}")
        }
    }

    companion object {
        private fun setStyleClass(value: String?, target: Node) {
            target.styleClass.clear()
            if (value != null) {
                target.styleClass.addAll(value.split(" "))
            }
        }

        fun asDouble(value: Any?): Double {
            if (value is Double) return value
            return (value as? String)?.toDouble()!!
        }

        fun asBoolean(value: Any?): Boolean {
            return (value as? String)?.toBoolean() ?: false
        }
    }
}