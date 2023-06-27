/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors.solid
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.point.UpdatableShape
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
        return if (solid(color)) {    // only apply 'aes' alpha to solid colors
            p.alpha()!!
        } else SvgUtils.alpha2opacity(color.alpha)

        // else, override with color's alpha
    }

    fun strokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2.0
    }

    fun pointStrokeWidth(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.stroke()!! * AesScaling.UNIT_SHAPE_SIZE / 2.0
    }
//
//    fun circleDiameter(p: DataPointAesthetics): Double {
//        // aes Units -> px
//        return p.size()!! * 2.2
//    }
//
//    fun circleDiameterSmaller(p: DataPointAesthetics): Double {
//        // aes Units -> px
//        return p.size()!! * 1.5
//    }
//
//    fun sizeFromCircleDiameter(diameter: Double): Double {
//        // px -> aes Units
//        return diameter / 2.2
//    }
//
    fun textSize(p: DataPointAesthetics): Double {
        // aes Units -> px
        return p.size()!! * 2
    }

    fun updateStroke(shape: SvgShape, p: DataPointAesthetics, applyAlpha: Boolean) {
        shape.strokeColor().set(p.color())
        if (solid(p.color()!!) && applyAlpha) {
            shape.strokeOpacity().set(p.alpha())
        }
    }

    fun updateFill(shape: SvgShape, p: DataPointAesthetics) {
        shape.fillColor().set(p.fill())
        if (solid(p.fill()!!)) {
            shape.fillOpacity().set(p.alpha())
        }
    }
}
