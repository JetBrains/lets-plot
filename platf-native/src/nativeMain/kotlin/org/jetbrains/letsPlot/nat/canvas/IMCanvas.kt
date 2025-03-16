/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.canvas
/*
import MagickWand.*
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CValues
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped

class IMCanvas {
    init {
        MagickWandGenesis()
        val wand = NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        try {
            // Set the canvas size and background color
            val background = NewPixelWand()
            PixelSetColor(background, "white")
            MagickNewImage(wand, 500U, 500U, background)

            // Draw a rectangle
            val draw = NewDrawingWand()
            val pixel = NewPixelWand()

            // Set rectangle color
            PixelSetColor(pixel, "blue")
            DrawSetFillColor(draw, pixel)

            // Draw the rectangle
            DrawRectangle(draw, 100.0, 100.0, 400.0, 400.0)

            // Set circle color
            PixelSetColor(pixel, "red")
            DrawSetFillColor(draw, pixel)

            // Draw the circle inside the rectangle
            DrawCircle(draw, 250.0, 250.0, 250.0, 200.0)

            // Draw text with a font name
            PixelSetColor(pixel, "black") // Set text color
            DrawSetFillColor(draw, pixel)
            DrawSetFont(draw, "DejaVu-Sans-Bold") // Use font name
            DrawSetFontSize(draw, 36.0) // Set font size

            memScoped {
                val text: CValues<ByteVar> = "Hello, MagicWand!".cstr // Convert to C string
                //DrawAnnotation(draw, 150.0, 300.0, text.getPointer(this)) // Get pointer within scope
            }

            // Apply the drawing to the MagickWand
            MagickDrawImage(wand, draw)

            // Save the image to a file
            val outputFilename = "output_with_text.png"
            //if (MagickWriteImage(wand, outputFilename) == MagickFalse) {
            //    throw RuntimeException("Failed to write image")
            //}

            println("Image saved to $outputFilename")
        } finally {
            // Clean up resources
            DestroyMagickWand(wand)
            MagickWandTerminus()
        }

    }
}

 */