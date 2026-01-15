/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import kotlin.math.abs

class ImageComparer(
    private val canvasPeer: CanvasPeer,
    private val bitmapIO: BitmapIO,
    private val tol: Int = 1,
    private val suffix: String = "",
    private val silent: Boolean = false
) {
    private fun log(message: Any) {
        if (!silent) {
            println(message)
        }
    }

    fun assertBitmapEquals(fileName: String, actualBitmap: Bitmap) {
        val testName = fileName.removeSuffix(".png") + if (suffix.isNotEmpty()) "_${suffix.lowercase()}" else ""
        val expectedFileName = "$testName.png"
        val actualFileName = "$testName.png"

        val expectedBitmap = runCatching { bitmapIO.read(expectedFileName) }.getOrElse {
            log("expectedBitmap failure - ${bitmapIO.getReadFilePath(expectedFileName)}")
            log(it)
            runCatching { bitmapIO.write(actualBitmap, actualFileName) }.onFailure {
                log("actualBitmap failure - ${bitmapIO.getWriteFilePath(actualFileName)}")
                log(it)
                return
            }
            log("Failed to read expected image. Actual image saved to file://${bitmapIO.getActualFileReportPath(actualFileName)}")
            error("Failed to read expected image. Actual image saved to 'file://${bitmapIO.getActualFileReportPath(actualFileName)}'")
        }

        val diffBitmap = createDiffImage(expectedBitmap, actualBitmap)
        if (diffBitmap != null) {
            val diffFilePath = "${testName}_diff.png"
            val visualDiffBitmap = composeVisualDiff(expectedBitmap, actualBitmap, diffBitmap)

            bitmapIO.write(visualDiffBitmap, diffFilePath)
            bitmapIO.write(actualBitmap, actualFileName)

            error(
                """Image mismatch.
                |    Diff: file://${bitmapIO.getDiffFileReportPath(diffFilePath)}
                |    Actual: file://${bitmapIO.getActualFileReportPath(actualFileName)}
                |    Expected: file://${bitmapIO.getExpectedFileReportPath(expectedFileName)}""".trimMargin()
            )
        } else {
            log("Image comparison passed: $expectedFileName")
        }
    }

    private fun createDiffImage(expected: Bitmap, actual: Bitmap, tolerance: Int = tol): Bitmap? {
        val diffWidth = maxOf(expected.width, actual.width)
        val diffHeight = maxOf(expected.height, actual.height)
        val diffCanvas = canvasPeer.createCanvas(diffWidth, diffHeight)
        diffCanvas.context2d.setFillStyle(Color.TRANSPARENT)
        diffCanvas.context2d.fillRect(0.0, 0.0, diffWidth.toDouble(), diffHeight.toDouble())

        diffCanvas.context2d.setFillStyle(Color.RED)

        var diffPixelsCount = 0

        for (y in 0 until diffHeight) {
            for (x in 0 until diffWidth) {
                val expectedPixel = expected.getPixel(x, y)
                val actualPixel = actual.getPixel(x, y)

                if (expectedPixel != null && actualPixel != null) {
                    if (abs(expectedPixel.green - actualPixel.green) > tolerance ||
                        abs(expectedPixel.red - actualPixel.red) > tolerance ||
                        abs(expectedPixel.blue - actualPixel.blue) > tolerance ||
                        abs(expectedPixel.alpha - actualPixel.alpha) > tolerance
                    ) {
                        diffPixelsCount++
                        diffCanvas.context2d.fillRect(x.toDouble(), y.toDouble(), 1.0, 1.0)
                    }
                } else {
                    // If one of the pixels is null, consider it a difference
                    diffPixelsCount++
                    diffCanvas.context2d.fillRect(x.toDouble(), y.toDouble(), 1.0, 1.0)
                }
            }
        }

        if (diffPixelsCount == 0) {
            return null // No differences found
        }

        log("Total differing pixels: $diffPixelsCount")

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

        val canvas = canvasPeer.createCanvas(totalWidth, totalHeight)
        val ctx = canvas.context2d

        ctx.setFillStyle(Color.WHITE)
        ctx.fillRect(0.0, 0.0, totalWidth.toDouble(), totalHeight.toDouble())

        val expectedSnapshot = canvasPeer.createSnapshot(expectedBitmap)
        val actualSnapshot = canvasPeer.createSnapshot(actualBitmap)
        val diffSnapshot = canvasPeer.createSnapshot(addBorderToBitmap(diffBitmap, 1, Color.BLACK))

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
        val canvas = canvasPeer.createCanvas(width, height)
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
        val canvas = canvasPeer.createCanvas(newWidth, newHeight)
        val ctx = canvas.context2d

        // Draw border rectangle
        ctx.setFillStyle(color)
        ctx.fillRect(0.0, 0.0, newWidth.toDouble(), newHeight.toDouble())

        // Draw original image inside the border
        ctx.drawImage(canvasPeer.createSnapshot(bitmap), borderSize.toDouble(), borderSize.toDouble())

        return canvas.takeSnapshot().bitmap
    }

    interface BitmapIO {
        fun write(bitmap: Bitmap, fileName: String)
        fun read(fileName: String): Bitmap

        fun getReadFilePath(fileName: String): String
        fun getWriteFilePath(fileName: String): String

        fun getExpectedFileReportPath(fileName: String): String {
            return getReadFilePath(fileName)
        }

        fun getActualFileReportPath(fileName: String): String {
            return getWriteFilePath(fileName)
        }

        fun getDiffFileReportPath(fileName: String): String {
            return getWriteFilePath(fileName)
        }
    }
}
