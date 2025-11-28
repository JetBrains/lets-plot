/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.cloneMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyPixelWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newPixelWand

class MagickCanvas(
    private val _img: CPointer<ImageMagick.MagickWand>,
    override val size: Vector,
    pixelDensity: Double,
    antialiasing: Boolean,
    fontManager: MagickFontManager,
) : Canvas, Disposable {
    private val magickContext2d = MagickContext2d(_img, pixelDensity, antialiasing, fontManager)
    override val context2d: MagickContext2d = magickContext2d

    override fun takeSnapshot(): MagickSnapshot {
        val wand = context2d.wand
        ImageMagick.MagickDrawImage(_img, wand)
        return MagickSnapshot(cloneMagickWand(_img))
    }

    override fun dispose() {
        destroyMagickWand(_img)
        magickContext2d.dispose()
    }

    companion object {
        fun create(width: Number, height: Number, pixelDensity: Number, fontManager: MagickFontManager, antialiasing: Boolean = true): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()), pixelDensity, fontManager, antialiasing)
        }

        fun create(size: Vector, pixelDensity: Number, fontManager: MagickFontManager, antialiasing: Boolean = true): MagickCanvas {
            val wand = newMagickWand()
            ImageMagick.MagickSetImageAlphaChannel(wand, ImageMagick.AlphaChannelOption.OnAlphaChannel)
            val background = newPixelWand()
            ImageMagick.PixelSetColor(background, "transparent")
            ImageMagick.MagickNewImage(wand, (size.x * pixelDensity.toFloat()).toULong(), (size.y * pixelDensity.toFloat()).toULong(), background)
            destroyPixelWand(background)
            return MagickCanvas(wand, size, pixelDensity.toDouble(), antialiasing, fontManager)
        }
    }
}
