/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import kotlin.math.ceil
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class ImageComparer(
    private val canvasPeer: CanvasPeer,
    private val bitmapIO: BitmapIO,
    private val defaultProfile: ComparisonProfile = ComparisonProfile.Strict,
    private val profileAdjuster: (ComparisonContext) -> ComparisonProfile = { it.profile },
    private val suffix: String = "",
    private val silent: Boolean = false
) {
    data class ComparisonProfile(
        // Maximum allowed weighted RGBA distance between two pixels.
        // Examples: 0 = exact match, 10-15 = minor AA drift, 20-25 = typical browser text drift.
        val tol: Int,
        // Search radius for matching a nearby pixel.
        val maxShift: Int,
        val allowedDiffPixelRatio: Double
    ) {
        fun withBrowserAaTolerance(): ComparisonProfile {
            return copy(
                tol = tol + 8,
                maxShift = maxOf(maxShift, 1),
                allowedDiffPixelRatio = allowedDiffPixelRatio + 0.01
            )
        }

        companion object {
            val Strict = ComparisonProfile(
                tol = 2,
                maxShift = 0,
                allowedDiffPixelRatio = 0.0
            )

            val Geometries = ComparisonProfile(
                tol = 12,
                maxShift = 1,
                allowedDiffPixelRatio = 0.003
            )

            val Text = ComparisonProfile(
                tol = 24,
                maxShift = 1,
                allowedDiffPixelRatio = 0.02
            )
        }
    }

    data class ComparisonContext(
        val testSuite: KClass<out TestSuit>?,
        val test: KFunction<*>?,
        val profile: ComparisonProfile
    )

    private fun reportLocation(path: String): String {
        return if ("://" in path) path else "file://$path"
    }

    private fun log(message: Any) {
        if (!silent) {
            println(message)
        }
    }

    fun assertBitmapEquals(
        fileName: String,
        actualBitmap: Bitmap,
        profile: ComparisonProfile? = null,
        testSuite: KClass<out TestSuit>? = null,
        test: KFunction<*>? = null
    ) {
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
            log("Failed to read expected image.\n${it.message}\n Actual image saved to ${reportLocation(bitmapIO.getActualFileReportPath(actualFileName))}")
            error("Failed to read expected image.\n${it.message}\nActual image saved to ${reportLocation(bitmapIO.getActualFileReportPath(actualFileName))}")
        }

        val profile = profileAdjuster(
            ComparisonContext(
                testSuite = testSuite,
                test = test,
                profile = profile ?: defaultProfile
            )
        )
        val diffBitmap = createDiffImage(expectedBitmap, actualBitmap, profile)
        if (diffBitmap != null) {
            val diffFilePath = "${testName}_diff.png"
            val visualDiffBitmap = composeVisualDiff(expectedBitmap, actualBitmap, diffBitmap)

            bitmapIO.write(visualDiffBitmap, diffFilePath)
            bitmapIO.write(actualBitmap, actualFileName)

            error(
                """Image mismatch.
                |    Diff: ${reportLocation(bitmapIO.getDiffFileReportPath(diffFilePath))}
                |    Actual: ${reportLocation(bitmapIO.getActualFileReportPath(actualFileName))}
                |    Expected: ${reportLocation(bitmapIO.getExpectedFileReportPath(expectedFileName))}""".trimMargin() + "\n"
            )
        } else {
            log("Image comparison passed: $expectedFileName")
        }
    }

    private fun createDiffImage(
        expected: Bitmap,
        actual: Bitmap,
        profile: ComparisonProfile
    ): Bitmap? {
        val diffWidth = maxOf(expected.width, actual.width)
        val diffHeight = maxOf(expected.height, actual.height)
        val diffCanvas = canvasPeer.createCanvas(diffWidth, diffHeight)
        diffCanvas.context2d.setFillStyle(Color.TRANSPARENT)
        diffCanvas.context2d.fillRect(0.0, 0.0, diffWidth.toDouble(), diffHeight.toDouble())

        diffCanvas.context2d.setFillStyle(Color.RED)

        var diffPixelsCount = 0
        val allowedDiffPixels = ceil(diffWidth * diffHeight * profile.allowedDiffPixelRatio).toInt()

        for (y in 0 until diffHeight) {
            for (x in 0 until diffWidth) {
                if (!pixelsMatchWithNeighborhood(expected, actual, x, y, profile)) {
                    diffPixelsCount++
                    diffCanvas.context2d.fillRect(x.toDouble(), y.toDouble(), 1.0, 1.0)
                }
            }
        }

        if (diffPixelsCount <= allowedDiffPixels) {
            return null // No differences found
        }

        log("Total differing pixels: $diffPixelsCount")

        return diffCanvas.takeSnapshot().bitmap
    }

    private fun pixelsMatchWithNeighborhood(
        expected: Bitmap,
        actual: Bitmap,
        x: Int,
        y: Int,
        profile: ComparisonProfile
    ): Boolean {
        val expectedPixel = expected.getPixel(x, y) ?: return actual.getPixel(x, y) == null

        for (dy in -profile.maxShift..profile.maxShift) {
            for (dx in -profile.maxShift..profile.maxShift) {
                val actualPixel = actual.getPixel(x + dx, y + dy) ?: continue
                if (pixelsMatch(expectedPixel, actualPixel, profile.tol)) {
                    return true
                }
            }
        }

        return false
    }

    private fun pixelsMatch(expectedPixel: Color, actualPixel: Color, tolerance: Int): Boolean {
        val dr = (expectedPixel.red - actualPixel.red).toDouble()
        val dg = (expectedPixel.green - actualPixel.green).toDouble()
        val db = (expectedPixel.blue - actualPixel.blue).toDouble()
        val da = (expectedPixel.alpha - actualPixel.alpha).toDouble()

        // Weighted RGB distance reduces sensitivity to tiny channel-level AA drift.
        val perceptualDistanceSquared =
            0.2126 * dr * dr +
            0.7152 * dg * dg +
            0.0722 * db * db +
            0.5 * da * da

        return perceptualDistanceSquared <= tolerance * tolerance
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
