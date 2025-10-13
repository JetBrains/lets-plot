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
    override val size: Vector
) : Canvas {
    override val context2d: Context2d = AwtContext2d(image.createGraphics() as Graphics2D)

    companion object {
        fun create(size: Vector, pixelDensity: Double): AwtCanvas {
            println("Create AwtCanvas: size= $size, pixelDensity= $pixelDensity")
            val s = if (size == Vector.ZERO) {
                Vector(1, 1)
            } else size

            return AwtCanvas(BufferedImage((s.x * pixelDensity).roundToInt(), (s.y * pixelDensity).roundToInt(), TYPE_4BYTE_ABGR), s)
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

        companion object {
            fun fromBitmap(bitmap: Bitmap): AwtSnapshot {
                val image = BitmapUtil.toBufferedImage(bitmap)
                return AwtSnapshot(image)
            }
        }
    }
}
