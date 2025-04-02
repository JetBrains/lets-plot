/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import org.jetbrains.letsPlot.core.canvas.Font
import org.jetbrains.letsPlot.core.canvas.TextMetrics

class TextMeasurer private constructor(
    private val canvas: Canvas
) {

    fun measureTextWidth(text: String, font: Font): Float {
        val ctx = canvas.context2d
        ctx.save()
        ctx.setFont(font)
        val width = ctx.measureTextWidth(text)
        ctx.restore()
        return width.toFloat()
    }

    fun measureText(text: String, font: Font): TextMetrics {
        val ctx = canvas.context2d
        ctx.save()
        ctx.setFont(font)
        val textMetrics = ctx.measureText(text)
        ctx.restore()

        return textMetrics
    }

    companion object {
        fun create(canvasProvider: CanvasProvider): TextMeasurer {
            return TextMeasurer(canvasProvider.createCanvas(Vector(0, 0)))
        }
    }
}
