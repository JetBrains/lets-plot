/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertFormulaTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
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

        val fracPosition = toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "a", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "b", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = toTestWidth(1, level), expectedAnchor = "start")
        assertThat(width).isEqualTo(toTestWidth(1, level))
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
        var expectedWidth = 0.0

        val firstFracPosition = toTestWidth(1, level) / 2.0
        assertFormulaTSpan(firstNum, "a", level = level.num(), expectedX = firstFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(firstDenom, "b", level = level.denom(), expectedX = firstFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(firstBar, null, level = level.bar(), expectedX = firstFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreFirstShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertFormulaTSpan(sumSign, "+", level = level.current())
        expectedWidth += toTestWidth(1, level)
        val secondFracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(secondNum, "c", level = level.num(), expectedX = secondFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(secondDenom, "d", level = level.denom(), expectedX = secondFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(secondBar, null, level = level.bar(), expectedX = secondFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreSecondShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
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

        val fracPosition = (toTestWidth(1, level) + toTestWidth(1, level.copy().sup())) / 2.0
        assertFormulaTSpan(numBase, "a", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(numSpace, " ", level = level.pass())
        assertFormulaTSpan(numShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(numPow, "3", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(numRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(denomBase, "b", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denomSpace, " ", level = level.pass())
        assertFormulaTSpan(denomShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(denomPow, "2", level = level.current())
        assertFormulaTSpan(denomRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(fracRestoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
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
        val fracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "b", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(denom, "c", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(restoreFracShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
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
        val fracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "c", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(denom, "d", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(restoreFracShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertFormulaTSpan(restoreSecondLevelShift, "\u200B", level = level.revert())
        assertFormulaTSpan(restoreFirstLevelShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun textBeforeFormulaWithFraction() {
        val formula = """a+\(\frac{b}{c}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (text, num, denom, bar, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertTSpan(text, "a+")
        expectedWidth += toTestWidth(2, level)
        val fracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "b", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "c", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun textBetweenTwoFormulasWithFractions() {
        val formula = """\(\frac{a}{b}\)XY\(\frac{c}{d}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(9)
        val (firstNum, firstDenom, firstBar, firstRestoreShift, text) = svg.tspans()
        val (secondNum, secondDenom, secondBar, secondRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        val firstFracPosition = toTestWidth(1, level) / 2.0
        assertFormulaTSpan(firstNum, "a", level = level.num(), expectedX = firstFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(firstDenom, "b", level = level.denom(), expectedX = firstFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(firstBar, null, level = level.bar(), expectedX = firstFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(firstRestoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertTSpan(text, "XY")
        expectedWidth += toTestWidth(2, level)
        val secondFracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(secondNum, "c", level = level.num(), expectedX = secondFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(secondDenom, "d", level = level.denom(), expectedX = secondFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(secondBar, null, level = level.bar(), expectedX = secondFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(secondRestoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun textAfterFormulaWithFraction() {
        val formula = """\(\frac{a}{b}\)+c"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (num, denom, bar, restoreShift, text) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        val fracPosition = toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "a", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "b", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertTSpan(text, "+c")
        expectedWidth += toTestWidth(2, level)
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun twoLines() {
        val formula = "\\(\\frac{a}{b}\\)\n\\(c_i\\)"
        val (firstSvg, secondSvg) = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator)
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(firstSvg.tspans()).hasSize(4)
        val (num, denom, bar, restoreFracShift) = firstSvg.tspans()
        val firstLevel = TestUtil.FormulaLevel()

        val fracPosition = toTestWidth(1, firstLevel) / 2.0
        assertFormulaTSpan(num, "a", level = firstLevel.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "b", level = firstLevel.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = firstLevel.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(restoreFracShift, "\u200B", level = firstLevel.revert(), expectedX = toTestWidth(1, firstLevel), expectedAnchor = "start")
        assertThat(width).isGreaterThan(toTestWidth(1, firstLevel))

        assertThat(secondSvg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreIndexShift) = secondSvg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertFormulaTSpan(base, "c", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "i", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreIndexShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun linkBeforeFormulaWithFraction() {
        val formula = """<a href="https://example.com">link</a>\(\frac{a}{b}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.children()).hasSize(5)
        val linkText = svg.children().first().children().single() as SvgTSpanElement
        val (num, denom, bar, restoreShift) = svg.children().drop(1) as List<SvgTSpanElement>
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertTSpan(linkText, "link")
        expectedWidth += toTestWidth(4, level)
        val fracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "a", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "b", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun markdownBeforeFormulaWithFraction() {
        val formula = """<span style="color:blue">**text**</span>\(\frac{a}{b}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator, markdown = true).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator, markdown = true)

        assertThat(svg.tspans()).hasSize(5)
        val (markdown, num, denom, bar, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertTSpan(markdown, "text", bold = true, color = "blue")
        expectedWidth += toTestWidth(4, level)
        val fracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(num, "a", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "b", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun formulaWithAnchorMiddle() {
        val formula = """a+\(\frac{b}{c}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator, anchor = Text.HorizontalAnchor.MIDDLE).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (text, num, denom, bar, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        val expectedWidth = toTestWidth(3, level)

        assertTSpan(text, "a+", x = -expectedWidth / 2.0)
        val fracPosition = 2.5 * toTestWidth(1, level) - expectedWidth / 2.0
        assertFormulaTSpan(num, "b", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "c", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth / 2.0, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun formulaWithAnchorRight() {
        val formula = """a+\(\frac{b}{c}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator, anchor = Text.HorizontalAnchor.RIGHT).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(5)
        val (text, num, denom, bar, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()
        val expectedWidth = toTestWidth(3, level)

        assertTSpan(text, "a+", x = -expectedWidth)
        val fracPosition = 2.5 * toTestWidth(1, level) - expectedWidth
        assertFormulaTSpan(num, "b", level = level.num(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(denom, "c", level = level.denom(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(bar, null, level = level.bar(), expectedX = fracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert(), expectedX = 0.0, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun complexFormulaRegression1() {
        val formula = """\(a^{b+\frac{c}{d}}+\frac{e}{f}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(14)
        val (a, space, shiftSup, b, c) = svg.tspans()
        val (d, cdBar, cdRestoreShift, restoreShiftSup, plus) = svg.tspans().drop(5)
        val (e, f, efBar, efRestoreShift) = svg.tspans().drop(10)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertFormulaTSpan(a, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(b, "b+", level = level.current())
        expectedWidth += toTestWidth(2, level)
        val cdFracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(c, "c", level = level.num(), expectedX = cdFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(d, "d", level = level.denom(), expectedX = cdFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(cdBar, null, level = level.bar(), expectedX = cdFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(cdRestoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertFormulaTSpan(restoreShiftSup, "\u200B", level = level.revert())
        assertFormulaTSpan(plus, "+", level = level.current())
        expectedWidth += toTestWidth(1, level)
        val bigBCFracPosition = expectedWidth + toTestWidth(1, level) / 2.0
        assertFormulaTSpan(e, "e", level = level.num(), expectedX = bigBCFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(f, "f", level = level.denom(), expectedX = bigBCFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(efBar, null, level = level.bar(), expectedX = bigBCFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(efRestoreShift, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun complexFormulaRegression2() {
        val formula = """\(a^\frac{c+d}{e}+b_{\frac{f}{g+h}}\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(16)
        val (a, aSpace, aShiftSup, cd, e) = svg.tspans()
        val (cdeBar, cdeRestoreFrac, restoreAShift, plusB, bSpace) = svg.tspans().drop(5)
        val (bShiftSub, f, gh, fghBar, fghRestoreFrac) = svg.tspans().drop(10)
        val restoreBShift = svg.tspans().drop(15).single()
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertFormulaTSpan(a, "a", level = level.current())
        expectedWidth += toTestWidth(1, level)
        assertFormulaTSpan(aSpace, " ", level = level.pass())
        assertFormulaTSpan(aShiftSup, "\u200B", level = level.sup())
        val cdeFracPosition = expectedWidth + toTestWidth(3, level) / 2.0
        assertFormulaTSpan(cd, "c+d", level = level.num(), expectedX = cdeFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(e, "e", level = level.denom(), expectedX = cdeFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(cdeBar, null, level = level.bar(), expectedX = cdeFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(3, level)
        assertFormulaTSpan(cdeRestoreFrac, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertFormulaTSpan(restoreAShift, "\u200B", level = level.revert())
        assertFormulaTSpan(plusB, "+b", level = level.current())
        expectedWidth += toTestWidth(2, level)
        assertFormulaTSpan(bSpace, " ", level = level.pass())
        assertFormulaTSpan(bShiftSub, "\u200B", level = level.sub())
        val fghFracPosition = expectedWidth + toTestWidth(3, level) / 2.0
        assertFormulaTSpan(f, "f", level = level.num(), expectedX = fghFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(gh, "g+h", level = level.denom(), expectedX = fghFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(fghBar, null, level = level.bar(), expectedX = fghFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(3, level)
        assertFormulaTSpan(fghRestoreFrac, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertFormulaTSpan(restoreBShift, "\u200B", level = level.revert())
        assertThat(width).isEqualTo(expectedWidth)
    }

    @Test
    fun complexFormulaRegression3() {
        val formula = """\(a1^{b1^{\frac{c1}{c2}+c3}+b2}+a2\)"""
        val svg = RichText.toSvg(formula, DEF_FONT, ::testWidthCalculator).single()
        val width = RichText.estimateWidth(formula, DEF_FONT, ::testWidthCalculator)

        assertThat(svg.tspans()).hasSize(15)
        val (a1, aSpace, aShiftSup, b1, bSpace) = svg.tspans()
        val (bShiftSup, c1, c2, cBar, cRestoreFrac) = svg.tspans().drop(5)
        val (c3, bRestoreShift, b2, aRestoreShift, a2) = svg.tspans().drop(10)
        val level = TestUtil.FormulaLevel()
        var expectedWidth = 0.0

        assertFormulaTSpan(a1, "a1", level = level.current())
        expectedWidth += toTestWidth(2, level)
        assertFormulaTSpan(aSpace, " ", level = level.pass())
        assertFormulaTSpan(aShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(b1, "b1", level = level.current())
        expectedWidth += toTestWidth(2, level)
        assertFormulaTSpan(bSpace, " ", level = level.pass())
        assertFormulaTSpan(bShiftSup, "\u200B", level = level.sup())
        val cFracPosition = expectedWidth + toTestWidth(2, level) / 2.0
        assertFormulaTSpan(c1, "c1", level = level.num(), expectedX = cFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(c2, "c2", level = level.denom(), expectedX = cFracPosition, expectedAnchor = "middle")
        assertFormulaTSpan(cBar, null, level = level.bar(), expectedX = cFracPosition, expectedAnchor = "middle")
        expectedWidth += toTestWidth(2, level)
        assertFormulaTSpan(cRestoreFrac, "\u200B", level = level.revert(), expectedX = expectedWidth, expectedAnchor = "start")
        assertFormulaTSpan(c3, "+c3", level = level.current())
        expectedWidth += toTestWidth(3, level)
        assertFormulaTSpan(bRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(b2, "+b2", level = level.current())
        expectedWidth += toTestWidth(3, level)
        assertFormulaTSpan(aRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(a2, "+a2", level = level.current())
        expectedWidth += toTestWidth(3, level)
        assertThat(width).isEqualTo(expectedWidth)
    }

    companion object {
        private const val DEF_FONT_SIZE = 12
        private val DEF_FONT = Font(family = FontFamily(FontFamily.DEF_FAMILY_NAME, false), size = DEF_FONT_SIZE, isBold = false, isItalic = false)

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