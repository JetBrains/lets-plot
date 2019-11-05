/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.awt

import jetbrains.datalore.base.values.toPngDataUri
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import javax.imageio.ImageIO

@Throws(IOException::class)
fun BufferedImage.toPngDataUri(): String {
    ByteArrayOutputStream().use { stream ->
        ImageIO.write(this, "png", stream)
        val bytes = stream.toByteArray()
        val base64String = Base64.getEncoder().encodeToString(bytes)
        return toPngDataUri(base64String)
    }
}
