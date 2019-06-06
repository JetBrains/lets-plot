package jetbrains.datalore.visualization.base.svgMapper.jfx.attr

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import jetbrains.datalore.visualization.base.svg.SvgImageElement
import java.io.ByteArrayInputStream
import java.util.*

internal class SvgImageAttrMapping(target: ImageView) : SvgAttrMapping<ImageView>(target) {
    override fun setAttribute(name: String, value: Any?) {
        when (name) {
            SvgImageElement.X.name -> target.x = value as Double
            SvgImageElement.Y.name -> target.y = value as Double
            SvgImageElement.WIDTH.name -> target.fitWidth = value as Double
            SvgImageElement.HEIGHT.name -> target.fitHeight = value as Double
            SvgImageElement.PRESERVE_ASPECT_RATIO.name -> target.preserveRatioProperty().set(asBoolean(value))
            SvgImageElement.HREF.name -> {
                val base64Str = (value as String).split(",")[1]
                val inputStream = ByteArrayInputStream(Base64.getDecoder().decode(base64Str))
                val image = Image(inputStream)
                target.image = image
            }
            else -> super.setAttribute(name, value)
        }
    }
}