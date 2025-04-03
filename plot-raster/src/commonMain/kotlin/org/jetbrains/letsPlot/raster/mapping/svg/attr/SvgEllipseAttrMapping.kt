/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgEllipseElement
import org.jetbrains.letsPlot.raster.shape.Ellipse

internal object SvgEllipseAttrMapping : SvgShapeMapping<Ellipse>() {
    override fun setAttribute(target: Ellipse, name: String, value: Any?) {
        when (name) {
            SvgEllipseElement.CX.name -> target.centerX = value?.asFloat ?: 0.0f
            SvgEllipseElement.CY.name -> target.centerY = value?.asFloat ?: 0.0f
            SvgEllipseElement.RX.name -> target.radiusX = value?.asFloat ?: 0.0f
            SvgEllipseElement.RY.name -> target.radiusY = value?.asFloat ?: 0.0f
            else -> super.setAttribute(target, name, value)
        }
    }
}
