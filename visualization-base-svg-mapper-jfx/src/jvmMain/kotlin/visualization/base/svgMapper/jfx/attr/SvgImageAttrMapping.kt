package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import jetbrains.datalore.visualization.base.svg.SvgImageElement
import java.io.ByteArrayInputStream
import java.util.*

internal class SvgImageAttrMapping(target: ImageView) : SvgAttrMapping<ImageView>(target) {
    private var imageBytes: ByteArray? = null

    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgImageElement.X.name -> target.x = value as Double
            SvgImageElement.Y.name -> target.y = value as Double
            SvgImageElement.WIDTH.name -> target.fitWidth = value as Double
            SvgImageElement.HEIGHT.name -> target.fitHeight = value as Double
            SvgImageElement.PRESERVE_ASPECT_RATIO.name -> {
                target.preserveRatioProperty().set(asBoolean(value))
                updateTargetImage()
            }
            SvgImageElement.HREF.name -> {
                val base64Str = (value as String).split(",")[1]
                imageBytes = Base64.getDecoder().decode(base64Str)
                updateTargetImage()
            }
            else -> super.setAttribute(name, value)
        }
    }

    private fun updateTargetImage() {
        if (imageBytes == null) return
        val inputStream = ByteArrayInputStream(imageBytes)
        // This way image is shown without interpolation
        target.image = Image(inputStream, target.fitWidth, target.fitHeight, target.preserveRatioProperty().get(), false)
    }
}