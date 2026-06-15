/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.text.LineBoxMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
import kotlin.test.Test

class RichTextLineLayoutMetricsTest {
    @Test
    fun estimateLineLayoutMetricsForTwoPlainTextLines() {
        val layoutMetrics = RichText.measure(
            text = "A\nB",
            font = DEF_FONT
        ).layout.lineBoxes

        assertLineBoxMetrics(
            actual = layoutMetrics,
            expected = listOf(
                LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0),
                LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0)
            )
        )
    }

    @Test
    fun estimateLineLayoutMetricsForPlainTextThenFraction() {
        val layoutMetrics = RichText.measure(
            text = "A\n\\( \\frac{B}{C} \\)",
            font = DEF_FONT
        ).layout.lineBoxes

        assertLineBoxMetrics(
            actual = layoutMetrics,
            expected = listOf(
                LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0),
                LineBoxMetrics(boxHeight = 35.52, topToBaseline = 23.36)
            )
        )
    }

    @Test
    fun estimateLineLayoutMetricsForFractionThenPlainText() {
        val layoutMetrics = RichText.measure(
            text = "\\( A\\frac{B}{C} \\)\nD",
            font = DEF_FONT
        ).layout.lineBoxes

        assertLineBoxMetrics(
            actual = layoutMetrics,
            expected = listOf(
                LineBoxMetrics(boxHeight = 35.52, topToBaseline = 23.36),
                LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0)
            )
        )
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnSecondLine() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setTextLayout(
            TextBlockLayout.fromLineBoxes(
                listOf(
                    LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0),
                    LineBoxMetrics(boxHeight = 32.0, topToBaseline = 24.0)
                )
            )
        )

        assertThat(lineYPositions(label)).containsExactly(88.0, 112.0)
    }

    @Test
    fun centerAnchoredLabelPositionsFractionOnFirstLine() {
        val label = Label("\\( \\frac{A}{B} \\)\nC")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.setTextLayout(
            TextBlockLayout.fromLineBoxes(
                listOf(
                    LineBoxMetrics(boxHeight = 32.0, topToBaseline = 24.0),
                    LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0)
                )
            )
        )

        assertThat(lineYPositions(label)).containsExactly(88.0, 112.0)
    }

    @Test
    fun topAnchoredLabelStacksLinesByBaselineOffsets() {
        val label = Label("A\n\\( \\frac{B}{C} \\)")
        label.setY(100.0)
        label.setVerticalAnchor(Text.VerticalAnchor.TOP)
        label.setTextLayout(
            TextBlockLayout.fromLineBoxes(
                listOf(
                    LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0),
                    LineBoxMetrics(boxHeight = 32.0, topToBaseline = 24.0)
                )
            )
        )

        assertThat(lineYPositions(label)).containsExactly(100.0, 124.0)
    }

    @Test
    fun textBlockLayoutExposesBaselineOffsetsAndSpan() {
        val layout = TextBlockLayout.fromLineBoxes(
            listOf(
                LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0),
                LineBoxMetrics(boxHeight = 32.0, topToBaseline = 24.0)
            )
        )

        assertThat(layout.baselineOffsets).containsExactly(0.0, 24.0)
        assertThat(layout.baselineSpan).isEqualTo(24.0)
        assertThat(layout.blockHeight).isEqualTo(48.0)
    }

    @Test
    fun mergeOnBaselinePreservesMaxTopAndBottomExtents() {
        val merged = LineBoxMetrics.mergeOnBaseline(
            metrics = listOf(
                LineBoxMetrics(boxHeight = 16.0, topToBaseline = 16.0),
                LineBoxMetrics(boxHeight = 35.52, topToBaseline = 23.36)
            ),
            defaultIfEmpty = LineBoxMetrics.fromBoxHeight(1.0)
        )

        assertThat(merged).isEqualTo(LineBoxMetrics(boxHeight = 35.52, topToBaseline = 23.36))
    }

    @Test
    fun renderLinesExposeAnchorPolicyForFractionLine() {
        val renderedLines = RichText.renderLines(
            text = "a+\\(\\frac{b}{c}\\)\ntext",
            font = DEF_FONT,
            anchor = Text.HorizontalAnchor.MIDDLE
        )

        assertThat(renderedLines.map { it.anchor }).containsExactly(
            Text.HorizontalAnchor.LEFT,
            Text.HorizontalAnchor.MIDDLE
        )
    }

    companion object {
        private val DEF_FONT = Font(
            family = FontFamily.SERIF,
            size = 16,
            isBold = false,
            isItalic = false
        )

        private fun lineYPositions(label: Label): List<Double> {
            return label.rootGroup.children().map { node ->
                when (node) {
                    is SvgTextElement -> node.y().get()!!
                    is SvgGElement -> extractTranslateY(node.transform().get()!!)
                    else -> error("Unexpected line node type: ${node::class.simpleName}")
                }
            }
        }

        private fun extractTranslateY(transform: SvgTransform): Double {
            val match = Regex("""translate\(([^ ]+) ([^)]+)\)""").find(transform.toString())
                ?: error("Could not parse translate transform: $transform")
            return match.groupValues[2].trim().toDouble()
        }

        private fun assertLineBoxMetrics(actual: List<LineBoxMetrics>, expected: List<LineBoxMetrics>) {
            assertThat(actual).hasSameSizeAs(expected)
            (actual zip expected).forEach { (actualMetrics, expectedMetrics) ->
                assertThat(actualMetrics.boxHeight).isCloseTo(expectedMetrics.boxHeight, offset(1e-9))
                assertThat(actualMetrics.topToBaseline).isCloseTo(expectedMetrics.topToBaseline, offset(1e-9))
            }
        }
    }
}
