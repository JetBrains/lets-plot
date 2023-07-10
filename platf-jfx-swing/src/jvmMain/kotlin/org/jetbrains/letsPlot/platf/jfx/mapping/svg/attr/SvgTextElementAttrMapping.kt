/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr

import javafx.geometry.VPos
import javafx.scene.text.Text
import javafx.scene.text.TextAlignment
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_CENTER
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_DY_TOP
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

internal object SvgTextElementAttrMapping : SvgShapeMapping<Text>() {
    override fun setAttribute(target: Text, name: String, value: Any?) {
        when (name) {
            SvgTextElement.X.name -> target.x = asDouble(value)
            SvgTextElement.Y.name -> target.y = asDouble(value)
            SvgTextContent.TEXT_ANCHOR.name -> {
                val svgTextAnchor = value as String?
                revalidatePositionAttributes(svgTextAnchor, target)
            }
            SvgTextContent.TEXT_DY.name -> {
                when (value) {
                    SVG_TEXT_DY_TOP -> target.textOrigin = VPos.TOP
                    SVG_TEXT_DY_CENTER -> target.textOrigin = VPos.CENTER
                    else -> throw IllegalStateException("Unexpected text 'dy' value: $value")
                }
            }

            SvgTextContent.FILL.name,
            SvgTextContent.FILL_OPACITY.name,
            SvgTextContent.STROKE.name,
            SvgTextContent.STROKE_OPACITY.name,
            SvgTextContent.STROKE_WIDTH.name -> super.setAttribute(target, name, value)

            else -> super.setAttribute(target, name, value)
        }
    }

    fun revalidatePositionAttributes(svgTextAnchor: String?, target: Text) {
        val width = target.boundsInLocal.width
        when (svgTextAnchor) {
            SvgConstants.SVG_TEXT_ANCHOR_END -> {
                target.translateX = -width
                target.textAlignment = TextAlignment.RIGHT
            }
            SvgConstants.SVG_TEXT_ANCHOR_MIDDLE -> {
                target.translateX = -width / 2
                target.textAlignment = TextAlignment.CENTER
            }
            else -> {
                target.translateX = 0.0
                target.textAlignment = TextAlignment.LEFT
            }
        }
    }
}