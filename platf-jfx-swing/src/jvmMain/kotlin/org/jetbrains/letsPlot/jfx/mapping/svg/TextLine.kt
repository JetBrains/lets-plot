/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import javafx.collections.ListChangeListener
import javafx.geometry.Bounds
import javafx.geometry.VPos
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.*

// Manually positions text runs along the x-axis, supports super/subscript.

// Limitations:
// Supports only a single line of text.
// Does not allow tspan elements to have their own styles.
internal class TextLine : Region() {
    enum class BaselineShift {
        SUB,
        SUPER
    }

    data class TextRun(
        val text: String,
        val baselineShift: BaselineShift? = null,
        val fontScale: Double? = null,
        // NOTE: resets between runs, yet by standard it alters the baseline for the rest of the text
        val dy: Double? = null,
    )

    init {
        transforms.addListener(
            ListChangeListener {
                rebuild()
            }
        )
    }

    var content: List<TextRun> = emptyList()
        set(value) {
            field = value
            rebuild()
        }

    var x: Double = 0.0
        set(value) {
            field = value
            rebuild()
        }

    var y: Double = 0.0
        set(value) {
            field = value
            rebuild()
        }

    var fill: Color? = null
        set(value) {
            field = value
            rebuild()
        }

    var stroke: Color? = null
        set(value) {
            field = value
            rebuild()
        }

    var strokeWidth: Double? = null
        set(value) {
            field = value
            rebuild()
        }

    // May be null - system will use default font
    var fontFamily: String? = null
        set(value) {
            if (value == field) return

            field = value
            rebuildFont()
        }

    var fontSize: Double = -1.0
        set(value) {
            if (value == field) return

            field = value
            rebuildFont()
        }

    var fontWeight: FontWeight? = null
        set(value) {
            if (value == field) return

            field = value
            rebuildFont()
        }

    var fontPosture: FontPosture? = null
        set(value) {
            if (value == field) return

            field = value
            rebuildFont()
        }

    var textOrigin: VPos? = null
        set(value) {
            field = value
            rebuild()
        }

    var textAlignment: TextAlignment? = null
        set(value) {
            field = value
            rebuild()
        }

    private var font: Font? = null
        set(value) {
            field = value
            if (value != null) {
                fontMetric = Text("X").apply { this.font = font }.boundsInLocal
            } else {
                fontMetric = null
            }

            rebuild()
        }

    private var fontMetric: Bounds? = null

    private fun rebuildFont() {
        font = Font.font(fontFamily, fontWeight, fontPosture, fontSize)
        rebuild()
    }

    private fun rebuild() {
        val fontMetric = fontMetric ?: return // wait for font to be set

        val texts = content.map(::textRunToTextFx)

        val width = texts.sumOf { it.boundsInLocal.width }
        val dx = when (textAlignment) {
            TextAlignment.RIGHT -> -width
            TextAlignment.CENTER -> -width / 2
            else -> 0.0
        }

        // Font metrics from Text bounds:
        // lineHeight = fontMetric.height
        // ascent = -fontMetric.minY
        // descent = fontMetric.maxY
        val height = fontMetric.minY
        val dy = when (textOrigin) {
            null -> 0.0
            VPos.BOTTOM -> 0.0
            VPos.TOP -> -height
            VPos.CENTER -> -height / 2
            VPos.BASELINE -> error("VPos.BASELINE is not supported")
        }

        // Arrange runs one after another
        var currentRunPosX = x
        texts.forEach { text ->
            text.x = currentRunPosX + dx
            text.y += y + dy
            currentRunPosX += text.boundsInLocal.width
        }

        children.clear()
        children.addAll(texts)
    }

    private fun textRunToTextFx(textRun: TextRun): Text {
        val font = font ?: error("Font is not specified")
        val scaleFactor = textRun.fontScale ?: 1.0

        val text = Text()
        fill?.let { text.fill = it }
        stroke?.let { text.stroke = it }
        strokeWidth?.let { text.strokeWidth = it }
        text.text = textRun.text
        text.font = when (scaleFactor) {
            1.0 -> font
            else -> {
                val fontWeight = FontWeight.BOLD.takeIf { font.style.contains("bold") }
                val fontPosture = FontPosture.ITALIC.takeIf { font.style.contains("italic") }
                val fontSize = font.size * scaleFactor
                Font.font(font.family, fontWeight, fontPosture, fontSize)
            }
        }

        val bounds = text.boundsInLocal
        val lineHeight = bounds.height

        val dy = textRun.dy?.let { lineHeight * it } ?: 0.0
        val baseline = when (textRun.baselineShift) {
            BaselineShift.SUPER -> lineHeight * 0.4
            BaselineShift.SUB -> lineHeight * -0.4
            else -> 0.0
        }
        text.y = -baseline + dy

        return text
    }

    override fun toString(): String {
        return "TextLine(content=$content, fill=$fill, stroke=$stroke, font=$font, textOrigin=$textOrigin, textAlignment=$textAlignment)"
    }
}
