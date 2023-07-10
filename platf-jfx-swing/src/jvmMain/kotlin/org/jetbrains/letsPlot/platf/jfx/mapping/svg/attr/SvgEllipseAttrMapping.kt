/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg.attr

import javafx.scene.shape.Ellipse
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgEllipseElement

internal object SvgEllipseAttrMapping : SvgShapeMapping<Ellipse>() {
    override fun setAttribute(target: Ellipse, name: String, value: Any?) {
        when (name) {
            SvgEllipseElement.CX.name -> target.centerX = asDouble(value)
            SvgEllipseElement.CY.name -> target.centerY = asDouble(value)
            SvgEllipseElement.RX.name -> target.radiusX = asDouble(value)
            SvgEllipseElement.RY.name -> target.radiusY = asDouble(value)
            else -> super.setAttribute(target, name, value)
        }
    }
}