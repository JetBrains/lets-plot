/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Byte.toUnsignedInt
import java.util.*
import javax.imageio.ImageIO

@Throws(IOException::class)
fun SvgUtils.buildDataUrl(bufferedImage: BufferedImage): String {
    var bytes: ByteArray? = null
    ByteArrayOutputStream().use { baos ->
        ImageIO.write(bufferedImage, "png", baos)
        bytes = baos.toByteArray()
    }
    val base64String = Base64.getEncoder().encodeToString(bytes)
    return pngDataURI(base64String)
}

enum class ImageType {
    GRAY,
    RGB,
    RGBA,
}

// TODO: Not used ? 
fun transcodeToDataUrl(width: Int, height: Int, type: ImageType, base64: String): String {
    val bytes = Base64.getDecoder().decode(base64)
    val channels: Int = when (type) {
        ImageType.GRAY -> 1
        ImageType.RGB -> 3
        ImageType.RGBA -> 4
    }

    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    var i = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            val r = toUnsignedInt(bytes[i])
            var g = r
            var b = r
            var a = 255
            if (channels > 1) {
                g = toUnsignedInt(bytes[i + 1])
                b = toUnsignedInt(bytes[i + 2])
            }
            if (channels > 3) {
                a = toUnsignedInt(bytes[i + 3])
            }
            bufferedImage.setRGB(x, y, SvgUtils.toARGB(r, g, b, a))
            i += channels
        }
    }

    try {
        return SvgUtils.buildDataUrl(bufferedImage)
    } catch (e: IOException) {
        throw IllegalArgumentException("Can't build image $width X $height", e)
    }
}
