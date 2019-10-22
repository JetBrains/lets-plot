package jetbrains.datalore.vis.canvas.javaFx

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*

internal object JavafxCanvasUtil {

    //works only in javafx thread
    fun takeSnapshotImage(canvas: Node): WritableImage {
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        return canvas.snapshot(params, null)
    }

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

    internal fun runInJavafxThread(runnable: Runnable) {
        runInJavafxThread { runnable.run() }
    }

    internal fun <T> runInJavafxThread(runnable: () -> T) {
        if (Platform.isFxApplicationThread()) {
            runnable()
        } else {
            Platform.runLater{ runnable() }
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
}
