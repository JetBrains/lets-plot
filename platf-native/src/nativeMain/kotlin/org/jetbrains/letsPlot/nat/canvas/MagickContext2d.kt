/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.canvas

import MagickWand.*
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Context2dDelegate

class MagickContext2d(
    private val wand: CPointer<MagickWand>?
) : Context2d by Context2dDelegate() {

    private val drawingWand: CPointer<DrawingWand>? = NewDrawingWand()

    override fun setFillStyle(color: Color?) {
        memScoped {
            val pixelWand = NewPixelWand()
            require(pixelWand != null) { "Failed to create PixelWand" }

            // Set fill color (default to black, modify as needed)
            PixelSetColor(pixelWand, color?.toHexColor() ?: "black")

            // Apply fill color to drawing
            DrawSetFillColor(drawingWand, pixelWand)

            DestroyPixelWand(pixelWand) // Cleanup
        }
    }

    override fun setStrokeStyle(color: Color?) {
        memScoped {
            val pixelWand = NewPixelWand()
            require(pixelWand != null) { "Failed to create PixelWand" }

            // Set stroke color (default to black, modify as needed)
            PixelSetColor(pixelWand, color?.toHexColor() ?: "black")

            // Apply stroke color to drawing
            DrawSetStrokeColor(drawingWand, pixelWand)

            DestroyPixelWand(pixelWand) // Cleanup
        }
    }

    override fun fillText(text: String, x: Double, y: Double) {
        memScoped {
            // Draw the text
            val textCStr = text.cstr.ptr.reinterpret<UByteVar>()
            DrawAnnotation(drawingWand, x, y, textCStr)

            // Apply drawing to image
            MagickDrawImage(wand, drawingWand)
        }
    }

    override fun moveTo(x: Double, y: Double) {
        println("MagickContext2d.moveTo(wand=$wand, x=$x, y=$y)")
    }

    override fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        // Draw the rectangle
        DrawRectangle(drawingWand, x, y, x + w, y + h)

        // Apply drawing to the MagickWand image
        MagickDrawImage(wand, drawingWand)
    }
}