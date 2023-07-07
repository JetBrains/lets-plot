/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.util

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

object RgbToDataUrl {
    fun png(width: Int, height: Int, argbValues: IntArray): String {
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        for (y in 0 until height) {
            for (x in 0 until width) {
                image.setRGB(x, y, argbValues[y * width + x])
            }
        }

        try {
            return toPngDataUri(image)
        } catch (e: IOException) {
            throw IllegalArgumentException("Can't build image $width X $height", e)
        }

    }

    @Throws(IOException::class)
    private fun toPngDataUri(image: BufferedImage): String {
        ByteArrayOutputStream().use { stream ->
            ImageIO.write(image, "png", stream)
            val bytes = stream.toByteArray()
            val base64String = Base64.getEncoder().encodeToString(bytes)
            return toPngDataUri(base64String)
        }
    }

    private fun toPngDataUri(base64EncodedPngImage: String): String {
        return "data:image/png;base64,$base64EncodedPngImage"
    }
}