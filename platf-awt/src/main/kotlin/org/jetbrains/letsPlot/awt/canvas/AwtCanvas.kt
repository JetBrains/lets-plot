/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_4BYTE_ABGR
import java.io.IOException
import javax.imageio.ImageIO


internal class AwtCanvas
private constructor(
    val image: BufferedImage,
    size: Vector,
    pixelDensity: Double,
) : ScaledCanvas(AwtContext2d(image.createGraphics() as Graphics2D), size, pixelDensity) {

    companion object {
        fun create(size: Vector, pixelDensity: Double): Canvas {
            val s = if (size == Vector.ZERO) {
                Vector(1, 1)
            } else size

            return AwtCanvas(BufferedImage(s.x, s.y, TYPE_4BYTE_ABGR), s, pixelDensity)
        }
    }

    override fun takeSnapshot(): Canvas.Snapshot {
        return AwtSnapshot(image)
    }

    internal data class AwtSnapshot(val image: BufferedImage) : Canvas.Snapshot {
        override val size: Vector = Vector(image.width, image.height)

        override fun copy(): AwtSnapshot {
            val b = BufferedImage(image.width, image.height, image.type)
            val g: Graphics2D = b.createGraphics()
            g.drawImage(image, 0, 0, null)
            g.dispose()
            return AwtSnapshot(b)
        }

        override fun toDataUrl(): String {
            return try {
                val byteArrayOutputStream = java.io.ByteArrayOutputStream()
                ImageIO.write(image, "png", byteArrayOutputStream)
                val bytes = byteArrayOutputStream.toByteArray()
                "data:image/png;base64," + Base64.encode(bytes)
            } catch (e: IOException) {
                throw RuntimeException("Failed to convert image to data URL", e)
            }
        }
    }
}
