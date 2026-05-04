/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_SEGMENT_COLOR
import org.jetbrains.letsPlot.core.plot.base.render.point.UpdatableShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform

object AestheticsUtil {
    //affects bar, smooth, area and ribbon
    internal const val ALPHA_CONTROLS_BOTH = false

    private fun isExplicitAlphaValue(alpha: Double?): Boolean {
        return alpha != null && alpha != AesInitValue.DEFAULT_ALPHA
    }

    private fun hasExplicitSegmentAlpha(p: DataPointAesthetics): Boolean {
        return isExplicitAlphaValue(p.segmentAlpha())
    }

    private fun explicitAlpha(p: DataPointAesthetics): Double? {
        return p.alpha()?.takeIf(::isExplicitAlphaValue)
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
        val stroke = p.color()!!

        val resolvedFill = if (filled || solid) {
            applyAlpha(fill(filled, solid, p), p)
        } else {
            Color.TRANSPARENT
        }

        val resolvedStroke = if (strokeWidth > 0) {
            applyAlpha(stroke, p)
        } else {
            Color.TRANSPARENT
        }

        shape.update(resolvedFill, resolvedStroke, strokeWidth, transform)
    }

    private fun applyAlpha(color: Color, p: DataPointAesthetics): Color {
        return explicitAlpha(p)?.let(color::changeAlpha) ?: color
    }

    fun effectiveSegmentAlpha(p: DataPointAesthetics): Double? {
        return if (hasExplicitSegmentAlpha(p)) p.segmentAlpha() else p.alpha()
    }

    fun effectiveSegmentColor(p: DataPointAesthetics): Color? {
        return p.segmentColor()
            ?.takeIf { it != DEFAULT_SEGMENT_COLOR }
            ?: p.color()
    }

    fun resolveColor(p: DataPointAesthetics, applyAlpha: Boolean): Color {
        val color = p.color()!!
        return if (applyAlpha) applyAlpha(color, p) else color
    }

    fun resolveFill(p: DataPointAesthetics, color: Color = p.fill()!!): Color {
        return applyAlpha(color, p)
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
        val resolvedStroke = resolveColor(p, applyAlpha)
        shape.strokeColor().set(resolvedStroke)
    }

    fun updateFill(shape: SvgShape, p: DataPointAesthetics) {
        val resolvedFill = resolveFill(p)
        shape.fillColor().set(resolvedFill)
    }
}
