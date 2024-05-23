/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

internal abstract class MultiShapeGlyph : Glyph {
    protected fun update(
        shape: SvgSlimShape?,
        fill: Color,
        fillAlpha: Double,
        stroke: Color,
        strokeAlpha: Double,
        strokeWidth: Double,
        angle: Double,
        centerX: Double,
        centerY: Double
    ) {
        shape?.setFill(fill, fillAlpha)
        shape?.setStroke(stroke, strokeAlpha)
        shape?.setStrokeWidth(strokeWidth)
        shape?.setRotation(angle, centerX, centerY)
    }
}
