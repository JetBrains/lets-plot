/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.test.Test

class RichTextLineHeightTest {
    @Test
    fun estimateHeightsForTwoPlainTextLines() {
        val heights = RichText.estimateHeights(
            text = "A\nB",
            font = DEF_FONT
        )

        assertThat(heights).containsExactly(16.0, 16.0)
    }

    @Test
    fun estimateHeightsForPlainTextThenFraction() {
        val heights = RichText.estimateHeights(
            text = "A\n\\( \\frac{B}{C} \\)",
            font = DEF_FONT
        )

        assertThat(heights).containsExactly(16.0, 32.0)
    }

    @Test
    fun estimateHeightsForFractionThenPlainText() {
        val heights = RichText.estimateHeights(
            text = "\\( A\\frac{B}{C} \\)\nD",
            font = DEF_FONT
        )

        assertThat(heights).containsExactly(32.0, 16.0)
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnSecondLine() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setLineHeights(listOf(16.0, 32.0))

        assertThat(lineYPositions(label)).containsExactly(92.0, 116.0)
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnFirstLine() {
        val label = Label("\\( \\frac{A}{B} \\)\nC")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setLineHeights(listOf(32.0, 16.0))

        assertThat(lineYPositions(label)).containsExactly(84.0, 108.0)
    }

    @Test
    fun topAnchoredLabelKeepsCumulativeLineHeights() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.TOP)
        label.setLineHeights(listOf(16.0, 32.0))

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