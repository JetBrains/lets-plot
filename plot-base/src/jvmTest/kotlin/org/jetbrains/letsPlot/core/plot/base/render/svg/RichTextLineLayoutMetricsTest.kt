/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextLayout
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.test.Test

class RichTextLineLayoutMetricsTest {
    @Test
    fun estimateLineLayoutMetricsForTwoPlainTextLines() {
        val layoutMetrics = RichText.measure(
            text = "A\nB",
            font = DEF_FONT
        ).layout.lineMetrics

        assertThat(layoutMetrics).containsExactly(
            LineLayoutMetrics(16.0, 0.0),
            LineLayoutMetrics(16.0, 0.0)
        )
    }

    @Test
    fun estimateLineLayoutMetricsForPlainTextThenFraction() {
        val layoutMetrics = RichText.measure(
            text = "A\n\\( \\frac{B}{C} \\)",
            font = DEF_FONT
        ).layout.lineMetrics

        assertThat(layoutMetrics).containsExactly(
            LineLayoutMetrics(16.0, 0.0),
            LineLayoutMetrics(23.36, 12.16)
        )
    }

    @Test
    fun estimateLineLayoutMetricsForFractionThenPlainText() {
        val layoutMetrics = RichText.measure(
            text = "\\( A\\frac{B}{C} \\)\nD",
            font = DEF_FONT
        ).layout.lineMetrics

        assertThat(layoutMetrics).containsExactly(
            LineLayoutMetrics(23.36, 12.16),
            LineLayoutMetrics(16.0, 0.0)
        )
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnSecondLine() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setTextLayout(TextLayout.fromLineMetrics(listOf(LineLayoutMetrics(16.0, 0.0), LineLayoutMetrics(24.0, 8.0))))

        assertThat(lineYPositions(label)).containsExactly(88.0, 112.0)
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnFirstLine() {
        val label = Label("\\( \\frac{A}{B} \\)\nC")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setTextLayout(TextLayout.fromLineMetrics(listOf(LineLayoutMetrics(24.0, 8.0), LineLayoutMetrics(16.0, 0.0))))

        assertThat(lineYPositions(label)).containsExactly(88.0, 112.0)
    }

    @Test
    fun topAnchoredLabelStacksLinesByBaselineOffsets() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.TOP)
        label.setTextLayout(TextLayout.fromLineMetrics(listOf(LineLayoutMetrics(16.0, 0.0), LineLayoutMetrics(24.0, 8.0))))

        assertThat(lineYPositions(label)).containsExactly(100.0, 124.0)
    }

    companion object {
        private val DEF_FONT = Font(
            family = FontFamily.SERIF,
            size = 16,
            isBold = false,
            isItalic = false
        )

        private fun lineYPositions(label: Label): List<Double> {
            @Suppress("UNCHECKED_CAST")
            val lines = label.rootGroup.children() as List<SvgTextElement>
            return lines.map { it.y().get()!! }
        }
    }
}
