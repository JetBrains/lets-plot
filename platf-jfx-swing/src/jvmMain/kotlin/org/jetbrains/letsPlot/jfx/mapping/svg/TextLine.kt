/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.geometry.Bounds
import javafx.geometry.VPos
import javafx.scene.Cursor
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import javafx.scene.text.*
import java.awt.Desktop
import java.awt.EventQueue

// Manually positions text runs along the x-axis, supports super/subscript/hyperlinks.

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
        val href: String? = null,
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

        val width = texts.sumOf { it.boundsInParent.width }
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
        var currentRunPosX = 0.0
        texts.forEach { text ->
            text.x = currentRunPosX + dx
            text.y += dy
            currentRunPosX += text.boundsInParent.width
        }

        children.clear()
        children.addAll(texts)
    }

    private fun textRunToTextFx(textRun: TextRun): Text {
        val font = font ?: error("Font is not specified")
        val scaleFactor = textRun.fontScale ?: 1.0

        val text = if (textRun.href == null) Text() else HyperlinkText(textRun.href)
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

        val lineHeight = text.boundsInParent.height

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

// Can't use javafx.scene.control.Hyperlink.
// It has a different baseline, so, to align it with a Text the layoutY modification is needed.
// But in this case MultiLineLabel (that uses TextLine internally) returns bounds larger than expected.
// Lots of extra styling required to make a Hyperlink look like a regular text.
// Another problem is that Hyperlink requires an extra frame to layout it properly, without it bounds is 0x0.
// This is not compatible with tooltips - they should get the bounds immediately.
// As a workaround hyperlink.graphic.boundingRectInLocal can be used, but this doesn't solve problem with layoutY and
// incorrect bounds of MultiLineLabel.
internal class HyperlinkText(
    private var href: String? = null
) : Text() {
    private var sceneCursor: Cursor? = null

    init {
        onMouseClicked = EventHandler {
            href?.let {
                // On Linux, Desktop.browse() may hang the application.
                // https://stackoverflow.com/questions/65852545/desktop-getdesktop-openfile-on-ubuntu-not-working
                EventQueue.invokeLater {
                    Desktop.getDesktop().browse(java.net.URI(it))
                }
            }
        }

        onMouseEntered = EventHandler {
            sceneCursor = scene.cursor
            scene.cursor = Cursor.HAND
        }

        onMouseExited = EventHandler {
            scene.cursor = sceneCursor
        }
    }
}
