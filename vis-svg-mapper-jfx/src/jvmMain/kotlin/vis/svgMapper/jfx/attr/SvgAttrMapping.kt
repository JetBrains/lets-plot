/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx.attr

import javafx.scene.Node
import jetbrains.datalore.vis.svg.*
import jetbrains.datalore.vis.svgMapper.jfx.unScaleTransforms
import jetbrains.datalore.vis.svgToScene.parseSvgTransform

internal abstract class SvgAttrMapping<in TargetT : Node> {
    open fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgGraphicsElement.VISIBILITY.name -> target.visibleProperty().set(visibilityAsBoolean(value))
            SvgGraphicsElement.OPACITY.name -> target.opacityProperty().set(asDouble(value))

            SvgConstants.SVG_STYLE_ATTRIBUTE -> setStyle(value as? String ?: "", target)
            SvgStylableElement.CLASS.name -> setStyleClass(value as String?, target)

            SvgTransformable.TRANSFORM.name -> setTransform((value as SvgTransform).toString(), target)

            else -> throw IllegalArgumentException("Unsupported attribute `$name` in ${target.javaClass.simpleName}")
        }
    }

    private fun visibilityAsBoolean(value: Any?): Boolean {
        return when (value) {
            is Boolean -> value
            is SvgGraphicsElement.Visibility -> value == SvgGraphicsElement.Visibility.VISIBLE
            is String -> value == SvgGraphicsElement.Visibility.VISIBLE.toString() || asBoolean(value)
            else -> false
        }
    }

    companion object {
        private fun setStyle(value: String, target: Node) {
            val valueFx = value.split(";").joinToString(";") { if (it.isNotEmpty()) "-fx-${it.trim()}" else it }
            target.style = valueFx
        }

        private fun setStyleClass(value: String?, target: Node) {
            target.styleClass.clear()
            if (value != null) {
                target.styleClass.addAll(value.split(" "))
            }
        }

        private fun setTransform(value: String, target: Node) {
            target.transforms.clear()

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