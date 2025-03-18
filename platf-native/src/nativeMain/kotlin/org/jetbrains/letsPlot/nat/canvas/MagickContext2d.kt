/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.canvas

import MagickWand.*
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.memScoped
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Context2dDelegate

class MagickContext2d(
    private val wand: CPointer<MagickWand>?
) : Context2d by Context2dDelegate() {
    private val drawingWand: CPointer<DrawingWand>? = NewDrawingWand()
    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        memScoped {
            val pixelWand = NewPixelWand()
            require(pixelWand != null) { "Failed to create PixelWand" }

            // Set fill color (default to black, modify as needed)
            PixelSetColor(pixelWand, "orange")

            // Apply fill color to drawing
            DrawSetFillColor(drawingWand, pixelWand)

            // Draw the rectangle
            DrawRectangle(drawingWand, x, y, x + w, y + h)

            // Apply drawing to the MagickWand image
            MagickDrawImage(wand, drawingWand)

            DestroyPixelWand(pixelWand) // Cleanup
        }
    }
}