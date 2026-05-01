/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.point.UpdatableShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

object AestheticsUtil {
    //affects bar, smooth, area and ribbon
    internal const val ALPHA_CONTROLS_BOTH = false

    data class ResolvedColor(
        val color: Color,
        val opacity: Double
    )

    fun isExplicitAlphaValue(alpha: Double?): Boolean {
        return alpha != null && alpha != AesInitValue.DEFAULT_ALPHA
    }

    fun hasExplicitAlpha(p: DataPointAesthetics): Boolean {
        return isExplicitAlphaValue(p.alpha())
    }

    fun hasExplicitSegmentAlpha(p: DataPointAesthetics): Boolean {
        return isExplicitAlphaValue(p.segmentAlpha())
    }

    fun fill(filled: Boolean, solid: Boolean, p: DataPointAesthetics): Color {
        if (filled) {
            return p.fill()!!
        } else if (solid) {
            return p.color()!!
        }
        return Color.TRANSPARENT
    }

    fun decorate(
        shape: UpdatableShape,
        filled: Boolean,
        solid: Boolean,
        p: DataPointAesthetics,
        strokeWidth: Double,
        transform: SvgTransform?
    ) {
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

        shape.update(fill, fillAlpha, stroke, strokeAlpha, strokeWidth, transform)
    }

    fun alpha(color: Color, p: DataPointAesthetics): Double {
        return if (hasExplicitAlpha(p)) {                      //  apply only custom 'aes' alpha
            p.alpha()!!
        } else {                                               // else, override with color's alpha
            SvgUtils.alpha2opacity(color.alpha)
        }
    }

    fun resolveColor(color: Color, p: DataPointAesthetics, applyAlpha: Boolean): ResolvedColor {
        val opacity = if (applyAlpha) {
            alpha(color, p)
        } else {
            SvgUtils.alpha2opacity(color.alpha)
        }

        return ResolvedColor(
            color = color.changeAlpha(255),
            opacity = opacity
        )
    }

    fun composeColor(resolvedColor: ResolvedColor): Color {
        return resolvedColor.color.changeAlpha(resolvedColor.opacity)
    }

    fun strokeWidth(p: DataPointAesthetics) = AesScaling.strokeWidth(p)

    fun pieDiameter(p: DataPointAesthetics) = AesScaling.pieDiameter(p)

    fun pointStrokeWidth(
        p: DataPointAesthetics,
        strokeGetter: (DataPointAesthetics) -> Double? = DataPointAesthetics::stroke
    ) = AesScaling.strokeWidth(p, strokeGetter)

    fun circleDiameter(
        p: DataPointAesthetics,
        sizeGetter: (DataPointAesthetics) -> Double? = DataPointAesthetics::size
    ) = AesScaling.circleDiameter(p, sizeGetter)

    fun textSize(p: DataPointAesthetics) = AesScaling.textSize(p)

    fun updateStroke(shape: SvgShape, p: DataPointAesthetics, applyAlpha: Boolean) {
        val resolvedStroke = resolveColor(p.color()!!, p, applyAlpha)
        shape.strokeColor().set(resolvedStroke.color)
        shape.strokeOpacity().set(resolvedStroke.opacity)
    }

    fun updateFill(shape: SvgShape, p: DataPointAesthetics) {
        val resolvedFill = resolveColor(p.fill()!!, p, applyAlpha = true)
        shape.fillColor().set(resolvedFill.color)
        shape.fillOpacity().set(resolvedFill.opacity)
    }
}
