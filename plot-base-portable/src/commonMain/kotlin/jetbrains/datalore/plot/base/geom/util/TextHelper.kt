/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import kotlin.math.max

object TextHelper {

    val HJUST_MAP: Map<Any, Text.HorizontalAnchor> = mapOf(
        "right" to Text.HorizontalAnchor.RIGHT,
        "middle" to Text.HorizontalAnchor.MIDDLE,
        "left" to Text.HorizontalAnchor.LEFT,
        0.0 to Text.HorizontalAnchor.LEFT,
        0.5 to Text.HorizontalAnchor.MIDDLE,
        1.0 to Text.HorizontalAnchor.RIGHT
    )
    val VJUST_MAP: Map<Any, Text.VerticalAnchor> = mapOf(
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

    fun hAnchor(p: DataPointAesthetics) = textLabelAnchor(
        p.hjust(),
        HJUST_MAP,
        Text.HorizontalAnchor.MIDDLE
    )

    fun vAnchor(p: DataPointAesthetics) = textLabelAnchor(
        p.vjust(),
        VJUST_MAP,
        Text.VerticalAnchor.CENTER
    )

    fun <T> textLabelAnchor(o: Any, conversionMap: Map<Any, T>, def: T): T {
        return conversionMap.getOrElse(o) { def }
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
        val hAnchor = hAnchor(p)
        val vAnchor = vAnchor(p)

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
        val lines = MultilineLabel.splitText(text)
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