/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.util

//import MagickWand.*
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.util.PlotSvgExportCommon
import org.jetbrains.letsPlot.nat.encoding.RGBEncoderNative


actual object PlotSvgExportNative {
    /**
     * @param plotSpec Raw specification of a plot.
     * @param plotSize Desired plot size.
     * @param useCssPixelatedImageRendering true for CSS style "pixelated", false for SVG style "optimizeSpeed". Used for compatibility.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    actual fun buildSvgImageFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        useCssPixelatedImageRendering: Boolean
    ): String {
        testImageWand()
        return PlotSvgExportCommon.buildSvgImageFromRawSpecs(
            plotSpec = plotSpec,
            plotSize = plotSize,
            rgbEncoder = RGBEncoderNative(),
            useCssPixelatedImageRendering
        )
    }
}


fun testImageWand() {
    /*
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

        val text = "Hello, Kotlin/Native and ImageMagick!"

        // Correct way to pass the string to DrawAnnotation:
        val utf8Bytes: UByteArray = text.encodeToByteArray().toUByteArray() // Convert to UTF-8 ByteArray

        utf8Bytes.usePinned { pinned ->
            DrawAnnotation(draw, 150.0, 300.0, pinned.addressOf(0))
        }

        // Apply the drawing to the MagickWand
        MagickDrawImage(wand, draw)

        memScoped {
            val numFormats = alloc<ULongVar>() // Allocate space for the output parameter

            val formats = MagickQueryFormats("*", numFormats.ptr) // Corrected call
            if (formats == null) {
                println("Failed to retrieve available formats")
            } else {
                val formatList = (0 until numFormats.value.toInt()).map { index ->
                    formats[index]?.toKString() ?: "Unknown"
                }
                println("Supported Formats: $formatList")
            }
        }

        // Save the image to a file
        val outputFilename = "output_with_text.png"
        MagickSetImageFormat(wand, "PNG")
        if (MagickWriteImage(wand, outputFilename) == MagickFalse) {
            memScoped {
                val severity = alloc<ExceptionTypeVar>() // Allocate memory for severity
                val messagePtr = MagickGetException(wand, severity.ptr) // Get exception message pointer
                println(messagePtr?.toKString() ?: "Unknown error") // Convert to Kotlin string

            }
            throw RuntimeException("Failed to write image")
        }

        println("Image saved to $outputFilename")
    } finally {
        // Clean up resources
        DestroyMagickWand(wand)
        MagickWandTerminus()
    }
*/
}
