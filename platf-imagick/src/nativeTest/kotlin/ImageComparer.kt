import kotlinx.cinterop.*
import kotlin.math.abs

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

class ImageComparer(
    private val expectedDir: String,
    private val outDir: String,
) {
    fun assertImageEquals(expectedFileName: String, actualWand: CPointer<ImageMagick.MagickWand>) {
        val expectedPath = expectedDir + expectedFileName
        val testName = expectedFileName.removeSuffix(".bmp")
        val actualFilePath = "${testName}_actual.bmp"
        val diffFilePath = "${testName}_diff.bmp"

        val expectedWand = ImageMagick.NewMagickWand() ?: error("Failed to create expected wand")
        if (ImageMagick.MagickReadImage(expectedWand, expectedPath) == ImageMagick.MagickFalse) {
            println(getMagickError(expectedWand))
            // Write the  actual image to a file for debugging
            if (ImageMagick.MagickWriteImage(actualWand, actualFilePath) == ImageMagick.MagickFalse) {
                println(getMagickError(actualWand))
            }
        }

        val expected = exportPixels(expectedWand)
        val actual = exportPixels(actualWand)

        if (!comparePixelArrays(expected, actual, tolerance = 0)) {
            ImageMagick.MagickWriteImage(actualWand, actualFilePath)
            val width = ImageMagick.MagickGetImageWidth(actualWand).toInt()
            val height = ImageMagick.MagickGetImageHeight(actualWand).toInt()
            val diffWand = composeVisualDiff(expectedWand, actualWand, createDiffImage(expected, actual, width, height))
            if (ImageMagick.MagickWriteImage(diffWand, diffFilePath) == ImageMagick.MagickFalse) {
                println(getMagickError(diffWand))
            }

            error("Image mismatch: see $actualFilePath and $diffFilePath")
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

    fun comparePixelArrays(expected: UByteArray, actual: UByteArray, tolerance: Int = 0): Boolean {
        if (expected.size != actual.size) return false
        return expected.indices.all { pixelsEqual(expected[it], actual[it], tolerance) }
    }

    fun createDiffImage(expected: UByteArray, actual: UByteArray, width: Int, height: Int): CPointer<ImageMagick.MagickWand> {
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

        val composite = ImageMagick.NewMagickWand()!!

        // Create new blank canvas: 3x width to fit all images
        val canvasWidth = (width * 3).toULong()
        val canvasHeight = height.toULong()

        val white = ImageMagick.NewPixelWand()!!
        ImageMagick.PixelSetColor(white, "white")

        ImageMagick.MagickNewImage(composite, canvasWidth, canvasHeight, white)
        ImageMagick.DestroyPixelWand(white)

        // Composite images: expected at x=0
        ImageMagick.MagickCompositeImage(composite, expected, ImageMagick.CompositeOperator.OverCompositeOp, ImageMagick.MagickFalse, 0, 0)
        // actual at x=width
        ImageMagick.MagickCompositeImage(composite, actual, ImageMagick.CompositeOperator.OverCompositeOp, ImageMagick.MagickFalse,width.toLong(), 0)
        // diff at x=width * 2
        ImageMagick.MagickCompositeImage(composite, diff, ImageMagick.CompositeOperator.OverCompositeOp, ImageMagick.MagickFalse,(width * 2).toLong(), 0)

        return composite
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
