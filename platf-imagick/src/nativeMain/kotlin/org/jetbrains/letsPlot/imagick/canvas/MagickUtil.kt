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
    private var countAllocations = false
    private val refCounter: MutableMap<String, Int> = mutableMapOf()
    private val refLog = mutableListOf<String>()
    private const val MEMORY_LOG_ENABLED = false

    fun startCountAllocations() {
        countAllocations = true
        refCounter.clear()
        refLog.clear()
    }

    fun stopCountAllocations(): Pair<Map<String, Int>, List<String>> {
        countAllocations = false
        return Pair(refCounter.toMap(), refLog.toList()).also {
            refCounter.clear()
            refLog.clear()
        }
    }

    private fun incRefCount(str: String, ptr: CPointer<*>) {
        if (countAllocations) {
            refLog += "$str: $ptr"

            val address = ptr.toString()
            refCounter[address] = refCounter.getOrElse(address) { 0 } + 1
        }
    }

    private fun decRefCount(str: String, ptr: CPointer<*>) {
        if (countAllocations) {
            refLog += "$str: $ptr"

            val address = ptr.toString()
            when (val counter = refCounter[address]) {
                null -> println("Warning: Reference count for pointer $ptr is not tracked")
                1 -> refCounter.remove(address)
                else -> refCounter[address] = counter - 1
            }
        }
    }

    private fun log(message: () -> String) {
        if (MEMORY_LOG_ENABLED) {
            println(message())
        }
    }

    fun newPixelWand(): CPointer<ImageMagick.PixelWand> {
        val pixelWand = ImageMagick.NewPixelWand() ?: throw RuntimeException("Failed to create new PixelWand")
        incRefCount("newPixelWand()", pixelWand)
        log { "newPixelWand(): $pixelWand" }
        return pixelWand
    }

    fun destroyPixelWand(pixelWand: CPointer<ImageMagick.PixelWand>) {
        decRefCount("destroyPixelWand()", pixelWand)
        log { "destroyPixelWand(): $pixelWand" }
        ImageMagick.DestroyPixelWand(pixelWand)
    }

    fun newMagickWand(): CPointer<ImageMagick.MagickWand> {
        val magickWand = ImageMagick.NewMagickWand() ?: throw RuntimeException("Failed to create new MagickWand")
        incRefCount("newMagickWand()", magickWand)
        log { "newMagickWand(): $magickWand" }
        return magickWand
    }

    fun cloneMagickWand(magickWand: CPointer<ImageMagick.MagickWand>): CPointer<ImageMagick.MagickWand> {
        val clonedWand = ImageMagick.CloneMagickWand(magickWand) ?: throw RuntimeException("Failed to clone MagickWand")
        incRefCount("cloneMagickWand()", clonedWand)
        log { "cloneMagickWand(): $clonedWand" }
        return clonedWand
    }

    fun destroyMagickWand(magickWand: CPointer<ImageMagick.MagickWand>) {
        decRefCount("destroyMagickWand()", magickWand)
        log { "destroyMagickWand(): $magickWand" }
        ImageMagick.DestroyMagickWand(magickWand)
    }

    fun newDrawingWand(): CPointer<ImageMagick.DrawingWand> {
        val drawingWand = ImageMagick.NewDrawingWand() ?: throw RuntimeException("Failed to create new DrawingWand")
        incRefCount("newDrawingWand()", drawingWand)
        log { "newDrawingWand(): $drawingWand" }
        return drawingWand
    }

    fun destroyDrawingWand(drawingWand: CPointer<ImageMagick.DrawingWand>) {
        decRefCount("destroyDrawingWand()", drawingWand)
        log { "destroyDrawingWand(): $drawingWand" }
        ImageMagick.DestroyDrawingWand(drawingWand)
    }

    fun fromBitmap(bitmap: Bitmap): CPointer<ImageMagick.MagickWand> {
        val w = bitmap.width
        val h = bitmap.height
        val rgba = bitmap.rgbaBytes()
        val img = newMagickWand()
        val backgroundPixel = newPixelWand()
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
            destroyPixelWand(backgroundPixel)

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
