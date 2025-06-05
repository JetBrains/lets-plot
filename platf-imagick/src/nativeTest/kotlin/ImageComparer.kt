
import demoAndTestShared.parsePlotSpec
import kotlinx.cinterop.*
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvas
import org.jetbrains.letsPlot.imagick.canvas.MagickCanvasControl
import org.jetbrains.letsPlot.raster.builder.MonolithicCanvas
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure
import platform.posix.*
import kotlin.math.abs

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class ImageComparer(
    // Reuse existing directories when possible.
    // `mkdirs` has different signatures: one parameter on Windows, two on Linux.
    // To avoid compilation errors, we’d need a Windows-specific source set.
    private val expectedDir: String = getCurrentDir() + "/src/nativeTest/resources/expected/",
    private val outDir: String = getCurrentDir() + "/build/reports/",
    private val tol: Int = 1
) {

    fun assertImageEquals(expectedFileName: String, svg: SvgSvgElement) {
        val w = svg.width().get()?.toInt() ?: error("SVG width is not specified")
        val h = svg.height().get()?.toInt() ?: error("SVG height is not specified")
        val canvasControl = MagickCanvasControl(w = w, h = h, pixelDensity = 1.0)
        SvgCanvasFigure(svg).mapToCanvas(canvasControl)

        val canvas = canvasControl.children.single() as MagickCanvas
        assertImageEquals(expectedFileName, canvas.img)
    }

    fun assertImageEquals(expectedFileName: String, spec: String) {
        val plotSpec = parsePlotSpec(spec)

        val plotFigure = MonolithicCanvas.buildPlotFigureFromRawSpec(plotSpec, SizingPolicy.keepFigureDefaultSize(), { _ -> })
        val plotSpecWidth = plotFigure.preferredWidth ?: error("Plot figure has no preferred width")
        val plotSpecHeight = plotFigure.preferredHeight ?: error("Plot figure has no preferred height")

        val canvasControl = MagickCanvasControl(plotSpecWidth, plotSpecHeight, 1.0)
        plotFigure.mapToCanvas(canvasControl)

        val canvas = canvasControl.children.single() as MagickCanvas
        assertImageEquals(expectedFileName, canvas.img)
    }

    fun assertImageEquals(expectedFileName: String, actualWand: CPointer<ImageMagick.MagickWand>) {
        val testName = expectedFileName.removeSuffix(".bmp")
        val expectedPath = expectedDir + expectedFileName
        val actualFilePath = outDir + "${testName}.bmp"

        val expectedWand = ImageMagick.NewMagickWand() ?: error("Failed to create expected wand")
        if (ImageMagick.MagickReadImage(expectedWand, expectedPath) == ImageMagick.MagickFalse) {
            println("expectedWand failure - $expectedPath")
            println(getMagickError(expectedWand))
            // Write the  actual image to a file for debugging
            if (ImageMagick.MagickWriteImage(actualWand, actualFilePath) == ImageMagick.MagickFalse) {
                println("actualWand failure - $actualFilePath")
                println(getMagickError(actualWand))
            } else {
                println("Failed to read expected image. Actual image saved to $actualFilePath")
            }
            error("Failed to read expected image. Actual image saved to '$actualFilePath'")
        }

        val expected = exportPixels(expectedWand)
        val actual = exportPixels(actualWand)

        if (!comparePixelArrays(expected, actual, tolerance = 0)) {
            val diffFilePath = outDir + "${testName}_diff.bmp"
            val width = ImageMagick.MagickGetImageWidth(actualWand).toInt()
            val height = ImageMagick.MagickGetImageHeight(actualWand).toInt()
            val diffWand = composeVisualDiff(expectedWand, actualWand, createDiffImage(expected, actual, width, height))
            if (ImageMagick.MagickWriteImage(diffWand, diffFilePath) == ImageMagick.MagickFalse) {
                println(getMagickError(diffWand))
                error("Failed to write diff image")
            }

            if (ImageMagick.MagickWriteImage(actualWand, actualFilePath) == ImageMagick.MagickFalse) {
                println(getMagickError(actualWand))
                error("Failed to write actual image")
            }

            error("""Image mismatch.
                |    Diff: $diffFilePath
                |    Actual: $actualFilePath
                |    Expected: $expectedPath""".trimMargin()
            )
        } else {
            println("Image comparison passed: $expectedPath")
        }
    }

    fun compare(expectedImagePath: String, actualImagePath: String) {
        val expected = ImageMagick.NewMagickWand()
        ImageMagick.MagickReadImage(expected, expectedImagePath/*"resources/expected/test_name.bmp"*/)

        val actual = ImageMagick.NewMagickWand()
        ImageMagick.MagickReadImage(actual, actualImagePath)
    }

    fun exportPixels(wand: CPointer<ImageMagick.MagickWand>): UByteArray {
        val width = ImageMagick.MagickGetImageWidth(wand).toInt()
        val height = ImageMagick.MagickGetImageHeight(wand).toInt()
        val pixels = UByteArray(width * height * 4) // RGBA
        val success = ImageMagick.MagickExportImagePixels(
            wand, 0, 0, width.toULong(), height.toULong(),
            "RGBA",
            ImageMagick.StorageType.CharPixel,
            pixels.refTo(0)
        )
        if (success == ImageMagick.MagickFalse) error("Failed to export pixels")
        return pixels
    }

    fun pixelsEqual(p1: UByte, p2: UByte, tolerance: Int): Boolean {
        return abs(p1.toInt() - p2.toInt()) <= tolerance
    }

    fun comparePixelArrays(expected: UByteArray, actual: UByteArray, tolerance: Int = tol): Boolean {
        if (expected.size != actual.size) return false
        return expected.indices.all { pixelsEqual(expected[it], actual[it], tolerance) }
    }

    fun createDiffImage(
        expected: UByteArray,
        actual: UByteArray,
        width: Int,
        height: Int
    ): CPointer<ImageMagick.MagickWand> {
        val diff = ImageMagick.NewMagickWand() ?: error("Failed to create diff image")
        val blackPixel = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(blackPixel, "black")

        ImageMagick.MagickNewImage(diff, width.toULong(), height.toULong(), blackPixel)

        val diffPixels = UByteArray(width * height * 4)
        for (i in 0 until width * height) {
            val ei = i * 4
            val ai = i * 4
            val match = (0 until 4).all { k ->
                abs(expected[ei + k].toInt() - actual[ai + k].toInt()) <= 0
            }

            if (!match) {
                // red pixel
                diffPixels[ei + 0] = 255u // R
                diffPixels[ei + 1] = 0u   // G
                diffPixels[ei + 2] = 0u   // B
                diffPixels[ei + 3] = 255u // A
            } else {
                diffPixels[ei + 3] = 0u // transparent
            }
        }

        val ok = ImageMagick.MagickImportImagePixels(
            diff, 0, 0, width.toULong(), height.toULong(),
            "RGBA",
            ImageMagick.StorageType.CharPixel,
            diffPixels.refTo(0)
        )
        if (ok == ImageMagick.MagickFalse) error("Failed to import diff pixels")
        return diff
    }

    fun composeVisualDiff(
        expected: CPointer<ImageMagick.MagickWand>,
        actual: CPointer<ImageMagick.MagickWand>,
        diff: CPointer<ImageMagick.MagickWand>
    ): CPointer<ImageMagick.MagickWand> {
        val width = ImageMagick.MagickGetImageWidth(expected).toInt()
        val height = ImageMagick.MagickGetImageHeight(expected).toInt()
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
    fun getMagickError(wand: CPointer<ImageMagick.MagickWand>?): String {
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
