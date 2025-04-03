import kotlinx.cinterop.*
import kotlin.test.Ignore
import kotlin.test.Test

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// This test class is used to demonstrate the usage of the ImageMagick library
@Ignore
class MagicWandTest {

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun simple() {
        // Initialize the MagickWand environment
        ImageMagick.MagickWandGenesis()
        // Create a MagickWand instance
        val wand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        try {
            val w = 50
            val h = 50

            // Set the canvas size and background color
            val background = ImageMagick.NewPixelWand()
            ImageMagick.PixelSetColor(background, "white")
            ImageMagick.MagickNewImage(wand, w.toULong(), h.toULong(), background)

            // Draw a rectangle
            val draw = ImageMagick.NewDrawingWand()
            val pixel = ImageMagick.NewPixelWand()

            // Set rectangle color
            ImageMagick.PixelSetColor(pixel, "orange")
            ImageMagick.DrawSetFillColor(draw, pixel)

            // Draw the rectangle
            ImageMagick.DrawRectangle(draw, 10.0, 10.0, 40.0, 40.0)

            // Set circle color
            ImageMagick.PixelSetColor(pixel, "red")
            ImageMagick.DrawSetFillColor(draw, pixel)

            // Draw the circle inside the rectangle
            ImageMagick.DrawCircle(draw, 25.0, 25.0, 25.0, 20.0)

            // Draw text with a font name
            ImageMagick.PixelSetColor(pixel, "black") // Set text color
            ImageMagick.DrawSetFillColor(draw, pixel)
            ImageMagick.DrawSetFont(draw, "DejaVu-Sans-Bold") // Use font name
            ImageMagick.DrawSetFontSize(draw, 36.0) // Set font size

            memScoped {
                val text: CValues<ByteVar> = "Hello, MagicWand!".cstr // Convert to C string
                //DrawAnnotation(draw, 150.0, 300.0, text.getPointer(this)) // Get pointer within scope
            }

            // Apply the drawing to the MagickWand
            ImageMagick.MagickDrawImage(wand, draw)

            // Never saves the image - this is just a test to check if the code compiles and runs
            val outputFilename = "simple.bmp"
            if (false && ImageMagick.MagickWriteImage(wand, outputFilename) == ImageMagick.MagickFalse) {
                throw RuntimeException("Failed to write image")
            }

            println("Image saved to $outputFilename")

            val pixelData = ByteArray(w * h * 4) // RGBA 8-bit per channel
            memScoped {
                val byteBuffer = pixelData.refTo(0) // Pointer to ByteArray
                val success = ImageMagick.MagickExportImagePixels(
                    wand,
                    0, 0, w.toULong(), h.toULong(),
                    "RGBA", ImageMagick.StorageType.CharPixel, byteBuffer
                )
                require(success == ImageMagick.MagickTrue) { "Failed to export pixels" }

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
            ImageMagick.DestroyMagickWand(wand)
            ImageMagick.MagickWandTerminus()
        }
    }

    @Test
    fun miterJoinWithOppositeSegments() {
        ImageMagick.MagickWandGenesis()
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create MagickWand")

        val backgroundWand = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundWand, "white")
        ImageMagick.MagickNewImage(magickWand, 100u, 100u, backgroundWand)

        val drawingWand = ImageMagick.NewDrawingWand()
        val strokeWand = ImageMagick.NewPixelWand()

        ImageMagick.PixelSetColor(strokeWand, "orange")
        ImageMagick.DrawSetStrokeColor(drawingWand, strokeWand)

        ImageMagick.DrawSetStrokeWidth(drawingWand, 20.0)

        // MiterJoin with a line in the opposite direction
        ImageMagick.DrawSetStrokeLineJoin(drawingWand, ImageMagick.LineJoin.MiterJoin)
        ImageMagick.DrawPathStart(drawingWand)
        ImageMagick.DrawPathMoveToAbsolute(drawingWand, 5.0, 30.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 95.0, 30.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 5.0, 30.0)
        ImageMagick.DrawPathFinish(drawingWand)

        // BevelJoin with a line in the opposite direction
        ImageMagick.DrawSetStrokeLineJoin(drawingWand, ImageMagick.LineJoin.BevelJoin)
        ImageMagick.DrawPathStart(drawingWand)
        ImageMagick.DrawPathMoveToAbsolute(drawingWand, 5.0, 70.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 95.0, 70.0)
        ImageMagick.DrawPathLineToAbsolute(drawingWand, 5.0, 70.0)
        ImageMagick.DrawPathFinish(drawingWand)


        ImageMagick.MagickDrawImage(magickWand, drawingWand)
        val outputFilename = "miter_join_artifact.bmp"

        // Never saves the image - this is just a test to check if the code compiles and runs
        if (false && ImageMagick.MagickWriteImage(magickWand, outputFilename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }

        ImageMagick.DestroyPixelWand(backgroundWand)
        ImageMagick.DestroyPixelWand(strokeWand)
        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyMagickWand(magickWand)
        ImageMagick.MagickWandTerminus()
    }
}
