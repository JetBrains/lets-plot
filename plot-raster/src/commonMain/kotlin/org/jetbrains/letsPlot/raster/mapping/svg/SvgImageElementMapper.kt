/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */


package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import org.jetbrains.letsPlot.raster.mapping.svg.attr.SvgImageAttrMapping
import org.jetbrains.letsPlot.raster.shape.Image

internal class SvgImageElementMapper(
    source: SvgImageElement,
    target: Image,
    peer: SvgCanvasPeer
) : SvgElementMapper<SvgImageElement, Image>(source, target, peer) {

    override fun setTargetAttribute(name: String, value: Any?) {
        when (name) {
            SvgImageElement.HREF.name -> {
                peer.canvasProvider.decodeDataImageUrl(value as String).onSuccess { target.img = it }
            }
            else -> SvgImageAttrMapping.setAttribute(target, name, value)
        }
    }
}
