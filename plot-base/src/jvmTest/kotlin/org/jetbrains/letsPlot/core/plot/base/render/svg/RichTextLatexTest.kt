/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertFormulaTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import kotlin.test.Test

class RichTextLatexTest {
    @Test
    fun noLatex() {
        val text = "There are no formulas here"
        val svg = RichText.toSvg(text).single()

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            text,
            level = TestUtil.FormulaLevel()
        )
    }

    @Test
    fun emptyFormula() {
        val formula = """\(\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(0)
    }

    @Test
    fun simpleFormulaWithSpace() {
        val formula = """\(A B\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(2)
        val (first, second) = svg.tspans()
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(first, "A", level = level.current())
        assertFormulaTSpan(second, "B", level = level.current())
    }

    @Test
    fun simpleFormulaWithExplicitSpace() {
        val formula = """\(A \quad B\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(3)
        val (first, space, second) = svg.tspans()
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(first, "A", level = level.current())
        assertFormulaTSpan(space, " ", level = level.current())
        assertFormulaTSpan(second, "B", level = level.current())
    }

    @Test
    fun simpleFormulaSpecialSymbol() {
        val formula = """\(\infty\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            "∞",
            level = TestUtil.FormulaLevel()
        )
    }

    @Test
    fun simpleFormulaLetter() {
        val formula = """\(\Omega\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(1)
        assertFormulaTSpan(
            svg.tspans().single(),
            "Ω",
            level = TestUtil.FormulaLevel()
        )
    }

    @Test
    fun simpleFormulaSuperscript() {
        val formula = """\(a^b\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(pow, "b", level = level.current())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaSuperscriptWithBraces() {
        val formula = """\(a^{bc}\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSup, pow, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(pow, "bc", level = level.current())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaSubscript() {
        val formula = """\(a_b\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "b", level = level.current())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun simpleFormulaSubscriptWithBraces() {
        val formula = """\(a_{bc}\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(5)
        val (base, space, shiftSub, index, restoreShift) = svg.tspans()
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(space, " ", level = level.pass())
        assertFormulaTSpan(shiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(index, "bc", level = level.current())
        assertFormulaTSpan(restoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMultipleSuperscript() {
        val formula = """\(a^{b^c}\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPow, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSup, secondLevelPow, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPow, "b", level = level.current())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(secondLevelPow, "c", level = level.current())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMultipleSubscript() {
        val formula = """\(a_{i_1}\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSub, firstLevelIndex, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, secondLevelIndex, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(firstLevelIndex, "i", level = level.current())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(secondLevelIndex, "1", level = level.current())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMixedSupSub() {
        val formula = """\(a^{b_i}\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSup, firstLevelPow, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSub, secondLevelIndex, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(firstLevelPow, "b", level = level.current())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(secondLevelIndex, "i", level = level.current())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }

    @Test
    fun formulaWithMixedSubSup() {
        val formula = """\(a_{I^n}\)"""
        val svg = RichText.toSvg(formula).single()

        assertThat(svg.tspans()).hasSize(9)
        val (base, firstLevelSpace, firstLevelShiftSub, firstLevelIndex, secondLevelSpace) = svg.tspans()
        val (secondLevelShiftSup, secondLevelPow, secondLevelRestoreShift, firstLevelRestoreShift) = svg.tspans().drop(5)
        val level = TestUtil.FormulaLevel()

        assertFormulaTSpan(base, "a", level = level.current())
        assertFormulaTSpan(firstLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(firstLevelShiftSub, "\u200B", level = level.sub())
        assertFormulaTSpan(firstLevelIndex, "I", level = level.current())
        assertFormulaTSpan(secondLevelSpace, " ", level = level.pass())
        assertFormulaTSpan(secondLevelShiftSup, "\u200B", level = level.sup())
        assertFormulaTSpan(secondLevelPow, "n", level = level.current())
        assertFormulaTSpan(secondLevelRestoreShift, "\u200B", level = level.revert())
        assertFormulaTSpan(firstLevelRestoreShift, "\u200B", level = level.revert())
    }
}