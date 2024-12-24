/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.image.ImageView
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import org.jetbrains.letsPlot.jfx.mapping.svg.attr.SvgImageAttrMapping

internal class SvgImageElementMapper(
    source: SvgImageElement,
    target: ImageView,
    peer: SvgJfxPeer
) : SvgElementMapper<SvgImageElement, ImageView>(source, target, peer) {

    private val myImageViewAttrSupport = ImageViewAttributesSupport(target)

    override fun setTargetAttribute(name: String, value: Any?) {
        myImageViewAttrSupport.setAttribute(name, value)
    }


    private class ImageViewAttributesSupport(val target: ImageView) {
        private var myImageBytes: ByteArray? = null

        init {
            @Suppress("ObjectLiteralToLambda")
            target.preserveRatioProperty().addListener(object : ChangeListener<Boolean> {
                override fun changed(
                    observable: ObservableValue<out Boolean>?,
                    oldValue: Boolean?,
                    newValue: Boolean?
                ) {
                    SvgImageAttrMapping.updateTargetImage(target, myImageBytes)
                }
            })
        }

        fun setAttribute(name: String, value: Any?) {
            if (name == SvgImageElement.HREF.name) {
                myImageBytes = SvgImageAttrMapping.setHrefDataUrl(target, value as String)
                return
            }

            // set default
            SvgImageAttrMapping.setAttribute(target, name, value)
        }
    }
}