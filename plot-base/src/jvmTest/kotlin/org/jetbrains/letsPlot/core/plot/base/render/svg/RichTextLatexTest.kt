/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import kotlin.test.Test

class RichTextLatexTest {
    @Test
    fun noLatex() {
        val text = "There are no formulas here"
        val svg = RichText.toSvg(text, DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertTSpan(
            svg.tspans().single(),
            text
        )
    }

    @Test
    fun emptyFormula() {
        val svg = RichText.toSvg("""\(\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(0)
    }

    @Test
    fun simpleFormulaSpace() {
        val svg = RichText.toSvg("""\(\quad\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertTSpan(
            svg.tspans().single(),
            " "
        )
    }

    @Test
    fun simpleFormulaSpecialSymbol() {
        val svg = RichText.toSvg("""\(\infty\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertTSpan(
            svg.tspans().single(),
            "∞"
        )
    }

    @Test
    fun simpleFormulaLetter() {
        val svg = RichText.toSvg("""\(\Omega\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertTSpan(
            svg.tspans().single(),
            "Ω"
        )
    }

    @Test
    fun simpleFormulaSuperscript() {
        val svg = RichText.toSvg("""\(a^b\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        assertTSpan(base, "a", sub = false, sup = false)
        assertTSpan(space, " ", sub = false, sup = false)
        assertTSpan(shiftSup, "\u200B", sub = null, sup = true)
        assertTSpan(pow, "b", sub = false, sup = false)
        assertTSpan(restoreShift, "\u200B", sub = true, sup = null)
    }

    @Test
    fun simpleFormulaSuperscriptWithBraces() {
        val svg = RichText.toSvg("""\(a^{bc}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        assertTSpan(base, "a", sub = false, sup = false)
        assertTSpan(space, " ", sub = false, sup = false)
        assertTSpan(shiftSup, "\u200B", sub = null, sup = true)
        assertTSpan(pow, "bc", sub = false, sup = false)
        assertTSpan(restoreShift, "\u200B", sub = true, sup = null)
    }

    @Test
    fun simpleFormulaSubscript() {
        val svg = RichText.toSvg("""\(a_b\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        assertTSpan(base, "a", sub = false, sup = false)
        assertTSpan(space, " ", sub = false, sup = false)
        assertTSpan(shiftSub, "\u200B", sub = true, sup = null)
        assertTSpan(index, "b", sub = false, sup = false)
        assertTSpan(restoreShift, "\u200B", sub = null, sup = true)
    }

    @Test
    fun simpleFormulaSubscriptWithBraces() {
        val svg = RichText.toSvg("""\(a_{bc}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        assertTSpan(base, "a", sub = false, sup = false)
        assertTSpan(space, " ", sub = false, sup = false)
        assertTSpan(shiftSub, "\u200B", sub = true, sup = null)
        assertTSpan(index, "bc", sub = false, sup = false)
        assertTSpan(restoreShift, "\u200B", sub = null, sup = true)
    }

    @Test
    fun simpleFormulaFraction() {
        TODO()
    }

    @Test
    fun formulaWithMultipleLevels() {
        TODO()
    }

    @Test
    fun sumOfFractions() {
        TODO()
    }

    @Test
    fun superscriptInFraction() {
        TODO()
    }

    @Test
    fun fractionInSuperscript() {
        TODO()
    }

    @Test
    fun fractionOnSecondLevel() {
        TODO()
    }

    @Test
    fun twoLines() {
        TODO()
    }

    @Test
    fun textBeforeFormula() {
        TODO()
    }

    @Test
    fun textBetweenTwoFormulas() {
        TODO()
    }

    @Test
    fun textAfterFormula() {
        TODO()
    }

    @Test
    fun linkBeforeFormula() {
        TODO()
    }

    @Test
    fun formulaInLink() {
        TODO()
    }

    @Test
    fun linkInFormula() {
        TODO()
    }

    @Test
    fun markdownBeforeFormula() {
        TODO()
    }

    @Test
    fun mixMarkdownWithFormula() {
        TODO()
    }

    @Test
    fun estimateFormulaWidth() {
        TODO()
    }

    @Test
    fun estimateFractionWidthAndPosition() {
        TODO()
    }

    @Test
    fun formulaWithAnchorLeft() {
        TODO()
    }

    @Test
    fun formulaWithAnchorMiddle() {
        TODO()
    }

    @Test
    fun formulaWithAnchorRight() {
        TODO()
    }

    companion object {
        private val DEF_FONT = Font(family = FontFamily("sans-serif", false), size = 16, isBold = false, isItalic = false)
    }
}