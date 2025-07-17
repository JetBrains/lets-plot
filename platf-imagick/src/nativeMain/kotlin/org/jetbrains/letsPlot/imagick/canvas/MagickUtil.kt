/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import org.jetbrains.letsPlot.commons.values.Bitmap

object MagickUtil {
    private const val MEMORY_LOG_ENABLED = false
    private fun log(message: () -> String) {
        if (MEMORY_LOG_ENABLED) {
            println(message())
        }
    }

    fun newPixelWand(tag: String): CPointer<ImageMagick.PixelWand> {
        val pixelWand = ImageMagick.NewPixelWand() ?: throw RuntimeException("Failed to create new PixelWand")
        log { "newPixelWand($tag): $pixelWand" }
        return pixelWand
    }

    fun destroyPixelWand(pixelWand: CPointer<ImageMagick.PixelWand>, tag: String) {
        log { "destroyPixelWand($tag): $pixelWand" }
        ImageMagick.DestroyPixelWand(pixelWand)
    }

    fun newMagickWand(tag: String): CPointer<ImageMagick.MagickWand> {
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create new MagickWand")
        log { "newMagickWand($tag): $magickWand" }
        return magickWand
    }

    fun cloneMagickWand(magickWand: CPointer<ImageMagick.MagickWand>, tag: String): CPointer<ImageMagick.MagickWand> {
        val clonedWand = ImageMagick.CloneMagickWand(magickWand) ?: throw RuntimeException("Failed to clone MagickWand")
        log { "cloneMagickWand($tag): $clonedWand" }
        return clonedWand
    }

    fun destroyMagickWand(magickWand: CPointer<ImageMagick.MagickWand>, tag: String) {
        log { "destroyMagickWand($tag): $magickWand" }
        ImageMagick.DestroyMagickWand(magickWand)
    }

    fun newDrawingWand(tag: String): CPointer<ImageMagick.DrawingWand> {
        val drawingWand = ImageMagick.NewDrawingWand() ?: throw RuntimeException("Failed to create new DrawingWand")
        log { "newDrawingWand($tag): $drawingWand" }
        return drawingWand
    }

    fun destroyDrawingWand(drawingWand: CPointer<ImageMagick.DrawingWand>, tag: String) {
        log { "destroyDrawingWand($tag): $drawingWand" }
        ImageMagick.DestroyDrawingWand(drawingWand)
    }

    fun fromBitmap(bitmap: Bitmap): CPointer<ImageMagick.MagickWand> {
        val w = bitmap.width
        val h = bitmap.height
        val rgba = bitmap.rgbaBytes()
        val img = ImageMagick.NewMagickWand() ?: error("MagickCanvas: Failed to create new MagickWand")
        val backgroundPixel = ImageMagick.NewPixelWand()
        ImageMagick.PixelSetColor(backgroundPixel, "transparent")

        memScoped {
            val status = ImageMagick.MagickNewImage(
                img,
                w.toULong(),
                h.toULong(),
                backgroundPixel
            )
            if (status == ImageMagick.MagickFalse) {
                val err = ImageMagick.MagickGetException(img, null)
                ImageMagick.DestroyMagickWand(img)
                throw RuntimeException("Failed to create new image: $err")
            }

            // Set pixels
            ImageMagick.MagickImportImagePixels(
                img, 0, 0, w.convert(), h.convert(),
                "RGBA",
                ImageMagick.StorageType.CharPixel,
                rgba.refTo(0)
            )
            ImageMagick.DestroyPixelWand(backgroundPixel)

            return img
        }
    }

    fun toBitmap(wand: CPointer<ImageMagick.MagickWand>): Bitmap {
        val width = ImageMagick.MagickGetImageWidth(wand).toInt()
        val height = ImageMagick.MagickGetImageHeight(wand).toInt()
        val pixels = ByteArray(width * height * 4) // RGBA
        val success = ImageMagick.MagickExportImagePixels(
            wand, 0, 0, width.toULong(), height.toULong(),
            "RGBA",
            ImageMagick.StorageType.CharPixel,
            pixels.refTo(0)
        )
        if (success == ImageMagick.MagickFalse) error("Failed to export pixels")

        return Bitmap.fromRGBABytes(w = width, h = height, rgba = pixels)
    }
}
