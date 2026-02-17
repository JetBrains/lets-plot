/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.areEqual
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_ALPHA
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_SEGMENT_COLOR
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils
import kotlin.math.abs
import kotlin.math.max

object TextUtil {

    val DEF_NUDGE: (DoubleVector, DoubleVector) -> DoubleVector = { location, _ -> location }

    private val HJUST_MAP: Map<Any, Text.HorizontalAnchor> = mapOf(
        "right" to Text.HorizontalAnchor.RIGHT,
        "middle" to Text.HorizontalAnchor.MIDDLE,
        "left" to Text.HorizontalAnchor.LEFT,
        0.0 to Text.HorizontalAnchor.LEFT,
        0.5 to Text.HorizontalAnchor.MIDDLE,
        1.0 to Text.HorizontalAnchor.RIGHT
    )
    private val VJUST_MAP: Map<Any, Double> = mapOf(
        "bottom" to 0.0,
        "center" to 0.5,
        "top" to 1.0,
    )
    private val FONT_FAMILY_MAP = mapOf(
        "sans" to "sans-serif",
        "serif" to "serif",
        "mono" to "monospace"
    )

    fun hAnchor(hjust: Any) = HJUST_MAP[hjust] ?: Text.HorizontalAnchor.MIDDLE

    fun hAnchor(p: DataPointAesthetics, location: DoubleVector, center: DoubleVector?): Text.HorizontalAnchor {
        var hjust = p.hjust()
        if (hjust in listOf("inward", "outward") && center != null) {
            hjust = computeJustification(hjust, p.angle()!!, location, center, isHorizontal = true)
        }
        return hAnchor(hjust)
    }

    fun vAnchor(vjust: Any): Double = VJUST_MAP[vjust] ?: (vjust as? Double) ?: 0.5

    fun vAnchor(p: DataPointAesthetics, location: DoubleVector, center: DoubleVector?): Double {
        var vjust = p.vjust()
        if (vjust in listOf("inward", "outward") && center != null) {
            vjust = computeJustification(vjust, p.angle()!!, location, center, isHorizontal = false)
        }
        return vAnchor(vjust)
    }

    // 'internal' access for tests
    internal fun computeJustification(
        initialJust: Any,
        initialAngle: Double,
        location: DoubleVector,
        center: DoubleVector,
        isHorizontal: Boolean
    ): Any {
        if (initialJust !in listOf("inward", "outward")) {
            return initialJust
        }

        var angle = initialAngle % 360
        angle = if (angle > 180) angle - 360 else angle
        angle = if (angle < -180) angle + 360 else angle

        val rotatedForward = (angle > 45.0 && angle < 135.0)
        val rotatedBackward = (angle < -45.0 && angle > -135.0)

        val a = if (isHorizontal) DoubleVector::x else DoubleVector::y
        val b = if (isHorizontal) DoubleVector::y else DoubleVector::x
        val coord = if (rotatedForward || rotatedBackward) b else a

        val swap =
            (isHorizontal && rotatedForward) || (!isHorizontal && rotatedBackward) || abs(angle) >= 135.0
        val outward = (initialJust == "inward" && swap) || (initialJust == "outward" && !swap)

        val justifications = if (isHorizontal) {
            listOf("left", "middle", "right")
        } else {
            listOf("top", "center", "bottom")
        }
            .toMutableList()
            .apply { if (outward) reverse() }

        val pos = when {
            areEqual(coord(location), coord(center)) -> 1
            coord(location) < coord(center) -> 0
            else -> 2
        }
        return justifications[pos]
    }

    fun fontFamily(p: DataPointAesthetics): String {
        var family = p.family()
        if (FONT_FAMILY_MAP.containsKey(family)) {   // otherwise - use value as provided by user
            family = FONT_FAMILY_MAP[family]!!
        }
        return family
    }

    fun angle(angle: Double): Double {
        return if (angle == 0.0) {
            0.0
        } else {
            // ggplot angle: counter-clockwise
            // SVG angle: clockwise
            360 - angle % 360
        }
    }

    internal fun orientedAngle(p: DataPointAesthetics, flipAngle: Boolean, ctx: GeomContext): Double {
        return p.angle()!!.let { angle ->
            if (flipAngle && ctx.flipped) {
                angle(angle - 90)
            } else {
                angle(angle)
            }
        }
    }

    fun fontSize(p: DataPointAesthetics, scale: Double): Double {
        val d = AesScaling.textSize(p) * scale

        // Fix error (Batik):
        // org.w3c.dom.DOMException: <unknown>:
        // The attribute "style" represents an invalid CSS declaration ("fill:#000000;font:7.602310327302772E-4px sans-serif;").
        return max(0.1, d)
    }

    fun lineheight(p: DataPointAesthetics, scale: Double) = p.lineheight()!! * fontSize(p, scale)

    fun decorate(label: Label, p: DataPointAesthetics, scale: Double = 1.0, applyAlpha: Boolean = true) {
        val color = p.color()!!
        label.textColor().set(color)
        val alpha = if (applyAlpha) {
            // apply alpha aes
            AestheticsUtil.alpha(color, p)
        } else {
            // keep color's alpha
            SvgUtils.alpha2opacity(color.alpha)
        }
        label.setTextOpacity(alpha)

        label.setFontSize(fontSize(p, scale))
        label.setLineHeight(lineheight(p, scale))

        // family
        label.setFontFamily(fontFamily(p))

        // fontface
        // ignore 'plain' / 'normal' as it is default values
        with(FontFace.fromString(p.fontface())) {
            if (bold) label.setFontWeight("bold")
            if (italic) label.setFontStyle("italic")
        }
    }

    fun measure(text: String, p: DataPointAesthetics, ctx: GeomContext, scale: Double = 1.0): DoubleVector {
        val lines = Label.splitLines(text)
        val fontSize = fontSize(p, scale)
        val lineHeight = lineheight(p, scale)
        val fontFamily = fontFamily(p)
        val fontFace = FontFace.fromString(p.fontface())

        val estimated = lines.map { line ->
            ctx.estimateTextSize(line, fontFamily, fontSize, fontFace.bold, fontFace.italic)
        }.fold(DoubleVector.ZERO) { acc, sz ->
            DoubleVector(
                x = max(acc.x, sz.x),
                y = acc.y + sz.y
            )
        }
        val lineInterval = lineHeight - fontSize
        val textHeight = estimated.y + lineInterval * (lines.size - 1)
        return DoubleVector(estimated.x, textHeight)
    }

    fun rectangleForText(
        location: DoubleVector,
        textSize: DoubleVector,
        padding: Double,
        hAnchor: Text.HorizontalAnchor,
        vAnchor: Double
    ): DoubleRectangle {
        val width = textSize.x + padding * 2
        val height = textSize.y + padding * 2

        val originX = when (hAnchor) {
            Text.HorizontalAnchor.LEFT -> location.x
            Text.HorizontalAnchor.RIGHT -> location.x - width
            Text.HorizontalAnchor.MIDDLE -> location.x - width / 2
        }
        val originY = location.y + (vAnchor - 1) * height
        return DoubleRectangle(originX, originY, width, height)
    }

    internal fun toSegmentAes(p: DataPointAesthetics): DataPointAesthetics {
        return object : DataPointAestheticsDelegate(p) {

            override operator fun <T> get(aes: Aes<T>): T? {
                val value: Any? = when (aes) {
                    Aes.COLOR -> if (super.get(Aes.SEGMENT_COLOR) == DEFAULT_SEGMENT_COLOR) super.get(Aes.COLOR) else super.get(Aes.SEGMENT_COLOR)
                    Aes.SIZE -> super.get(Aes.SEGMENT_SIZE)
                    Aes.ALPHA -> if (super.get(Aes.SEGMENT_ALPHA) == DEFAULT_ALPHA) super.get(Aes.ALPHA) else super.get(Aes.SEGMENT_ALPHA)
                    else -> super.get(aes)
                }
                @Suppress("UNCHECKED_CAST")
                return value as T?
            }
        }
    }

    internal fun textComponentFactory(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        ctx: GeomContext,
        flipAngle: Boolean = false,
        sizeUnitRatio: Double = 1.0,
        boundsCenter: DoubleVector? = null,
        nudge: (DoubleVector, DoubleVector) -> DoubleVector = DEF_NUDGE
    ): SvgGElement {
        val label = Label(text)
        decorate(label, p, sizeUnitRatio, applyAlpha = true)
        val hAnchor = hAnchor(p, location, boundsCenter)
        label.setHorizontalAnchor(hAnchor)

        val fontSize = fontSize(p, sizeUnitRatio)
        val textSize = measure(text, p, ctx, sizeUnitRatio)

        val yPosition = vAnchor(p, location, boundsCenter).let { vjust ->
            location.y + (vjust - 1) * textSize.y + (1 - 0.3 * vjust) * fontSize
        }

        val textLocation = DoubleVector(location.x, yPosition)
        label.moveTo(nudge(textLocation, textSize))

        val g = SvgGElement()
        g.children().add(label.rootGroup)
        SvgUtils.transformRotate(g, orientedAngle(p, flipAngle, ctx), location.x, location.y)
        return g
    }

    internal fun labelComponentFactory(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        ctx: GeomContext,
        labelOptions: LabelOptions,
        flipAngle: Boolean = false,
        sizeUnitRatio: Double = 1.0,
        boundsCenter: DoubleVector? = null,
        nudge: (DoubleVector, DoubleVector) -> DoubleVector = DEF_NUDGE
    ): SvgGElement {
        // text size estimation
        val textSize = measure(text, p, ctx, sizeUnitRatio)

        val hAnchor = hAnchor(p, location, boundsCenter)
        val vAnchor = vAnchor(p, location, boundsCenter)

        // Background rectangle
        val fontSize = fontSize(p, sizeUnitRatio)
        val rectangle = rectangleForText(location, textSize, padding = fontSize * labelOptions.paddingFactor, hAnchor, vAnchor)
        val backgroundRect = SvgPathElement().apply {
            d().set(
                roundedRectangle(rectangle, labelOptions.radiusFactor * rectangle.height).build()
            )
        }
        GeomHelper.decorate(backgroundRect, p, applyAlphaToAll = labelOptions.alphaStroke)
        backgroundRect.strokeWidth().set(labelOptions.borderWidth)

        // Text element
        val label = Label(text)
        decorate(label, p, sizeUnitRatio, applyAlpha = labelOptions.alphaStroke)

        val padding = fontSize * labelOptions.paddingFactor
        val xPosition = when (hAnchor) {
            Text.HorizontalAnchor.LEFT -> location.x + padding
            Text.HorizontalAnchor.RIGHT -> location.x - padding
            Text.HorizontalAnchor.MIDDLE -> location.x
        }
        val textPosition = DoubleVector(
            xPosition,
            rectangle.origin.y + padding + fontSize * 0.8 // top-align the first line
        )
        label.setHorizontalAnchor(hAnchor)
        label.moveTo(nudge(textPosition, textSize))

        // group elements and apply rotation
        val g = SvgGElement()
        g.children().add(backgroundRect)
        g.children().add(label.rootGroup)

        // rotate all
        SvgUtils.transformRotate(g, orientedAngle(p, flipAngle, ctx), location.x, location.y)

        return g
    }

    private fun roundedRectangle(rect: DoubleRectangle, radius: Double): SvgPathDataBuilder {
        return SvgPathDataBuilder().apply {
            with(rect) {
                // Ensure normal radius
                val r = minOf(radius, width / 2, height / 2)

                moveTo(right - r, bottom)
                curveTo(
                    right - r, bottom,
                    right, bottom,
                    right, bottom - r
                )

                lineTo(right, top + r)
                curveTo(
                    right, top + r,
                    right, top,
                    right - r, top
                )

                lineTo(left + r, top)
                curveTo(
                    left + r, top,
                    left, top,
                    left, top + r
                )

                lineTo(left, bottom - r)
                curveTo(
                    left, bottom - r,
                    left, bottom,
                    left + r, bottom
                )

                closePath()
            }
        }
    }
}