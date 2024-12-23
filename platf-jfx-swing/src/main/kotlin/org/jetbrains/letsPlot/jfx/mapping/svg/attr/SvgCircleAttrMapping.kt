/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg.attr

import javafx.scene.shape.Circle
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCircleElement

internal object SvgCircleAttrMapping : SvgShapeMapping<Circle>() {
    override fun setAttribute(target: Circle, name: String, value: Any?) {
        when (name) {
            SvgCircleElement.CX.name -> target.centerX = asDouble(value)
            SvgCircleElement.CY.name -> target.centerY = asDouble(value)
            SvgCircleElement.R.name -> target.radius = asDouble(value)
            else -> super.setAttribute(target, name, value)
        }
    }
}