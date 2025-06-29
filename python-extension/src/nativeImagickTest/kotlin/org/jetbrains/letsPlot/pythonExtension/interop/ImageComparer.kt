/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.imagick.canvas.MagickFontManager
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import platform.posix.getcwd
import kotlin.math.abs

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

// TODO: rewrite diff composition logic with Canvas and Context2D and move to canvas module
class ImageComparer(
    // Reuse existing directories when possible.
    // `mkdirs` has different signatures: one parameter on Windows, two on Linux.
    // To avoid compilation errors, we’d need a Windows-specific source set.
    private val expectedDir: String = getCurrentDir() + "/src/nativeImagickTest/resources/expected/",
    private val outDir: String = getCurrentDir() + "/build/reports/",
    private val tol: Int = 1,
    private val suffix: String = ""
) {

    fun assertSvg(expectedFileName: String, svg: SvgSvgElement) {
        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvasControl = MagickCanvasControl(w = w, h = h, pixelDensity = 1.0, fontManager = MagickFontManager.DEFAULT)
        SvgCanvasFigure(svg).mapToCanvas(canvasControl)

        val canvas = canvasControl.children.single() as MagickCanvas
        assertBitmapEquals(expectedFileName, canvas.takeSnapshot().bitmap)
    }

    fun assertPlot(
        expectedFileName: String,
        plotSpec: MutableMap<String, Any>,
        width: Int? = null,
        height: Int? = null,
        pixelDensity: Double = 1.0
    ) {
        val bitmap = PlotReprGenerator.exportBitmap(plotSpec, width, height, pixelDensity)
        if (bitmap == null)  error("Failed to export bitmap from plot spec")
        assertBitmapEquals(expectedFileName, bitmap)
    }

    private fun assertBitmapEquals(expectedFileName: String, actualBitmap: Bitmap) {
        val testName = expectedFileName.removeSuffix(".bmp") + if (suffix.isNotEmpty()) "_${suffix.lowercase()}" else ""
        val expectedFilePath = expectedDir + testName + ".bmp"
        val actualFilePath = outDir + testName + ".bmp"

        val expectedBitmap = runCatching { readBitmapFromFile(expectedFilePath) }.getOrElse {
            println("expectedWand failure - $expectedFilePath")
            println(it)
            // Write the  actual image to a file for debugging
            runCatching { writeBitmapToFile(actualBitmap, actualFilePath) }.onFailure {
                println("actualBitmap failure - $actualFilePath")
                println(it)
                return
            }
            println("Failed to read expected image. Actual image saved to $actualFilePath")
            error("Failed to read expected image. Actual image saved to '$actualFilePath'")
        }

        if (!comparePixelArrays(expectedBitmap, actualBitmap, tolerance = 0)) {
            val diffFilePath = outDir + "${testName}_diff.bmp"
            val diffWand = composeVisualDiff(expectedBitmap, actualBitmap, createDiffImage(expectedBitmap, actualBitmap))
            if (ImageMagick.MagickWriteImage(diffWand, diffFilePath) == ImageMagick.MagickFalse) {
                println(getMagickError(diffWand))
                error("Failed to write diff image")
            }

            runCatching { writeBitmapToFile(actualBitmap, actualFilePath) }
                .onFailure {
                    println(it)
                    error("Failed to write actual image")
                }

            error("""Image mismatch.
                |    Diff: $diffFilePath
                |    Actual: $actualFilePath
                |    Expected: $expectedFilePath""".trimMargin()
            )
        } else {
            println("Image comparison passed: $expectedFilePath")
        }
    }

    private fun compare(expectedImagePath: String, actualImagePath: String) {
        val expected = ImageMagick.NewMagickWand()
        ImageMagick.MagickReadImage(expected, expectedImagePath/*"resources/expected/test_name.bmp"*/)

        val actual = ImageMagick.NewMagickWand()
        ImageMagick.MagickReadImage(actual, actualImagePath)
    }

    private fun comparePixelArrays(expected: Bitmap, actual: Bitmap, tolerance: Int = tol): Boolean {
        if (expected.height != actual.height) return false
        if (expected.width != actual.width) return false

        val expectedPixels = expected.rgbaBytes()
        val actualPixels = actual.rgbaBytes()

        return expectedPixels.indices.all {
            abs(expectedPixels[it].toInt() - actualPixels[it].toInt()) <= tolerance
        }
    }

    private fun createDiffImage(
        expected: Bitmap,
        actual: Bitmap
    ): Bitmap {
        val width = expected.width
        val height = expected.height
        val expectedPixels = expected.rgbaBytes()
        val actualPixels = actual.rgbaBytes()

        val diff = ImageMagick.NewMagickWand() ?: error("Failed to create diff image")
        val blackPixel = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(blackPixel, "black")

        ImageMagick.MagickNewImage(diff, width.toULong(), height.toULong(), blackPixel)

        val diffPixels = ByteArray(width * height * 4)
        for (i in 0 until width * height) {
            val ei = i * 4
            val ai = i * 4
            val match = (0 until 4).all { k ->
                abs(expectedPixels[ei + k].toInt() - actualPixels[ai + k].toInt()) <= 0
            }

            if (!match) {
                // red pixel
                diffPixels[ei + 0] = 255.toByte() // R
                diffPixels[ei + 1] = 0   // G
                diffPixels[ei + 2] = 0   // B
                diffPixels[ei + 3] = 255.toByte() // A
            } else {
                diffPixels[ei + 3] = 0 // transparent
            }
        }

        val ok = ImageMagick.MagickImportImagePixels(
            diff, 0, 0, width.toULong(), height.toULong(),
            "RGBA",
            ImageMagick.StorageType.CharPixel,
            diffPixels.refTo(0)
        )
        if (ok == ImageMagick.MagickFalse) error("Failed to import diff pixels")
        //return diff
        val diffBitmap = Bitmap.fromRGBABytes(w = width, h = height, rgba = diffPixels)
        return diffBitmap
    }

    private fun composeVisualDiff(
        expectedBitmap: Bitmap,
        actualBitmap: Bitmap,
        diffBitmap: Bitmap
    ): CPointer<ImageMagick.MagickWand> {
        val width = expectedBitmap.width
        val height = expectedBitmap.height

        val expected = MagickUtil.fromBitmap(expectedBitmap)
        val actual = MagickUtil.fromBitmap(actualBitmap)
        val diff = MagickUtil.fromBitmap(diffBitmap)

        val separatorWidth = 10

        val totalTopWidth = width * 2 + separatorWidth
        val totalHeight = height * 2 + separatorWidth

        val canvas = ImageMagick.NewMagickWand()!!
        val white = ImageMagick.NewPixelWand()!!.apply {
            ImageMagick.PixelSetColor(this, "white")
        }
        ImageMagick.MagickNewImage(canvas, totalTopWidth.toULong(), totalHeight.toULong(), white)
        ImageMagick.DestroyPixelWand(white)

        // --- Separator texture ---
        val sep = createZigZagPattern(separatorWidth, height)

        // --- Border for diff ---
        val borderColor = ImageMagick.NewPixelWand()!!.apply {
            ImageMagick.PixelSetColor(this, "gray")
        }
        ImageMagick.MagickBorderImage(diff, borderColor, 1u, 1u, ImageMagick.CompositeOperator.OverCompositeOp)
        ImageMagick.DestroyPixelWand(borderColor)

        // --- Composite images ---
        ImageMagick.MagickCompositeImage(
            canvas,
            expected,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            0,
            0
        )
        ImageMagick.MagickCompositeImage(
            canvas,
            sep,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            width.toLong(),
            0
        )
        ImageMagick.MagickCompositeImage(
            canvas,
            actual,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            (width + separatorWidth).toLong(),
            0
        )

        // Add horizontal zigzag separator (same height as vertical one)
        val separatorSize = 10
        val horizSeparator = createZigZagPattern(totalTopWidth, separatorSize)

        val diffWidth = ImageMagick.MagickGetImageWidth(diff).toInt()
        val diffX = ((totalTopWidth - diffWidth) / 2).toLong()
        ImageMagick.MagickCompositeImage(
            canvas,
            diff,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            diffX,
            height.toLong() + separatorWidth
        )

        ImageMagick.MagickCompositeImage(
            canvas,
            horizSeparator,
            ImageMagick.CompositeOperator.OverCompositeOp,
            ImageMagick.MagickTrue,
            0,
            height.toLong() // Below top row
        )

        return canvas
    }

    private fun createZigZagPattern(width: Int, height: Int): CPointer<ImageMagick.MagickWand> {
        val wand = ImageMagick.NewMagickWand()!!
        val black = ImageMagick.NewPixelWand()!!
        ImageMagick.PixelSetColor(black, "grey")

        ImageMagick.MagickNewImage(wand, width.toULong(), height.toULong(), black)

        val white = ImageMagick.NewPixelWand()!!
        ImageMagick.PixelSetColor(white, "white")

        // Draw the pattern (diagonal lines across)
        val drawingWand = ImageMagick.NewDrawingWand()!!
        ImageMagick.DrawSetStrokeColor(drawingWand, white)
        ImageMagick.DrawSetStrokeWidth(drawingWand, 1.0)

        for (i in 0 until width step 4) {
            ImageMagick.DrawLine(drawingWand, i.toDouble(), 0.0, i.toDouble() + 4.0, height.toDouble())
        }

        ImageMagick.MagickDrawImage(wand, drawingWand)

        ImageMagick.DestroyDrawingWand(drawingWand)
        ImageMagick.DestroyPixelWand(black)
        ImageMagick.DestroyPixelWand(white)

        return wand
    }

    private fun addBorder(
        wand: CPointer<ImageMagick.MagickWand>,
        borderSize: Int = 1,
        color: String = "black"
    ): CPointer<ImageMagick.MagickWand> {
        val bordered = ImageMagick.CloneMagickWand(wand) ?: error("Failed to clone wand for border")

        val borderColor = ImageMagick.NewPixelWand()!!
        ImageMagick.PixelSetColor(borderColor, color)

        val ok = ImageMagick.MagickBorderImage(
            bordered,
            borderColor,
            borderSize.toULong(),
            borderSize.toULong(),
            ImageMagick.CompositeOperator.OverCompositeOp
        )
        ImageMagick.DestroyPixelWand(borderColor)

        if (ok == ImageMagick.MagickFalse) error("Failed to apply border")

        return bordered
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getMagickError(wand: CPointer<ImageMagick.MagickWand>?): String {
        require(wand != null) { "MagickWand is null" }

        return memScoped {
            val severity = alloc<ImageMagick.ExceptionTypeVar>()
            val messagePtr = ImageMagick.MagickGetException(wand, severity.ptr)

            if (messagePtr != null) {
                val errorMessage = messagePtr.toKString()
                ImageMagick.MagickRelinquishMemory(messagePtr)
                "ImageMagick Error: $errorMessage"
            } else {
                "Unknown ImageMagick error"
            }
        }
    }

    private fun writeBitmapToFile(bitmap: Bitmap, filePath: String) {
        val img = MagickUtil.fromBitmap(bitmap)
        if (ImageMagick.MagickWriteImage(img, filePath) == ImageMagick.MagickFalse) {
            val error = getMagickError(img)
            ImageMagick.DestroyMagickWand(img)
            error("Failed to write image to $filePath: $error")
        }

        ImageMagick.DestroyMagickWand(img)
    }

    private fun readBitmapFromFile(filePath: String): Bitmap {
        val img = ImageMagick.NewMagickWand() ?: error("Failed to create new MagickWand")
        if (ImageMagick.MagickReadImage(img, filePath) == ImageMagick.MagickFalse) {
            val error = getMagickError(img)
            ImageMagick.DestroyMagickWand(img)
            error("Failed to read image from $filePath: $error")
        }

        val bitmap = MagickUtil.toBitmap(img)
        ImageMagick.DestroyMagickWand(img)
        return bitmap
    }

}

fun getCurrentDir(): String {
    return memScoped {
        val bufferSize = 4096 * 8
        val buffer = allocArray<ByteVar>(bufferSize)
        if (getcwd(buffer, bufferSize.convert()) != null) {
            buffer.toKString()
        } else {
            "." // Default to current directory on error
        }
    }
}
