/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.checkError
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyPixelWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newPixelWand

class MagickCanvas(
    override val size: Vector,
    private val pixelDensity: Double,
    fontManager: MagickFontManager,
    private val antialiasing: Boolean,
) : Canvas {
    private val magickContext2d = MagickContext2d(pixelDensity, fontManager)
    override val context2d: MagickContext2d = magickContext2d

    override fun takeSnapshot(): MagickSnapshot {
        val background = newPixelWand("MagickCanvas.takeSnapshot.background")
        ImageMagick.PixelSetColor(background, "none")

        val img = newMagickWand("MagickCanvas.takeSnapshot.img")

        ImageMagick.MagickNewImage(img, (size.x * pixelDensity.toFloat()).toULong(), (size.y * pixelDensity.toFloat()).toULong(), background)
        ImageMagick.MagickSetImageAlphaChannel(img, ImageMagick.AlphaChannelOption.SetAlphaChannel)

        if (antialiasing) {
            ImageMagick.MagickSetAntialias(img, ImageMagick.MagickTrue)
        } else {
            ImageMagick.MagickSetAntialias(img, ImageMagick.MagickFalse)
        }

        destroyPixelWand(background)

        img.checkError()
        context2d.wand.checkError()

        ImageMagick.MagickDrawImage(img, context2d.wand)

        return MagickSnapshot(img)
    }

    companion object {
        fun create(width: Number, height: Number, pixelDensity: Number, fontManager: MagickFontManager, antialiasing: Boolean = true): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()), pixelDensity, fontManager, antialiasing)
        }

        fun create(size: Vector, pixelDensity: Number, fontManager: MagickFontManager, antialiasing: Boolean = true): MagickCanvas {
            return MagickCanvas(size, pixelDensity.toDouble(), fontManager, antialiasing)
        }
    }
}
