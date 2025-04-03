/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.raster.shape.Line

internal object SvgLineAttrMapping : SvgShapeMapping<Line>() {
    override fun setAttribute(target: Line, name: String, value: Any?) {
        when (name) {
            SvgLineElement.X1.name -> target.x0 = value?.asFloat ?: 0.0f
            SvgLineElement.Y1.name -> target.y0 = value?.asFloat ?: 0.0f
            SvgLineElement.X2.name -> target.x1 = value?.asFloat ?: 0.0f
            SvgLineElement.Y2.name -> target.y1 = value?.asFloat ?: 0.0f
            else -> super.setAttribute(target, name, value)
        }
    }
}
