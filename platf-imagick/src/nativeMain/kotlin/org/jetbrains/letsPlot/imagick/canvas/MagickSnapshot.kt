/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.cloneMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyMagickWand

class MagickSnapshot(
    val img: CPointer<ImageMagick.MagickWand>
) : Disposable, Canvas.Snapshot {
    private var isDisposed = false

    override val size: Vector = Vector(
        ImageMagick.MagickGetImageWidth(img).toInt(),
        ImageMagick.MagickGetImageHeight(img).toInt()
    )
    override val bitmap: Bitmap
        get() = toBitmap()

    override fun dispose() {
        if (isDisposed) {
            return
        }
        isDisposed = true
        destroyMagickWand(img)
    }

    override fun copy(): Canvas.Snapshot {
        val copiedImg = cloneMagickWand(img)
        return MagickSnapshot(copiedImg)
    }

    private fun toBitmap(): Bitmap {
        val width = ImageMagick.MagickGetImageWidth(img).toInt()
        val height = ImageMagick.MagickGetImageHeight(img).toInt()
        val numPixels = width * height

        memScoped {
            // Allocate native buffer: 4 bytes per pixel (RGBA)
            val pixelBuffer = allocArray<UByteVar>(numPixels * 4)

            val success = ImageMagick.MagickExportImagePixels(
                img,
                0, 0,
                width.convert(), height.convert(),
                "RGBA",
                ImageMagick.StorageType.CharPixel,
                pixelBuffer
            )

            if (success == ImageMagick.MagickFalse) {
                throw RuntimeException("Failed to export image pixels from MagickWand")
            }

            // Convert RGBA to ARGB IntArray (optional)
            val argbIntArray = IntArray(numPixels)
            for (i in 0 until numPixels) {
                val r = pixelBuffer[i * 4].toInt() and 0xFF
                val g = pixelBuffer[i * 4 + 1].toInt() and 0xFF
                val b = pixelBuffer[i * 4 + 2].toInt() and 0xFF
                val a = pixelBuffer[i * 4 + 3].toInt() and 0xFF
                argbIntArray[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
            }

            return Bitmap(width = width, height = height, argbInts = argbIntArray)
        }
    }

    companion object {
        fun fromBitmap(bitmap: Bitmap): MagickSnapshot {
            val img = MagickUtil.fromBitmap(bitmap)
            return MagickSnapshot(img)
        }
    }
}