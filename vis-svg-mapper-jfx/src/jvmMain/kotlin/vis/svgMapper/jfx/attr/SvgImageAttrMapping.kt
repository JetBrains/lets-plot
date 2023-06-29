/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgMapper.jfx.attr

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import java.io.ByteArrayInputStream
import java.util.*

internal object SvgImageAttrMapping : SvgAttrMapping<ImageView>() {
    override fun setAttribute(target: ImageView, name: String, value: Any?) {
        when (name) {
            SvgImageElement.X.name -> target.x = asDouble(value)
            SvgImageElement.Y.name -> target.y = asDouble(value)
            SvgImageElement.WIDTH.name -> target.fitWidth = asDouble(value)
            SvgImageElement.HEIGHT.name -> target.fitHeight = asDouble(value)
            SvgImageElement.PRESERVE_ASPECT_RATIO.name -> target.preserveRatioProperty().set(asBoolean(value))
            SvgImageElement.HREF.name -> {
                setHrefDataUrl(target, value as String)
            }
            else -> super.setAttribute(target, name, value)
        }
    }

    fun setHrefDataUrl(target: ImageView, dataUrl: String): ByteArray {
        val base64Str = dataUrl.split(",")[1]
        val imageBytes = Base64.getDecoder().decode(base64Str)
        updateTargetImage(target, imageBytes)
        return imageBytes
    }

    fun updateTargetImage(target: ImageView, imageBytes: ByteArray?) {
        if (imageBytes == null) return
        val inputStream = ByteArrayInputStream(imageBytes)
        // This way image is shown without interpolation
        target.image = Image(
            inputStream,
            target.fitWidth,
            target.fitHeight,
            target.preserveRatioProperty().get(),
            false
        )
    }
}