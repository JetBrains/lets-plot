/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d

class MagickCanvas(
    private val _img: CPointer<ImageMagick.MagickWand>,
    override val size: Vector,
    pixelDensity: Double,
    private val fontManager: MagickFontManager,
) : Canvas {
    // TODO: replace usage in tests with Snapshot
    val img: CPointer<ImageMagick.MagickWand>
        get() {
            val wand = (context2d as MagickContext2d).wand

            if (false) {
                val v = ImageMagick.DrawGetVectorGraphics(wand)
                println(v!!.toKString())
            }

            ImageMagick.MagickDrawImage(_img, wand)
            return _img
        }

    override val context2d: Context2d = MagickContext2d(_img, pixelDensity, fontManager)


    override fun takeSnapshot(): Canvas.Snapshot {
        return MagickSnapshot(img)
    }

    fun saveBmp(filename: String) {
        if (ImageMagick.MagickWriteImage(img, filename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }
    }

    companion object {
        fun create(width: Number, height: Number, pixelDensity: Number, fontManager: MagickFontManager): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()), pixelDensity, fontManager)
        }

        fun create(size: Vector, pixelDensity: Number, fontManager: MagickFontManager): MagickCanvas {
            val wand = ImageMagick.NewMagickWand() ?: error("MagickCanvas: Failed to create new MagickWand")
            ImageMagick.MagickSetImageAlphaChannel(wand, ImageMagick.AlphaChannelOption.OnAlphaChannel)
            val background = ImageMagick.NewPixelWand()
            ImageMagick.PixelSetColor(background, "transparent")
            ImageMagick.MagickNewImage(wand, size.x.toULong(), size.y.toULong(), background)
            return MagickCanvas(wand, size, pixelDensity = pixelDensity.toDouble(), fontManager = fontManager)
        }
    }

}
