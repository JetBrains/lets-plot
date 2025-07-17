/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyPixelWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newPixelWand

class MagickCanvas(
    private val _img: CPointer<ImageMagick.MagickWand>,
    override val size: Vector,
    pixelDensity: Double,
    private val fontManager: MagickFontManager,
) : Canvas, Disposable {
    private val magickContext2d = MagickContext2d(_img, pixelDensity, fontManager)
    override val context2d: Context2d = magickContext2d

    override fun takeSnapshot(): MagickSnapshot {
        val wand = (context2d as MagickContext2d).wand

        if (false) {
            val v = ImageMagick.DrawGetVectorGraphics(wand)
            println(v!!.toKString())
        }

        ImageMagick.MagickDrawImage(_img, wand)
        return MagickSnapshot(_img)
    }

    override fun dispose() {
        destroyMagickWand(_img, "MagickCanvas.dispose()._img")
        magickContext2d.dispose()
    }

    companion object {
        fun create(width: Number, height: Number, pixelDensity: Number, fontManager: MagickFontManager): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()), pixelDensity, fontManager)
        }

        fun create(size: Vector, pixelDensity: Number, fontManager: MagickFontManager): MagickCanvas {
            val wand = newMagickWand("MagickCanvas.create().wand")
            ImageMagick.MagickSetImageAlphaChannel(wand, ImageMagick.AlphaChannelOption.OnAlphaChannel)
            val background = newPixelWand("MagickCanvas.create().background")
            ImageMagick.PixelSetColor(background, "transparent")
            ImageMagick.MagickNewImage(wand, size.x.toULong(), size.y.toULong(), background)
            destroyPixelWand(background, "MagickCanvas.create().background")
            return MagickCanvas(wand, size, pixelDensity = pixelDensity.toDouble(), fontManager = fontManager)
        }
    }

}
