/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertFormulaTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import kotlin.test.Test

class RichTextLatexTest {
    @Test
    fun noLatex() {
        val text = "There are no formulas here"
        val svg = RichText.toSvg(text, DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            text,
            level = TestUtil.FormulaLevel()
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
        assertFormulaTSpan(
            svg.tspans().single(),
            " ",
            level = TestUtil.FormulaLevel()
        )
    }

    @Test
    fun simpleFormulaSpecialSymbol() {
        val svg = RichText.toSvg("""\(\infty\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            "∞",
            level = TestUtil.FormulaLevel()
        )
    }

    @Test
    fun simpleFormulaLetter() {
        val svg = RichText.toSvg("""\(\Omega\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            "Ω",
            level = TestUtil.FormulaLevel()
        )
    }

    @Test
    fun simpleFormulaSuperscript() {
        val svg = RichText.toSvg("""\(a^b\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(pow, "b", level = level.pass())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaSuperscriptWithBraces() {
        val svg = RichText.toSvg("""\(a^{bc}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(pow, "bc", level = level.pass())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaSubscript() {
        val svg = RichText.toSvg("""\(a_b\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "b", level = level.pass())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaSubscriptWithBraces() {
        val svg = RichText.toSvg("""\(a_{bc}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "bc", level = level.pass())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMultipleSuperscript() {
        val svg = RichText.toSvg("""\(a^{b^c}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPower, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSup, secondLevelPower, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPower, "b", level = level.pass())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(secondLevelPower, "c", level = level.pass())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMultipleSubscript() {
        val svg = RichText.toSvg("""\(a_{i_1}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSub, firstLevelIndex, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, secondLevelIndex, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(firstLevelIndex, "i", level = level.pass())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(secondLevelIndex, "1", level = level.pass())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMixedSupSub() {
        val svg = RichText.toSvg("""\(a^{b_i}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPower, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, secondLevelIndex, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPower, "b", level = level.pass())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(secondLevelIndex, "i", level = level.pass())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMixedSubSup() {
        val svg = RichText.toSvg("""\(a_{I^n}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSub, firstLevelIndex, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSup, secondLevelPower, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        assertFormulaTSpan(base, "a", level = level.pass())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(firstLevelIndex, "I", level = level.pass())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(secondLevelPower, "n", level = level.pass())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaFraction() {
        val svg = RichText.toSvg("""\(\frac{a}{b}\)""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = false).single()

        assertThat(svg.tspans()).hasSize(4)
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