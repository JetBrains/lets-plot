/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package demoAndTestShared

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider
import kotlin.math.abs

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class ImageComparer(
    private val canvasProvider: CanvasProvider,
    private val bitmapIO: BitmapIO,
    private val expectedDir: String, // Adjusted path
    private val outDir: String,
    private val tol: Int = 1,
    private val suffix: String = ""
) {


    fun assertBitmapEquals(expectedFileName: String, actualBitmap: Bitmap) {
        val testName = expectedFileName.removeSuffix(".bmp") + if (suffix.isNotEmpty()) "_${suffix.lowercase()}" else ""
        val expectedFilePath = "$expectedDir$testName.bmp"
        val actualFilePath = "$outDir$testName.bmp"

        val expectedBitmap = runCatching { bitmapIO.read(expectedFilePath) }.getOrElse {
            println("expectedWand failure - $expectedFilePath")
            println(it)
            runCatching { bitmapIO.write(actualBitmap, actualFilePath) }.onFailure {
                println("actualBitmap failure - $actualFilePath")
                println(it)
                return
            }
            println("Failed to read expected image. Actual image saved to $actualFilePath")
            error("Failed to read expected image. Actual image saved to '$actualFilePath'")
        }

        if (!comparePixelArrays(expectedBitmap, actualBitmap, tolerance = 0)) {
            val diffFilePath = outDir + "${testName}_diff.bmp"

            val diffBitmap = createDiffImage(expectedBitmap, actualBitmap)
            val visualDiffCanvas = composeVisualDiff(expectedBitmap, actualBitmap, diffBitmap)
            val visualDiffBitmap = visualDiffCanvas.takeSnapshot().bitmap

            bitmapIO.write(visualDiffBitmap, diffFilePath)
            bitmapIO.write(actualBitmap, actualFilePath)

            error("""Image mismatch.
                |    Diff: $diffFilePath
                |    Actual: $actualFilePath
                |    Expected: $expectedFilePath""".trimMargin()
            )
        } else {
            println("Image comparison passed: $expectedFilePath")
        }
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

        val diffPixels = ByteArray(width * height * 4)
        for (i in 0 until width * height) {
            val offset = i * 4
            val match = (0 until 4).all { k ->
                abs(expectedPixels[offset + k].toInt() - actualPixels[offset + k].toInt()) <= tol
            }

            if (!match) {
                // Red pixel for difference
                diffPixels[offset + 0] = 255.toByte() // R
                diffPixels[offset + 1] = 0           // G
                diffPixels[offset + 2] = 0           // B
                diffPixels[offset + 3] = 255.toByte() // A
            } else {
                // Fully transparent for matching pixels
                diffPixels[offset + 0] = 0
                diffPixels[offset + 1] = 0
                diffPixels[offset + 2] = 0
                diffPixels[offset + 3] = 0
            }
        }

        return Bitmap.fromRGBABytes(w = width, h = height, rgba = diffPixels)
    }

    private fun composeVisualDiff(
        expectedBitmap: Bitmap,
        actualBitmap: Bitmap,
        diffBitmap: Bitmap
    ): Canvas {
        val width = expectedBitmap.width
        val height = expectedBitmap.height
        val separatorSize = 10

        val totalWidth = width * 2 + separatorSize
        val totalHeight = height * 2 + separatorSize

        val canvas = canvasProvider.createCanvas(Vector(totalWidth, totalHeight))
        val ctx = canvas.context2d

        ctx.setFillStyle(Color.WHITE)
        ctx.fillRect(0.0, 0.0, totalWidth.toDouble(), totalHeight.toDouble())

        val expectedSnapshot = canvasProvider.createSnapshot(expectedBitmap)
        val actualSnapshot = canvasProvider.createSnapshot(actualBitmap)
        val diffSnapshot = canvasProvider.createSnapshot(addBorderToBitmap(diffBitmap, 1, Color.GRAY))

        val vertSeparatorSnapshot = createZigZagPattern(separatorSize, height).takeSnapshot()
        val horizSeparatorSnapshot = createZigZagPattern(totalWidth, separatorSize).takeSnapshot()

        // --- Composite images onto the canvas ---
        // Expected image (top-left)
        ctx.drawImage(expectedSnapshot, 0.0, 0.0)

        // Vertical separator
        ctx.drawImage(vertSeparatorSnapshot, width.toDouble(), 0.0)

        // Actual image (top-right)
        ctx.drawImage(actualSnapshot, (width + separatorSize).toDouble(), 0.0)

        // Horizontal separator
        ctx.drawImage(horizSeparatorSnapshot, 0.0, height.toDouble())

        // Diff image (bottom-centered)
        val diffWidth = diffSnapshot.size.x
        val diffX = ((totalWidth - diffWidth) / 2).toDouble()
        val diffY = (height + separatorSize).toDouble()
        ctx.drawImage(diffSnapshot, diffX, diffY)

        return canvas
    }

    private fun createZigZagPattern(width: Int, height: Int): Canvas {
        val canvas = canvasProvider.createCanvas(width, height)
        val ctx = canvas.context2d

        ctx.setFillStyle(Color.GRAY)
        ctx.fillRect(0.0, 0.0, width.toDouble(), height.toDouble())

        ctx.setStrokeStyle(Color.WHITE)
        ctx.setLineWidth(1.0)
        ctx.beginPath()
        for (i in -height until width step 4) { // Start from negative to cover the top-left corner
            ctx.moveTo(i.toDouble(), 0.0)
            ctx.lineTo(i.toDouble() + height, height.toDouble())
        }
        ctx.stroke()

        return canvas
    }

    private fun addBorderToBitmap(
        bitmap: Bitmap,
        borderSize: Int = 1,
        color: Color = Color.BLACK
    ): Bitmap {
        val newWidth = bitmap.width + borderSize * 2
        val newHeight = bitmap.height + borderSize * 2
        val canvas = canvasProvider.createCanvas(newWidth, newHeight)
        val ctx = canvas.context2d

        // Draw border rectangle
        ctx.setFillStyle(color)
        ctx.fillRect(0.0, 0.0, newWidth.toDouble(), newHeight.toDouble())

        // Draw original image inside the border
        ctx.drawImage(canvasProvider.createSnapshot(bitmap), borderSize.toDouble(), borderSize.toDouble())

        return canvas.takeSnapshot().bitmap
    }

    interface BitmapIO {
        fun write(bitmap: Bitmap, filePath: String)
        fun read(filePath: String): Bitmap
    }
}
