/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator.widthCalculator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.pathElements
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.stringParts
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.vectorFormulaGroups
import org.jetbrains.letsPlot.core.plot.base.render.text.LatexVectorFont
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
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

    @Test
    fun mixedLineReRenderKeepsFormulaAndPrefix() {
        val label = Label("""a+\(\frac{b}{c}\)""")
        val initialLineCount = label.linesCount()

        label.setFontSize(18.0)
        label.setFontSize(24.0)
        label.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)

        assertThat(label.linesCount()).isEqualTo(initialLineCount)
        val line = label.rootGroup.children().single() as SvgGElement
        assertThat(line.vectorFormulaGroups()).isNotEmpty()
        assertThat(line.stringParts()).contains("a+")
    }

    @Test
    fun widthCalculationForBasicFont() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily.SERIF,
            size = 12,
            isBold = false,
            isItalic = false
        ))
    }

    @Test
    fun widthCalculationForBoldFont() {
        widthCalculationForFractionWithPrefix(Font(
            family = FontFamily.SERIF,
            size = 12,
            isBold = true,
            isItalic = false
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

            val line = label.rootGroup.children().single()
            assertThat(line).isInstanceOf(SvgGElement::class.java)
            val svg = line as SvgGElement
            assertThat(svg.pathElements()).hasSize(3)
            assertThat(svg.stringParts()).contains("a+")

            val prefixWidth = widthCalculator("a+", font)
            val formulaWidth = max(
                LatexVectorFont.advanceEm('b', font.isBold, font.isItalic),
                LatexVectorFont.advanceEm('c', font.isBold, font.isItalic)
            ) * font.size
            val expectedWidth = prefixWidth + formulaWidth
            val formulaGroupX = extractTranslateX(svg.vectorFormulaGroups().single().transform().get()!!)

            assertThat(formulaGroupX).isCloseTo(prefixWidth, offset(1e-9))
            assertThat(RichText.measure("""a+\(\frac{b}{c}\)""", font).width)
                .isCloseTo(expectedWidth, offset(1e-9))
        }

        private fun extractTranslateX(transform: SvgTransform): Double {
            val match = Regex("""translate\(([^ ]+) ([^)]+)\)""").find(transform.toString())
                ?: error("Could not parse translate transform: $transform")
            return match.groupValues[1].trim().toDouble()
        }
    }
}