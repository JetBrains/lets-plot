import MagickWand.*
import kotlinx.cinterop.*
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


class ImageMagicTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun simple() {
        // Initialize the MagickWand environment
        MagickWandGenesis()
        // Create a MagickWand instance
        val wand = NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        try {
            val w = 50
            val h = 50

            // Set the canvas size and background color
            val background = NewPixelWand()
            PixelSetColor(background, "white")
            MagickNewImage(wand, w.toULong(), h.toULong(), background)

            // Draw a rectangle
            val draw = NewDrawingWand()
            val pixel = NewPixelWand()

            // Set rectangle color
            PixelSetColor(pixel, "orange")
            DrawSetFillColor(draw, pixel)

            // Draw the rectangle
            DrawRectangle(draw, 10.0, 10.0, 40.0, 40.0)

            // Set circle color
            PixelSetColor(pixel, "red")
            DrawSetFillColor(draw, pixel)

            // Draw the circle inside the rectangle
            DrawCircle(draw, 25.0, 25.0, 25.0, 20.0)

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
            val outputFilename = "output_with_text.bmp"
            if (MagickWriteImage(wand, outputFilename) == MagickFalse) {
                throw RuntimeException("Failed to write image")
            }

            println("Image saved to $outputFilename")

            val pixelData = ByteArray(w * h * 4) // RGBA 8-bit per channel
            memScoped {
                val byteBuffer = pixelData.refTo(0) // Pointer to ByteArray
                val success = MagickExportImagePixels(
                    wand,
                    0, 0, w.toULong(), h.toULong(),
                    "RGBA", StorageType.CharPixel, byteBuffer
                )
                require(success == MagickTrue) { "Failed to export pixels" }

                val lines = pixelData.asSequence().windowed(4 * w.toInt(), 4 * h.toInt()).toList()
                val strLines = lines.map { line ->
                    line.windowed(4, 4).joinToString { pixel ->
                        pixel.joinToString(separator = "") { it.toHexString() }
                    }
                }
                val str = strLines.joinToString("\n")
                println("Simple demo")
                println("${strLines.size} * ${lines[0].size}")
                println(str)
            }

        } finally {
            // Clean up resources
            DestroyMagickWand(wand)
            MagickWandTerminus()
        }

    }
}
