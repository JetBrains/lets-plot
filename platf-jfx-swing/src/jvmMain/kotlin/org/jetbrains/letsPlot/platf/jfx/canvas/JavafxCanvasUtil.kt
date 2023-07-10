/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.canvas

import javafx.application.Platform
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import jetbrains.datalore.base.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.SimpleAsync
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

internal object JavafxCanvasUtil {

    fun asyncTakeSnapshotImage(canvas: Canvas): Async<WritableImage> {
        val async = SimpleAsync<WritableImage>()

        runInJavafxThread(Runnable {
            val params = SnapshotParameters()
            params.fill = Color.TRANSPARENT
            canvas.snapshot(
                { param ->
                    async.success(param.image)
                    null
                },
                params, null
            )
        })

        return async
    }

    private fun runInJavafxThread(runnable: Runnable) {
        runInJavafxThread { runnable.run() }
    }

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

    fun imagePngByteArrayToImage(bytes: ByteArray): Image {
        return Image(ByteArrayInputStream(bytes))
    }

    fun imagePngByteArrayToImage(bytes: ByteArray, size: Vector): Image {
        return Image(ByteArrayInputStream(bytes), size.x.toDouble(), size.y.toDouble(), false, false)
    }
}