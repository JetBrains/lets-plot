/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
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

        val v = ImageMagick.DrawGetVectorGraphics(wand)
        println(v!!.toKString())
        ImageMagick.MagickDrawImage(_img, wand)
        return _img
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        TODO("Not yet implemented")
    }

    override fun immidiateSnapshot(): Canvas.Snapshot {
        TODO("Not yet implemented")
    }

    fun saveBmp(filename: String) {
        val wand = (context2d as MagickContext2d).wand

        ImageMagick.MagickDrawImage(_img, wand)

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
