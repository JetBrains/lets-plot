/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Shape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape

internal abstract class SvgShapeMapping<TargetT : Shape> : SvgAttrMapping<TargetT>() {
    init {
//        target.smoothProperty().set(false)
//        target.strokeType = StrokeType.CENTERED
    }

    override fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> setColor(value, fillGet(target), fillSet(target))
            SvgShape.FILL_OPACITY.name -> {
                // To fix artifacts on a 100% opaque shape (JFX problem only).
                // Issue: https://github.com/JetBrains/lets-plot/issues/539
                // * The problem occurs on rectangles with 100% opacity.
                // So it was enough to use this fix in `SvgRectAttrMapping`,
                // but decided to move the fix higher in hierarchy to `SvgShapeMapping` as a precaution.
                // * This code will work even when opacity is not explicitly set
                // (due to `colorAttributeTransform()`)
                // * See also: https://bugs.openjdk.java.net/browse/JDK-8087752

                val v = if (asDouble(value) == 1.0) 0.99 else asDouble(value)
                setOpacity(v, fillGet(target), fillSet(target))
            }
            SvgShape.STROKE.name -> setColor(value, strokeGet(target), strokeSet(target))
            SvgShape.STROKE_OPACITY.name -> setOpacity(asDouble(value), strokeGet(target), strokeSet(target))
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = asDouble(value)
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map { it.toDouble() }
                target.strokeDashArray.addAll(strokeDashArray)
            }
            else -> super.setAttribute(target, name, value)
        }
    }

    companion object {
        private val fillGet = { shape: Shape ->
            // This will reset fill color to black if color is defined via style
            { shape.fill as? Color ?: Color.BLACK }
        }
        private val fillSet = { shape: Shape -> { c: Color -> shape.fill = c } }
        private val strokeGet = { shape: Shape ->
            // This will reset stroke color to black if color is defined via style
            { shape.stroke as? Color ?: Color.BLACK }
        }
        private val strokeSet = { shape: Shape -> { c: Color -> shape.stroke = c } }


        /**
         * value : the color name (string) or SvgColor (jetbrains.datalore.vis.svg)
         */
        private fun setColor(value: Any?, get: () -> Color, set: (Color) -> Unit) {
            if (value == null) return

            val svgColorString = value.toString()
            val newColor =
                if (svgColorString == SvgColors.NONE.toString()) {
                    Color(0.0, 0.0, 0.0, 0.0)
                } else {
                    val new = Paint.valueOf(svgColorString) as Color
                    val curr = get()
                    Color.color(new.red, new.green, new.blue, curr.opacity)
                }
            set(newColor)
        }

        private fun setOpacity(value: Double, get: () -> Color, set: (Color) -> Unit) {
            val c = get()
            set(Color.color(c.red, c.green, c.blue, value))
        }
    }
}