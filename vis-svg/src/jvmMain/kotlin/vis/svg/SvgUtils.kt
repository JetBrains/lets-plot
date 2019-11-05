/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
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

