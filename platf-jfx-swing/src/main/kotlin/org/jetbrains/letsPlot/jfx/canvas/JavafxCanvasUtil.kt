/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.application.Platform
import javafx.scene.image.Image
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

internal object JavafxCanvasUtil {

    internal fun <T> runInJavafxThread(runnable: () -> T) {
        if (Platform.isFxApplicationThread()) {
            runnable()
        } else {
            Platform.runLater { runnable() }
        }
    }

    fun imagePngBase64ToImage(dataUrl: String): Image {
        val mediaType = "data:image/png;base64,"
        val imageString = dataUrl.replace(mediaType, "")

        val bytes = imageString.toByteArray(StandardCharsets.UTF_8)
        val byteArrayInputStream = ByteArrayInputStream(bytes)

        try {
            return Base64.getDecoder().wrap(byteArrayInputStream).let(::Image)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }
}