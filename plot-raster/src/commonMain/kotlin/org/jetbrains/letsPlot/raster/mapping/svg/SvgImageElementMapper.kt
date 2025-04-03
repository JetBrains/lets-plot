/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

/*
package org.jetbrains.letsPlot.rasterizer.mapping.svg

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import org.jetbrains.letsPlot.skia.mapping.svg.attr.SvgImageAttrMapping
import org.jetbrains.letsPlot.skia.shape.Image

internal class SvgImageElementMapper(
    source: SvgImageElement,
    target: Image,
    peer: SvgSkiaPeer
) : SvgElementMapper<SvgImageElement, Image>(source, target, peer) {

    private val myImageViewAttrSupport = ImageViewAttributesSupport(target)

    override fun setTargetAttribute(name: String, value: Any?) {
        myImageViewAttrSupport.setAttribute(name, value)
    }


    private class ImageViewAttributesSupport(val target: Image) {
        private var myImageBytes: ByteArray? = null

        init {
//            target.preserveRatio.addListener(object : ChangeListener<Boolean> {
//                override fun changed(
//                    observable: ObservableValue<out Boolean>?,
//                    oldValue: Boolean?,
//                    newValue: Boolean?
//                ) {
//                    SvgImageAttrMapping.updateTargetImage(target, myImageBytes)
//                }
//            })
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

 */