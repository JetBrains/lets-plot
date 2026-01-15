/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_4BYTE_ABGR
import kotlin.math.roundToInt


class AwtCanvas private constructor(
    val image: BufferedImage,
    override val size: Vector,
    private val fontManager: FontManager,
    contentScale: Double,
) : Canvas {
    override val context2d: Context2d = AwtContext2d(image.createGraphics() as Graphics2D, contentScale, fontManager = fontManager)

    companion object {
        fun create(size: Vector, pixelDensity: Double, fontManager: FontManager): AwtCanvas {
            val s = if (size == Vector.ZERO) {
                Vector(1, 1)
            } else size

            return AwtCanvas(
                image = BufferedImage((s.x * pixelDensity).roundToInt(), (s.y * pixelDensity).roundToInt(), TYPE_4BYTE_ABGR),
                size = s,
                fontManager = fontManager,
                contentScale = pixelDensity,
            ).also {
                if (pixelDensity != 1.0) {
                    it.context2d.scale(pixelDensity, pixelDensity)
                }
            }
        }
    }

    override fun takeSnapshot(): Canvas.Snapshot {
        return AwtSnapshot(image)
    }

    data class AwtSnapshot(val image: BufferedImage) : Canvas.Snapshot {
        override val size: Vector = Vector(image.width, image.height)
        override val bitmap: Bitmap by lazy { BitmapUtil.fromBufferedImage(image) }

        override fun copy(): AwtSnapshot {
            val b = BufferedImage(image.width, image.height, image.type)
            val g: Graphics2D = b.createGraphics()
            g.drawImage(image, 0, 0, null)
            g.dispose()
            return AwtSnapshot(b)
        }

        override fun dispose() {
            // No resources to dispose
        }

        companion object {
            fun fromBitmap(bitmap: Bitmap): AwtSnapshot {
                val image = BitmapUtil.toBufferedImage(bitmap)
                return AwtSnapshot(image)
            }
        }
    }
}
