/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.point.UpdatableShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

object AestheticsUtil {
    //affects bar, smooth, area and ribbon
    internal const val ALPHA_CONTROLS_BOTH = false

    fun fill(filled: Boolean, solid: Boolean, p: DataPointAesthetics): Color {
        if (filled) {
            return p.fill()!!
        } else if (solid) {
            return p.color()!!
        }
        return Color.TRANSPARENT
    }

    fun decorate(shape: UpdatableShape, filled: Boolean, solid: Boolean, p: DataPointAesthetics, strokeWidth: Double) {
        val fill = fill(filled, solid, p)
        val stroke = p.color()!!

        var fillAlpha = 0.0
        if (filled || solid) {
            fillAlpha = alpha(fill, p)
        }

        var strokeAlpha = 0.0
        if (strokeWidth > 0) {
            strokeAlpha = alpha(stroke, p)
        }

        shape.update(fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
    }

    fun alpha(color: Color, p: DataPointAesthetics): Double {
        return if (p.alpha() != AesInitValue.DEFAULT_ALPHA) {  //  apply only custom 'aes' alpha
            p.alpha()!!
        } else {                                               // else, override with color's alpha
            SvgUtils.alpha2opacity(color.alpha)
        }
    }

    fun strokeWidth(p: DataPointAesthetics) = AesScaling.strokeWidth(p)

    fun pointStrokeWidth(p: DataPointAesthetics) = AesScaling.pointStrokeWidth(p)

    fun textSize(p: DataPointAesthetics) = AesScaling.textSize(p)

    fun updateStroke(shape: SvgShape, p: DataPointAesthetics, applyAlpha: Boolean) {
        shape.strokeColor().set(p.color())
        if (p.alpha() != AesInitValue.DEFAULT_ALPHA && applyAlpha) {
            shape.strokeOpacity().set(p.alpha())
        }
    }

    fun updateFill(shape: SvgShape, p: DataPointAesthetics) {
        shape.fillColor().set(p.fill())
        if (p.alpha() != AesInitValue.DEFAULT_ALPHA) {
            shape.fillOpacity().set(p.alpha())
        }
    }
}
