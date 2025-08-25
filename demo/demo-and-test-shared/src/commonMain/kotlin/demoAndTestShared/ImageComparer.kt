/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package demoAndTestShared

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasProvider

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
        val testName = expectedFileName.removeSuffix(".png") + if (suffix.isNotEmpty()) "_${suffix.lowercase()}" else ""
        val expectedFilePath = "$expectedDir$testName.png"
        val actualFilePath = "$outDir$testName.png"

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

        val diffBitmap = createDiffImage(expectedBitmap, actualBitmap)
        if (diffBitmap != null) {
            val diffFilePath = outDir + "${testName}_diff.png"
            val visualDiffBitmap = composeVisualDiff(expectedBitmap, actualBitmap, diffBitmap)

            bitmapIO.write(visualDiffBitmap, diffFilePath)
            bitmapIO.write(actualBitmap, actualFilePath)
            val diffDataImage = Png.encodeDataImage(visualDiffBitmap)

            error(
                """Image mismatch.
                |    Diff: $diffFilePath
                |    Actual: $actualFilePath
                |    Expected: $expectedFilePath
                |    Diff DataImage: $diffDataImage""".trimMargin()
            )
        } else {
            println("Image comparison passed: $expectedFilePath")
        }
    }

    private fun createDiffImage(expected: Bitmap, actual: Bitmap, tolerance: Int = tol): Bitmap? {
        val diffWidth = maxOf(expected.width, actual.width)
        val diffHeight = maxOf(expected.height, actual.height)
        val diffCanvas = canvasProvider.createCanvas(diffWidth, diffHeight)
        diffCanvas.context2d.setFillStyle(Color.TRANSPARENT)
        diffCanvas.context2d.fillRect(0.0, 0.0, diffWidth.toDouble(), diffHeight.toDouble())

        diffCanvas.context2d.setFillStyle(Color.RED)

        var match = true

        for (y in 0 until diffHeight) {
            for (x in 0 until diffWidth) {
                val expectedPixel = expected.getPixel(x, y)
                val actualPixel = actual.getPixel(x, y)

                if (expectedPixel != null && actualPixel != null) {
                    if (expectedPixel.green - actualPixel.green > tolerance ||
                        expectedPixel.red - actualPixel.red > tolerance ||
                        expectedPixel.blue - actualPixel.blue > tolerance ||
                        expectedPixel.alpha - actualPixel.alpha > tolerance
                    ) {
                        match = false
                        diffCanvas.context2d.fillRect(x.toDouble(), y.toDouble(), 1.0, 1.0)
                    }
                } else {
                    // If one of the pixels is null, consider it a difference
                    match = false
                    diffCanvas.context2d.fillRect(x.toDouble(), y.toDouble(), 1.0, 1.0)
                }
            }
        }

        if (match) {
            return null // No differences found
        }

        return diffCanvas.takeSnapshot().bitmap
    }

    private fun composeVisualDiff(
        expectedBitmap: Bitmap,
        actualBitmap: Bitmap,
        diffBitmap: Bitmap
    ): Bitmap {
        val separatorSize = 10

        val width = maxOf(expectedBitmap.width, actualBitmap.width)
        val height = maxOf(expectedBitmap.height, actualBitmap.height)

        val totalWidth = width * 2 + separatorSize
        val totalHeight = height * 2 + separatorSize

        val canvas = canvasProvider.createCanvas(totalWidth, totalHeight)
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

        return canvas.takeSnapshot().bitmap
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
