/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg.attr

import javafx.scene.Node
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.PointerEvents
import org.jetbrains.letsPlot.jfx.mapping.svg.unScaleTransforms

internal abstract class SvgAttrMapping<in TargetT : Node> {
    open fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgGraphicsElement.VISIBILITY.name -> target.isVisible = visibilityAsBoolean(value)
            SvgGraphicsElement.OPACITY.name -> target.opacity = asDouble(value)
            SvgGraphicsElement.CLIP_BOUNDS_JFX.name -> target.clip = (value as? DoubleRectangle)?.run { Rectangle(left, top, width, height) }
            SvgGraphicsElement.CLIP_CIRCLE_JFX.name -> target.clip = (value as? DoubleRectangle)?.run { Circle(center.x, center.y, width / 2) }
            SvgGraphicsElement.CLIP_PATH.name -> Unit // TODO: ignored

            SvgConstants.SVG_STYLE_ATTRIBUTE -> target.style = svgStyleToFx(value as? String ?: "")
            SvgStylableElement.CLASS.name -> setStyleClass(value as String?, target)

            SvgTransformable.TRANSFORM.name -> value?.toString()?.let { setTransform(it, target) } // might be SvgTransform or String (for SlimObject)

            SvgElement.ID.name -> target.id = value as? String // TODO: or ignore it?
            SvgGraphicsElement.POINTER_EVENTS.name -> target.isMouseTransparent = value == PointerEvents.NONE
            SvgConstants.DISPLAY -> {} // not needed for JavaFX

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
        internal fun svgStyleToFx(value: String): String {
            return value.split(";").joinToString(";") { if (it.isNotEmpty()) "-fx-${it.trim()}" else it }
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