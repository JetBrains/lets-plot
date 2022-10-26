/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.math.areEqual
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import kotlin.math.abs
import kotlin.math.max

object TextUtil {

    private val HJUST_MAP: Map<Any, Text.HorizontalAnchor> = mapOf(
        "right" to Text.HorizontalAnchor.RIGHT,
        "middle" to Text.HorizontalAnchor.MIDDLE,
        "left" to Text.HorizontalAnchor.LEFT,
        0.0 to Text.HorizontalAnchor.LEFT,
        0.5 to Text.HorizontalAnchor.MIDDLE,
        1.0 to Text.HorizontalAnchor.RIGHT
    )
    private val VJUST_MAP: Map<Any, Text.VerticalAnchor> = mapOf(
        "bottom" to Text.VerticalAnchor.BOTTOM,
        "center" to Text.VerticalAnchor.CENTER,
        "top" to Text.VerticalAnchor.TOP,
        0.0 to Text.VerticalAnchor.BOTTOM,
        0.5 to Text.VerticalAnchor.CENTER,
        1.0 to Text.VerticalAnchor.TOP
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

    fun vAnchor(vjust: Any) = VJUST_MAP[vjust] ?:Text.VerticalAnchor.CENTER

    fun vAnchor(p: DataPointAesthetics, location: DoubleVector, center: DoubleVector?): Text.VerticalAnchor {
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
        angle =  if (angle > 180) angle - 360 else angle
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
            family = FONT_FAMILY_MAP.get(family)!!
        }
        return family
    }

    fun angle(p: DataPointAesthetics): Double {
        var angle = p.angle()!!
        if (angle != 0.0) {
            // ggplot angle: counter clockwise
            // SVG angle: clockwise
            angle = 360 - angle % 360
        }
        return angle
    }

    fun fontSize(p: DataPointAesthetics, scale: Double) = AesScaling.textSize(p) * scale

    fun lineheight(p: DataPointAesthetics, scale: Double) = p.lineheight()!! * fontSize(p, scale)

    fun decorate(label: TextLabel, p: DataPointAesthetics, scale: Double = 1.0, applyAlpha: Boolean = true) {
        label.textColor().set(p.color())
        if (applyAlpha) {
            label.textOpacity().set(p.alpha())
        }
        label.setFontSize(fontSize(p, scale))

        // family
        label.setFontFamily(fontFamily(p))

        // fontface
        // ignore 'plain' / 'normal' as it is default values
        with(FontFace.fromString(p.fontface())) {
            if (bold) label.setFontWeight("bold")
            if (italic) label.setFontStyle("italic")
        }

        // text justification
        val hAnchor = hAnchor(p.hjust())
        val vAnchor = vAnchor(p.vjust())

        if (hAnchor !== Text.HorizontalAnchor.LEFT) {  // 'left' is default
            label.setHorizontalAnchor(hAnchor)
        }
        if (vAnchor !== Text.VerticalAnchor.BOTTOM) {  // 'bottom' is default
            label.setVerticalAnchor(vAnchor)
        }

        label.rotate(angle(p))
    }

    fun decorate(label: MultilineLabel, p: DataPointAesthetics, scale: Double = 1.0, applyAlpha: Boolean = true) {
        label.textColor().set(p.color())
        if (applyAlpha) {
            label.setTextOpacity(p.alpha())
        }

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
        val lines = MultilineLabel.splitLines(text)
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
}