/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas

class MagickCanvas(
    private val _img: CPointer<ImageMagick.MagickWand>?,
    size: Vector,
    pixelRatio: Double,
) : ScaledCanvas(MagickContext2d(_img), size, pixelRatio) {

    val img: CPointer<ImageMagick.MagickWand>? get ()  {
        val wand = (context2d as MagickContext2d).wand

        if (REUSE_WAND) {
            val v = ImageMagick.DrawGetVectorGraphics(wand)
            println(v!!.toKString())
            ImageMagick.MagickDrawImage(_img, wand)
        }
        return _img
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun immidiateSnapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun dumpPixels(): String {
        val w = size.x.toInt()
        val h = size.y.toInt()

        val pixelData = ByteArray(w * h * 4) // RGBA 8-bit per channel
        memScoped {
            val byteBuffer = pixelData.refTo(0) // Pointer to ByteArray
            val success = ImageMagick.MagickExportImagePixels(
                _img,
                0, 0, w.toULong(), h.toULong(),
                "RGBA", ImageMagick.StorageType.CharPixel, byteBuffer
            )
            require(success == ImageMagick.MagickTrue) { "Failed to export pixels" }

            val lines = pixelData.asSequence().windowed(4 * w.toInt(), 4 * h.toInt()).toList()
            val strLines = lines.map { line ->
                line.windowed(4, 4).joinToString { pixel ->
                    pixel.joinToString(separator = "") { it.toHexString() }
                }
            }
            return strLines.joinToString("\n")
        }
    }

    fun saveBmp(filename: String) {
        val wand = (context2d as MagickContext2d).wand

        if (REUSE_WAND) {
            ImageMagick.MagickDrawImage(_img, wand)
        }

        if (ImageMagick.MagickWriteImage(_img, filename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }
    }

    companion object {
        fun create(width: Number, height: Number): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()))
        }

        fun create(size: Vector): MagickCanvas {
            val wand = ImageMagick.NewMagickWand()
            val background = ImageMagick.NewPixelWand()
            ImageMagick.PixelSetColor(background, "white")
            ImageMagick.MagickNewImage(wand, size.x.toULong(), size.y.toULong(), background)
            return MagickCanvas(wand, size, 1.0)
        }
    }
}
