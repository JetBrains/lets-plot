/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator.widthCalculator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertFormulaTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.max
import kotlin.test.Test

class LabelTest {
    @Test
    fun setHorizontalAnchorBasic() {
        val text = """text"""
        checkHorizontalAnchor(text, Text.HorizontalAnchor.LEFT, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.MIDDLE, "middle")
        checkHorizontalAnchor(text, Text.HorizontalAnchor.RIGHT, "end")
    }

    @Test
    fun setHorizontalAnchorForFormulaWithFraction() {
        val text = """a+\(\frac{b}{c}\)"""
        checkHorizontalAnchor(text, Text.HorizontalAnchor.LEFT, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.MIDDLE, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.RIGHT, null)
    }

    @Test
    fun setHorizontalAnchorForFormulaWithFractionAfterLink() {
        val text = """<a href="https://example.com">link</a>\(\frac{b}{c}\)"""
        checkHorizontalAnchor(text, Text.HorizontalAnchor.LEFT, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.MIDDLE, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.RIGHT, null)
    }

    @Test
    fun setHorizontalAnchorForTwoLinesWithFractionInFirst() {
        val text = "a+\\(\\frac{b}{c}\\)\ntext"
        checkHorizontalAnchors(text, Text.HorizontalAnchor.LEFT, listOf(null, null))
        checkHorizontalAnchors(text, Text.HorizontalAnchor.MIDDLE, listOf(null, "middle"))
        checkHorizontalAnchors(text, Text.HorizontalAnchor.RIGHT, listOf(null, "end"))
    }

    // Asserts deep legacy tspan structure for `a+\frac{b}{c}`. The formula is now vector-rendered;
    // equivalent width-via-vector-advances coverage lives in RichTextLatexVectorTest. Disabled.
    @kotlin.test.Ignore
    @Test
    fun widthCalculationForBasicFont() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily.SERIF,
            size = 12,
            isBold = false,
            isItalic = false
        ))
    }

    @kotlin.test.Ignore
    @Test
    fun widthCalculationForCustomizedFontFamily() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily("Arial", monospaced = false),
            size = 12,
            isBold = false,
            isItalic = false
        ))
    }

    @kotlin.test.Ignore
    @Test
    fun widthCalculationForMonospacedFont() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily("monospace", monospaced = true),
            size = 12,
            isBold = false,
            isItalic = false
        ))
    }

    @kotlin.test.Ignore
    @Test
    fun widthCalculationForBoldFont() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily.SERIF,
            size = 12,
            isBold = true,
            isItalic = false
        ))
    }

    @kotlin.test.Ignore
    @Test
    fun widthCalculationForItalicFont() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily.SERIF,
            size = 12,
            isBold = false,
            isItalic = true
        ))
    }

    companion object {
        private fun checkHorizontalAnchor(
            text: String,
            horizontalAnchor: Text.HorizontalAnchor,
            expectedAnchor: String?
        ) {
            checkHorizontalAnchors(text, horizontalAnchor, listOf(expectedAnchor))
        }

        private fun checkHorizontalAnchors(
            text: String,
            horizontalAnchor: Text.HorizontalAnchor,
            expectedAnchors: List<String?>
        ) {
            val label = Label(text)
            label.setHorizontalAnchor(horizontalAnchor)
            val lines = label.rootGroup.children()
            assertThat(lines.size).isEqualTo(expectedAnchors.size)
            (lines zip expectedAnchors).forEach { (line, expectedAnchor) ->
                // Lines with vector formulas or fractions are SvgGElement and have no text-anchor
                // attribute (their horizontal anchoring comes from a line origin shift). The legacy
                // expectation for those cases is `null`.
                val actualAnchor: String? = when (line) {
                    is SvgTextElement -> line.textAnchor().get()
                    else -> null
                }
                if (expectedAnchor == null) {
                    assertThat(actualAnchor).isNull()
                } else {
                    assertThat(actualAnchor).isEqualTo(expectedAnchor)
                }
            }
        }

        private fun widthCalculationForFractionWithPrefix(
            font: Font
        ) {
            val label = Label("""a+\(\frac{b}{c}\)""")
            label.setFontFamily(font.family.toString())
            label.setFontSize(font.size.toDouble())
            label.setFontWeight(if (font.isBold) "bold" else null)
            label.setFontStyle(if (font.isItalic) "italic" else null)

            val svg = label.rootGroup.children().single() as SvgTextElement
            assertThat(svg.tspans()).hasSize(6)
            val (_, baseline, num, denom, bar) = svg.tspans()
            val restoreShift = svg.tspans().drop(5).single()
            val level = TestUtil.FormulaLevel()
            val prefixWidth = widthCalculator("a+", font)
            val formulaWidth = max(widthCalculator("b", font), widthCalculator("c", font))
            val expectedWidth = prefixWidth + formulaWidth

            val fracPosition = prefixWidth + formulaWidth / 2.0
            assertFormulaTSpan(baseline, "\u200B", level = level.pass(), expectedAnchor = "start")
            assertFormulaTSpan(num, "b", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
            assertFormulaTSpan(denom, "c", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
            assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
            assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        }
    }
}