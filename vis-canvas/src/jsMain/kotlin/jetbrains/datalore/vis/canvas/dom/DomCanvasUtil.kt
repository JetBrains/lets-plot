package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import org.w3c.dom.Image


internal object DomCanvasUtil {

    fun imagePngBase64ToImage(dataUrl: String): Async<Image> {
        val async = SimpleAsync<Image>()

        val image = Image()

        image.onload = {
            async.success(image)
        }

        image.src = dataUrl

        return async
    }
}
