/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d

internal abstract class Figure : Element() {
    var stroke: Color? by visualProp(null)
    var strokeWidth: Float by visualProp(1f)
    var strokeOpacity: Float by visualProp(1f)
    var strokeDashArray: List<Double> by visualProp(emptyList())
    var strokeDashOffset: Float by visualProp(0f)
    var strokeMiter: Float? by visualProp(null) // not mandatory, default works fine

    var fill: Color? by visualProp(null)
    var fillOpacity: Float by visualProp(1f)

    val fillPaint: Paint? by computedProp(Figure::fill, Figure::fillOpacity, managed = true) {
        return@computedProp fillPaint(fill, fillOpacity)
    }

    val strokePaint: Paint? by computedProp(
        Figure::stroke,
        Figure::strokeWidth,
        Figure::strokeDashArray,
        Figure::strokeOpacity,
        Figure::strokeMiter,
        managed = true
    ) {
        return@computedProp strokePaint(
            stroke = stroke,
            strokeWidth = strokeWidth,
            strokeOpacity = strokeOpacity,
            strokeDashArray = strokeDashArray,
            strokeDashOffset = strokeDashOffset,
            strokeMiter = strokeMiter
        )
    }

    companion object {
        internal fun strokePaint(
            stroke: Color? = null,
            strokeWidth: Float = 1f,
            strokeOpacity: Float = 1f,
            strokeDashArray: List<Double> = emptyList(),
            strokeDashOffset: Float = 0f, // not mandatory, default works fine
            strokeMiter: Float? = null
        ): Paint? {
            if (stroke == null) return null
            if (strokeOpacity == 0f) return null

            if (strokeWidth == 0f) {
                // Handle zero width manually, because Skia threatens 0 as "hairline" width, i.e. 1 pixel.
                // Source: https://api.skia.org/classSkPaint.html#af08c5bc138e981a4e39ad1f9b165c32c
                return null
            }

            val paint = Paint()
            paint.isStroke = true
            paint.color = stroke.changeAlpha(strokeOpacity)
            paint.strokeWidth = strokeWidth
            strokeMiter?.let { paint.strokeMiter = it }
            strokeDashArray.let { paint.strokeDashList = it.toDoubleArray() }
            strokeDashOffset.let { paint.strokeDashOffset = it }
            return paint
        }

        internal fun fillPaint(fill: Color? = null, fillOpacity: Float = 1f): Paint? {
            if (fill == null) return null

            return Paint().also { paint ->
                paint.color = fill.changeAlpha(fillOpacity)
            }
        }

        internal fun applyPaint(
            paint: Paint,
            canvas: Canvas
        ) {
            if (paint.isStroke) {
                canvas.context2d.setLineWidth(paint.strokeWidth.toDouble())
                canvas.context2d.setStrokeStyle(paint.color)
                canvas.context2d.setStrokeMiterLimit(paint.strokeMiter.toDouble())
                canvas.context2d.setLineDash(paint.strokeDashList)

            } else {
                canvas.context2d.setFillStyle(paint.color)
            }
        }

        internal fun Context2d.stroke(paint: Paint) {
            require(paint.isStroke) { "Paint must be a stroke paint" }

            setLineWidth(paint.strokeWidth.toDouble())
            setStrokeStyle(paint.color)
            setStrokeMiterLimit(paint.strokeMiter.toDouble())
            setLineDash(paint.strokeDashList)

            stroke()
        }

        internal fun Context2d.fill(paint: Paint) {
            require(!paint.isStroke) { "Paint must be a fill paint" }

            setFillStyle(paint.color)

            fill()
        }

    }
}