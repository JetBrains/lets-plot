/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.formatting.string.ByteSizeFormatter.formatByteSize
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.checkError
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.destroyPixelWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newMagickWand
import org.jetbrains.letsPlot.imagick.canvas.MagickUtil.newPixelWand
import kotlin.experimental.ExperimentalNativeApi

val logEnabled = true

fun log(msg: () -> String) {
    if (logEnabled) {
        println(msg())
    }
}

class MagickCanvas(
    override val size: Vector,
    private val pixelDensity: Double,
    fontManager: MagickFontManager,
    private val antialiasing: Boolean,
) : Canvas {
    private val magickContext2d = MagickContext2d(size, pixelDensity, fontManager)
    override val context2d: MagickContext2d = magickContext2d

    override fun takeSnapshot(tag: String?): MagickSnapshot {
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

        log {
            memScoped {
                val length = ImageMagick.DrawGetVectorGraphics(context2d.wand)?.toKString()?.length ?: 0

                if (formatByteSize(length) == "184.71 KB") {
                    val mvgData = ImageMagick.DrawGetVectorGraphics(context2d.wand)?.toKString() ?: ""
                    if (tag != null) {
                        val outFilePath = Native.writeToFile("$tag.mvg", mvgData.encodeToByteArray())
                        "MVG data written to: $outFilePath"
                    } else {
                        "MVG data:\n$mvgData"
                    }
                } else {
                    "MagickCanvas.takeSnapshot(${tag ?: hashCode().toULong().toString(16)}): MVG data length: ${formatByteSize(length)}"
                }
            }
        }
        ImageMagick.MagickDrawImage(img, context2d.wand)

        return MagickSnapshot(img)
    }

    @OptIn(ExperimentalNativeApi::class)
    override fun takeSnapshot(): MagickSnapshot {
        return takeSnapshot(null)
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
