/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElement
import org.jetbrains.letsPlot.raster.shape.Image


internal object SvgImageAttrMapping : SvgAttrMapping<Image>() {
    override fun setAttribute(target: Image, name: String, value: Any?) {
        when (name) {
            SvgImageElement.X.name -> target.x = value?.asFloat ?: 0.0f
            SvgImageElement.Y.name -> target.y = value?.asFloat ?: 0.0f
            SvgImageElement.WIDTH.name -> target.width = value?.asFloat ?: 0.0f
            SvgImageElement.HEIGHT.name -> target.height = value?.asFloat ?: 0.0f
            SvgImageElement.PRESERVE_ASPECT_RATIO.name -> target.preserveRatio = asBoolean(value)
            else -> super.setAttribute(target, name, value)
        }
    }
}
