/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertFormulaTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import kotlin.math.roundToInt
import kotlin.test.Test

class RichTextLatexTest {
    @Test
    fun noLatex() {
        val text = "There are no formulas here"
        val svg = RichText.toSvg(text, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(text, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            text,
            level = TestUtil.FormulaLevel()
        )
        assertThat(width).isEqualTo(text.length * DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun emptyFormula() {
        val formula = """\(\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(0)
        assertThat(width).isEqualTo(0.0)
    }

    @Test
    fun simpleFormulaWithSpace() {
        val formula = """\(A B\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(2)
        val (first, second) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(first, "A", level = level.current())
        assertFormulaTSpan(second, "B", level = level.current())
        assertThat(width).isEqualTo(2 * DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun simpleFormulaWithExplicitSpace() {
        val formula = """\(A \quad B\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(3)
        val (first, space, second) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(first, "A", level = level.current())
        assertFormulaTSpan(space, " ", level = level.current())
        assertFormulaTSpan(second, "B", level = level.current())
        assertThat(width).isEqualTo(3 * DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun simpleFormulaSpecialSymbol() {
        val formula = """\(\infty\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            "∞",
            level = TestUtil.FormulaLevel()
        )
        assertThat(width).isEqualTo(DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun simpleFormulaLetter() {
        val formula = """\(\Omega\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            "Ω",
            level = TestUtil.FormulaLevel()
        )
        assertThat(width).isEqualTo(DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun simpleFormulaSuperscript() {
        val formula = """\(a^b\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(pow, "b", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun simpleFormulaSuperscriptWithBraces() {
        val formula = """\(a^{bc}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(pow, "bc", level = level.current())
        expectedWidth += toTestWidth(2, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun simpleFormulaSubscript() {
        val formula = """\(a_b\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "b", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun simpleFormulaSubscriptWithBraces() {
        val formula = """\(a_{bc}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "bc", level = level.current())
        expectedWidth += toTestWidth(2, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun formulaWithMultipleSuperscript() {
        val formula = """\(a^{b^c}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPow, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSup, secondLevelPow, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPow, "b", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(secondLevelPow, "c", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun formulaWithMultipleSubscript() {
        val formula = """\(a_{i_1}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSub, firstLevelIndex, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, secondLevelIndex, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(firstLevelIndex, "i", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(secondLevelIndex, "1", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun formulaWithMixedSupSub() {
        val formula = """\(a^{b_i}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPow, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, secondLevelIndex, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPow, "b", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(secondLevelIndex, "i", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun formulaWithMixedSubSup() {
        val formula = """\(a_{I^n}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSub, firstLevelIndex, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSup, secondLevelPow, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(firstLevelIndex, "I", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(secondLevelPow, "n", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun simpleFormulaFraction() {
        val formula = """\(\frac{a}{b}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(4)
        val (num, denom, bar, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(num, "a", level = level.num())
        assertFormulaTSpan(denom, "b", level = level.denom())
        assertFormulaTSpan(bar, null, level = level.bar())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun sumOfFractions() {
        val formula = """\(\frac{a}{b} + \frac{c}{d}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(9)
        val (firstNum, firstDenom, firstBar, restoreFirstShift, sumSign) = svg.tspans()
        val (secondNum, secondDenom, secondBar, restoreSecondShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(firstNum, "a", level = level.num())
        assertFormulaTSpan(firstDenom, "b", level = level.denom())
        assertFormulaTSpan(firstBar, null, level = level.bar())
        assertFormulaTSpan(restoreFirstShift, "\u200B", level = level.revert())
        assertFormulaTSpan(sumSign, "+", level = level.current())
        assertFormulaTSpan(secondNum, "c", level = level.num())
        assertFormulaTSpan(secondDenom, "d", level = level.denom())
        assertFormulaTSpan(secondBar, null, level = level.bar())
        assertFormulaTSpan(restoreSecondShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(3 * DEF_FONT_SIZE.toDouble())
    }

    @Test
    fun superscriptInFraction() {
        val formula = """\(\frac{a^3}{b^2}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(12)
        val (numBase, numSpace, numShiftSup, numPow, numRestoreShift) = svg.tspans()
        val (denomBase, denomSpace, denomShiftSup, denomPow, denomRestoreShift) = svg.tspans().drop(5)
        val (bar, fracRestoreShift) = svg.tspans().drop(10)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(numBase, "a", level = level.num())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(numSpace, " ", level = level.pass())
        assertFormulaTSpan(numShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(numPow, "3", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(numRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(denomBase, "b", level = level.denom())
        assertFormulaTSpan(denomSpace, " ", level = level.pass())
        assertFormulaTSpan(denomShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(denomPow, "2", level = level.current())
        assertFormulaTSpan(denomRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(bar, null, level = level.bar())
        assertFormulaTSpan(fracRestoreShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun fractionInSuperscript() {
        val formula = """\(a^{\frac{b}{c}}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(8)
        val (base, space, shiftSup, num, denom) = svg.tspans()
        val (bar, restoreFracShift, restorePowShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(num, "b", level = level.num())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(denom, "c", level = level.denom())
        assertFormulaTSpan(bar, null, level = level.bar())
        assertFormulaTSpan(restoreFracShift, "\u200B", level = level.revert())
        assertFormulaTSpan(restorePowShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun fractionOnSecondLevel() {
        val formula = """\(a^{b_{\frac{c}{d}}}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(12)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPow, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, num, denom, bar, restoreFracShift) = svg.tspans().drop(5)
        val (restoreSecondLevelShift, restoreFirstLevelShift) = svg.tspans().drop(10)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0
        assertFormulaTSpan(base, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPow, "b", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(num, "c", level = level.num())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(denom, "d", level = level.denom())
        assertFormulaTSpan(bar, null, level = level.bar())
        assertFormulaTSpan(restoreFracShift, "\u200B", level = level.revert())
        assertFormulaTSpan(restoreSecondLevelShift, "\u200B", level = level.revert())
        assertFormulaTSpan(restoreFirstLevelShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
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
        private const val DEF_FONT_SIZE = 12
        private val DEF_FONT = Font(family = FontFamily("sans-serif", false), size = DEF_FONT_SIZE, isBold = false, isItalic = false)

        private fun testWidthCalculator(text: String, font: Font): Double {
            return text.length * font.size.toDouble()
        }

        private fun toTestWidth(charsCount: Int, level: TestUtil.FormulaLevel): Double {
            val fontSizeCoefficient = level.sizeValue()?.let { sizeValue ->
                (DEF_FONT_SIZE * sizeValue).roundToInt()
            } ?: DEF_FONT_SIZE
            return charsCount * fontSizeCoefficient.toDouble()
        }
    }
}