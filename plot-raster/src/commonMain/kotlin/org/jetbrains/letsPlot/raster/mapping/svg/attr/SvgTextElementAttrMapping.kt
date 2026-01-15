/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_CENTER
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_TOP
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.raster.mapping.svg.SvgUtils.toColor
import org.jetbrains.letsPlot.raster.scene.Text
import org.jetbrains.letsPlot.raster.scene.Text.Companion.DEFAULT_FONT_FAMILY
import org.jetbrains.letsPlot.raster.scene.Text.Companion.DEFAULT_FONT_SIZE
import org.jetbrains.letsPlot.raster.scene.Text.HorizontalAlignment
import org.jetbrains.letsPlot.raster.scene.Text.VerticalAlignment

internal object SvgTextElementAttrMapping : SvgAttrMapping<Text>() {
    override fun setAttribute(target: Text, name: String, value: Any?) {
        when (name) {
            SvgTextContent.FONT_SIZE.name -> target.fontSize = value?.asPxSize ?: DEFAULT_FONT_SIZE
            SvgTextContent.FONT_FAMILY.name -> target.fontFamily = value?.asFontFamily ?: DEFAULT_FONT_FAMILY
            SvgTextContent.FONT_WEIGHT.name -> target.fontWeight = when(value) {
                "bold" -> FontWeight.BOLD
                "normal" -> FontWeight.NORMAL
                else -> FontWeight.NORMAL
            }
            SvgTextContent.FONT_STYLE.name -> target.fontStyle = when(value) {
                "italic" -> FontStyle.ITALIC
                "normal" -> FontStyle.NORMAL
                else -> FontStyle.NORMAL
            }

            SvgTextElement.X.name -> target.x = value?.asFloat ?: 0.0f
            SvgTextElement.Y.name -> target.y = value?.asFloat ?: 0.0f
            SvgTextContent.TEXT_ANCHOR.name -> {
                val svgTextAnchor = value as String?
                when (svgTextAnchor) {
                    SvgConstants.SVG_TEXT_ANCHOR_END -> target.textAlignment = HorizontalAlignment.RIGHT
                    SvgConstants.SVG_TEXT_ANCHOR_MIDDLE -> target.textAlignment = HorizontalAlignment.CENTER
                    SvgConstants.SVG_TEXT_ANCHOR_START -> target.textAlignment = HorizontalAlignment.LEFT
                    else -> println("Unknown alignment")
                }
            }

            SvgTextContent.TEXT_DY.name -> {
                when (value) {
                    SVG_TEXT_DY_TOP -> target.textOrigin = VerticalAlignment.TOP
                    SVG_TEXT_DY_CENTER -> target.textOrigin = VerticalAlignment.CENTER
                    else -> throw IllegalStateException("Unexpected text 'dy' value: $value")
                }
            }

            SvgShape.FILL.name -> target.fill = toColor(value)
            SvgShape.FILL_OPACITY.name -> target.fillOpacity = value!!.asFloat
            SvgShape.STROKE.name -> target.stroke = toColor(value)
            SvgShape.STROKE_OPACITY.name -> target.strokeOpacity = value!!.asFloat
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value!!.asFloat
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map(String::toFloat)
                target.strokeDashArray = strokeDashArray
            }

            SvgConstants.SVG_STYLE_ATTRIBUTE -> {
                splitStyle(value as? String)
                    .forEach { (attr, value) ->
                        setAttribute(target, attr, value)
                }
            }

            else -> super.setAttribute(target, name, value)
        }
    }
}
