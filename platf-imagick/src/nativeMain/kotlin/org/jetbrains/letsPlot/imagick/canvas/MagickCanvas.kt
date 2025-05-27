/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas

class MagickCanvas(
    private val _img: CPointer<ImageMagick.MagickWand>,
    override val size: Vector,
    pixelDensity: Double,
) : Canvas {

    // TODO: replace usage in tests with Snapshot
    val img: CPointer<ImageMagick.MagickWand>? get ()  {
        val wand = (context2d as MagickContext2d).wand
        if (false) {
            val v = ImageMagick.DrawGetVectorGraphics(wand)
            println(v!!.toKString())
        }

        ImageMagick.MagickDrawImage(_img, wand)
        return _img
    }

    override val context2d: Context2d = MagickContext2d(_img, pixelDensity)


    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(immidiateSnapshot())
    }

    override fun immidiateSnapshot(): Canvas.Snapshot {
        return MagickSnapshot(_img)
    }

    fun saveBmp(filename: String) {
        if (ImageMagick.MagickWriteImage(img, filename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }
    }

    companion object {
        fun create(width: Number, height: Number, pixelDensity: Number): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()), pixelDensity)
        }

        fun create(size: Vector, pixelDensity: Number): MagickCanvas {
            val wand = ImageMagick.NewMagickWand() ?: error("MagickCanvas: Failed to create new MagickWand")
            val background = ImageMagick.NewPixelWand()
            ImageMagick.PixelSetColor(background, "white")
            ImageMagick.MagickNewImage(wand, size.x.toULong(), size.y.toULong(), background)
            return MagickCanvas(wand, size, pixelDensity = pixelDensity.toDouble())
        }
    }

    class MagickSnapshot(
        img: CPointer<ImageMagick.MagickWand>
    ) : Disposable, Canvas.Snapshot {
        val img: CPointer<ImageMagick.MagickWand>

        init {
            //ImageMagick.DrawImage(img)
            this.img = ImageMagick.CloneMagickWand(img) ?: error("MagickSnapshot: Failed to clone image wand")
        }

        override fun dispose() {
            ImageMagick.DestroyMagickWand(img)
        }

        override fun copy(): Canvas.Snapshot {
            val copiedImg = ImageMagick.CloneMagickWand(img) ?: error("MagickSnapshot: Failed to clone image wand")
            return MagickSnapshot(copiedImg)
        }

    }
}
