/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement.PointerEvents
import org.jetbrains.letsPlot.raster.mapping.svg.SvgTransformParser.parseSvgTransform
import org.jetbrains.letsPlot.raster.shape.Element

internal abstract class SvgAttrMapping<in TargetT : Element> {
    open fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgGraphicsElement.VISIBILITY.name -> target.isVisible = visibilityAsBoolean(value)
            SvgGraphicsElement.OPACITY.name -> target.opacity = value?.asFloat
            //SvgGraphicsElement.CLIP_BOUNDS_JFX.name -> target.clipPath = (value as DoubleRectangle).let {
            //    SkPath().addRect(
            //        Rect.makeLTRB(
            //            it.left.toFloat(),
            //            it.top.toFloat(),
            //            it.right.toFloat(),
            //            it.bottom.toFloat()
            //        )
            //    )
            //}

            //SvgGraphicsElement.CLIP_CIRCLE_JFX.name -> target.clipPath = (value as DoubleRectangle).let {
            //    SkPath().addCircle(
            //        it.center.x.toFloat(),
            //        it.center.y.toFloat(),
            //        it.width.toFloat() / 2
            //    )
            //}

            SvgGraphicsElement.CLIP_PATH.name -> Unit // Not supported.
            SvgConstants.SVG_STYLE_ATTRIBUTE -> {
                splitStyle(value as? String)
                    .forEach { (attr, value) ->
                        setAttribute(target, attr, value)
                    }
            }

            SvgStylableElement.CLASS.name -> target.styleClass = (value as String?)?.split(" ")
            SvgTransformable.TRANSFORM.name -> setTransform(value.toString(), target)
            SvgElement.ID.name -> target.id = value as String?
            SvgGraphicsElement.POINTER_EVENTS.name -> target.isMouseTransparent = value == PointerEvents.NONE

            else -> println("Unsupported attribute `$name` in ${target::class.simpleName}")
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
        private fun setTransform(value: String, target: Element) {
            target.transform = parseSvgTransform(value).fold(AffineTransform.IDENTITY, AffineTransform::concat)
        }

        val Any.asFloat: Float
            get() = when (this) {
                is Number -> this.toFloat()
                is String -> this.toFloat()
                else -> error("Unsupported float value: $this")
            }

        val Any.asPxSize: Float?
            get() = when (this) {
                is Number -> this.toFloat()
                is String -> kotlin.runCatching { this.removeSuffix("px").toFloat() }.getOrNull()
                else -> null.also { println("Unsupported px size value: $this") }
            }

        val Any.asFontFamily: List<String>
            get() = (this as? String)
                ?.split(",")
                ?.map(String::trim)
                ?: emptyList()

        fun splitStyle(style: String?): List<Pair<String, String>> {
            val style = style ?: return emptyList()
                return style
                    .split(";")
                    .flatMap { it.split(":") }
                    .windowed(2, 2)
                    .map { (attr, value) -> attr to value }
        }

        internal fun asBoolean(value: Any?): Boolean {
            return (value as? String)?.toBoolean() ?: false
        }
   }
}
