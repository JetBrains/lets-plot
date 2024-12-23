/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg.attr

import javafx.geometry.VPos
import javafx.scene.paint.Color
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_CENTER
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_TOP
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.jfx.mapping.svg.TextLine
import org.jetbrains.letsPlot.jfx.mapping.svg.attr.SvgShapeMapping.Companion.asColor
import org.jetbrains.letsPlot.jfx.mapping.svg.attr.SvgShapeMapping.Companion.changeOpacity

internal object SvgTextElementAttrMapping : SvgAttrMapping<TextLine>() {
    override fun setAttribute(target: TextLine, name: String, value: Any?) {
        when (name) {
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = asDouble(value)

            SvgShape.FILL.name -> value?.let { fill ->
                target.fill = asColor(fill, target.fill?.opacity ?: 1.0)
            }

            SvgShape.FILL_OPACITY.name -> {
                val color = target.fill ?: Color.BLACK
                val opacity = asDouble(value)
                target.fill = changeOpacity(color, opacity)
            }

            SvgShape.STROKE.name -> value?.let { stroke ->
                target.stroke = asColor(stroke, target.stroke?.opacity ?: 1.0)
            }

            SvgShape.STROKE_OPACITY.name -> {
                val color = target.stroke ?: Color.BLACK
                val newOpacity = asDouble(value)
                target.stroke = changeOpacity(color, newOpacity)
            }


            SvgConstants.SVG_STYLE_ATTRIBUTE -> {
                require(value is String)
                val style = value.split(";")
                    .map(String::trim)
                    .filter(String::isNotEmpty)
                    .map { it.split(":").map(String::trim) }
                    .associate { it[0] to it[1] }

                style["font-family"]?.let {
                    target.fontFamily = it
                }

                style["font-weight"]?.let { weight ->
                    target.fontWeight = FontWeight.BOLD.takeIf { weight == "bold" }
                }

                style["font-style"]?.let { posture ->
                    target.fontPosture = FontPosture.ITALIC.takeIf { posture == "italic" }
                }

                style["font-size"]?.let { size ->
                    target.fontSize = size.removeSuffix("px").toDoubleOrNull() ?: -1.0
                }

                style["fill"]?.let {
                    target.fill = Color.web(it, target.fill?.opacity ?: 1.0)
                }

                style["stroke"]?.let {
                    target.stroke = Color.web(it, target.stroke?.opacity ?: 1.0)
                }

                val unhandledStyleProperties = style.keys - setOf(
                    "font-family",
                    "font-weight",
                    "font-style",
                    "font-size",
                    "fill",
                    "stroke"
                )
                if (unhandledStyleProperties.isNotEmpty()) {
                    throw IllegalStateException("Unhandled style attributes: $unhandledStyleProperties")
                }
            }

            SvgTextElement.X.name -> target.layoutX = asDouble(value)
            SvgTextElement.Y.name -> target.layoutY = asDouble(value)
            SvgTextContent.TEXT_ANCHOR.name ->
                target.textAlignment = when (value as String?) {
                    SvgConstants.SVG_TEXT_ANCHOR_END -> TextAlignment.RIGHT
                    SvgConstants.SVG_TEXT_ANCHOR_MIDDLE -> TextAlignment.CENTER
                    else -> TextAlignment.LEFT
                }

            SvgTextContent.TEXT_DY.name ->
                target.textOrigin = when (value) {
                    SVG_TEXT_DY_TOP -> VPos.TOP
                    SVG_TEXT_DY_CENTER -> VPos.CENTER
                    else -> throw IllegalStateException("Unexpected text 'dy' value: $value")
                }

            else -> super.setAttribute(target, name, value)
        }
    }
}
