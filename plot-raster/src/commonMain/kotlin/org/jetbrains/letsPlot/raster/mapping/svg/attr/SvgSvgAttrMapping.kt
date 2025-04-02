/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.shape.Pane
import org.jetbrains.letsPlot.raster.shape.TRANSLATE_X
import org.jetbrains.letsPlot.raster.shape.TRANSLATE_Y
import org.jetbrains.letsPlot.raster.shape.with

internal object SvgSvgAttrMapping : SvgAttrMapping<Pane>() {

    override fun setAttribute(target: Pane, name: String, value: Any?) {
        when (name) {
            SvgSvgElement.X.name -> target.transform = target.transform.with(TRANSLATE_X, value?.asFloat ?: 0.0f)
            SvgSvgElement.Y.name -> target.transform = target.transform.with(TRANSLATE_Y, value?.asFloat ?: 0.0f)
            SvgSvgElement.WIDTH.name -> target.width = value?.asFloat ?: 0.0f
            SvgSvgElement.HEIGHT.name -> target.height = value?.asFloat ?: 0.0f
            "display" /*SvgSvgElement.DISPLAY.name*/ -> { }  // ignore
            else -> super.setAttribute(target, name, value)
        }
    }
}
