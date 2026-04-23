/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.text.LineDimensions
import org.jetbrains.letsPlot.core.plot.base.render.text.LineMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.test.Test

class RichTextLineMetricsTest {
    @Test
    fun estimateBaselineMetricsForTwoPlainTextLines() {
        val lineMetrics = RichText.estimateLineDimensions(
            text = "A\nB",
            font = DEF_FONT
        ).map(LineDimensions::metrics)

        assertThat(lineMetrics).containsExactly(
            LineMetrics(16.0, 0.0),
            LineMetrics(16.0, 0.0)
        )
    }

    @Test
    fun estimateBaselineMetricsForPlainTextThenFraction() {
        val lineMetrics = RichText.estimateLineDimensions(
            text = "A\n\\( \\frac{B}{C} \\)",
            font = DEF_FONT
        ).map(LineDimensions::metrics)

        assertThat(lineMetrics).containsExactly(
            LineMetrics(16.0, 0.0),
            LineMetrics(24.0, 8.0)
        )
    }

    @Test
    fun estimateBaselineMetricsForFractionThenPlainText() {
        val lineMetrics = RichText.estimateLineDimensions(
            text = "\\( A\\frac{B}{C} \\)\nD",
            font = DEF_FONT
        ).map(LineDimensions::metrics)

        assertThat(lineMetrics).containsExactly(
            LineMetrics(24.0, 8.0),
            LineMetrics(16.0, 0.0)
        )
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnSecondLine() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setLineMetrics(listOf(LineMetrics(16.0, 0.0), LineMetrics(24.0, 8.0)))

        assertThat(lineYPositions(label)).containsExactly(88.0, 112.0)
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnFirstLine() {
        val label = Label("\\( \\frac{A}{B} \\)\nC")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setLineMetrics(listOf(LineMetrics(24.0, 8.0), LineMetrics(16.0, 0.0)))

        assertThat(lineYPositions(label)).containsExactly(88.0, 112.0)
    }

    @Test
    fun topAnchoredLabelStacksLinesByBaselineOffsets() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.TOP)
        label.setLineMetrics(listOf(LineMetrics(16.0, 0.0), LineMetrics(24.0, 8.0)))

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
