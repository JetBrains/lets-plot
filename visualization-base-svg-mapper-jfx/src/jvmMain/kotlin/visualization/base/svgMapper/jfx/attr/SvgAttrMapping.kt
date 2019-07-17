package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.Node
import jetbrains.datalore.visualization.base.svg.*
import jetbrains.datalore.visualization.base.svgMapper.jfx.unScaleTransforms
import jetbrains.datalore.visualization.base.svgToScene.parseSvgTransform

internal abstract class SvgAttrMapping<in TargetT : Node> {
    open fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgGraphicsElement.VISIBILITY.name -> target.visibleProperty().set(asBoolean(value))
            SvgGraphicsElement.OPACITY.name -> target.opacityProperty().set(asDouble(value))

            SvgConstants.SVG_STYLE_ATTRIBUTE -> setStyle(value as? String ?: "", target)
            SvgStylableElement.CLASS.name -> setStyleClass(value as String?, target)

            SvgTransformable.TRANSFORM.name -> setTransform((value as SvgTransform).toString(), target)

            else -> throw IllegalArgumentException("Unsupported attribute `$name` in ${target.javaClass.simpleName}")
        }
    }

    companion object {
        private fun setStyle(value: String, target: Node) {
            val valueFx = value.split(";").joinToString(";") { if (it.isNotEmpty()) "-fx-$it" else it }
            target.style = valueFx
        }

        private fun setStyleClass(value: String?, target: Node) {
            target.styleClass.clear()
            if (value != null) {
                target.styleClass.addAll(value.split(" "))
            }
        }

        private fun setTransform(value: String, target: Node) {
            val transforms = parseSvgTransform(value)
            target.transforms.addAll(unScaleTransforms(transforms))
        }

        fun asDouble(value: Any?): Double {
            if (value is Double) return value
            return (value as String).toDouble()
        }

        fun asBoolean(value: Any?): Boolean {
            return (value as? String)?.toBoolean() ?: false
        }
    }
}