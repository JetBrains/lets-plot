/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/*
package org.jetbrains.letsPlot.rasterizer.mapping.svg.attr
import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import org.jetbrains.letsPlot.skia.shape.Image

internal object SvgImageAttrMapping : SvgAttrMapping<Image>() {
    override fun setAttribute(target: Image, name: String, value: Any?) {
        when (name) {
            SvgImageElement.X.name -> target.x = value?.asFloat ?: 0.0f
            SvgImageElement.Y.name -> target.y = value?.asFloat ?: 0.0f
            SvgImageElement.WIDTH.name -> target.width = value?.asFloat ?: 0.0f
            SvgImageElement.HEIGHT.name -> target.height = value?.asFloat ?: 0.0f
            SvgImageElement.PRESERVE_ASPECT_RATIO.name -> target.preserveRatio = asBoolean(value)
            SvgImageElement.HREF.name -> setHrefDataUrl(target, value as String)
            else -> super.setAttribute(target, name, value)
        }
    }

    fun setHrefDataUrl(target: Image, dataUrl: String): ByteArray {
        val imageData = dataUrl.split(",")[1]
        val imageBytes = Base64.decode(imageData)
        updateTargetImage(target, imageBytes)
        return imageBytes
    }

    private fun updateTargetImage(target: Image, imageBytes: ByteArray) {
        target.img = org.jetbrains.skia.Image.makeFromEncoded(imageBytes)
    }
}

 */