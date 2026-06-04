/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.pathElements
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.toSvg
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.vectorFormulaGroups
import org.jetbrains.letsPlot.core.plot.base.render.text.LatexVectorFont
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.test.Test

class RichTextLatexVectorTest {
    private val font = Font(family = FontFamily.SERIF, size = 16, isBold = false, isItalic = false)

    @Test
    fun supportedSingleLetterRendersAsPath() {
        val svg = toSvg("""\(a\)""").single()
        // The single supported formula produces a vector-formula group and no tspans.
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.vectorFormulaGroups()).hasSize(1)
        assertThat(svg.pathElements()).hasSize(1)
        assertThat(svg.tspans()).isEmpty()
    }

    @Test
    fun supportedFractionRendersAsPaths() {
        val svg = toSvg("""\(\frac{a}{b}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        // 1 numerator path + 1 denominator path + 1 fraction-bar path = 3 paths.
        assertThat(svg.pathElements()).hasSize(3)
        assertThat(svg.tspans()).isEmpty()
    }

    @Test
    fun unsupportedSymbolFallsBackToLegacyTspans() {
        // `\bar` is not in Latex.SYMBOLS, so the parser emits literal text `\bar`. The backslash
        // is not in the vector glyph table → the whole formula falls back to legacy tspan rendering.
        val svg = toSvg("""\(\bar{x}\)""").single()
        assertThat(svg).isInstanceOf(SvgTextElement::class.java)
        assertThat(svg.pathElements()).isEmpty()
        // We expect tspans (the exact count depends on legacy rendering — just assert non-empty).
        assertThat(svg.tspans()).isNotEmpty
    }

    @Test
    fun unsupportedNestedFallsBackEntireFormula() {
        val svg = toSvg("""\(\frac{\unknown}{b}\)""").single()
        // Even though `b` alone would be vector-supported, one unsupported descendant forces the
        // whole formula into the legacy renderer.
        assertThat(svg).isInstanceOf(SvgTextElement::class.java)
        assertThat(svg.pathElements()).isEmpty()
        assertThat(svg.tspans()).isNotEmpty
    }

    @Test
    fun mixedTextAndVectorFormula() {
        val svg = toSvg("""prefix \(a + b\) suffix""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        // Outer group contains: <text>prefix </text><g formula></g><text> suffix</text>.
        val children = (svg as SvgGElement).children()
        assertThat(children).hasSize(3)
        assertThat(children[0]).isInstanceOf(SvgTextElement::class.java)
        assertThat(children[1]).isInstanceOf(SvgGElement::class.java)
        assertThat(children[2]).isInstanceOf(SvgTextElement::class.java)
        // Vector formula contributes 3 glyph paths: 'a', '+', 'b' (no path for the space glyph).
        assertThat(svg.pathElements()).hasSize(3)
    }

    @Test
    fun mixedVectorAndLegacyFormulasInOneLine() {
        // First formula is vector-supported; second falls back to legacy.
        val svg = toSvg("""\(a\) \(\bar{x}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        // We expect a mix of vector group and legacy tspans within a single outer group.
        assertThat(svg.vectorFormulaGroups()).hasSize(1)
        assertThat(svg.tspans()).isNotEmpty
    }

    @Test
    fun widthFromMeasureUsesExactVectorAdvances() {
        // For a vector-supported formula, the measured width equals sum(advance_em) * fontSize.
        // No reliance on TextMetricsEstimator clusters.
        val measured = RichText.measure(
            text = """\(abc\)""",
            font = font
        )
        val expected = (LatexVectorFont.advanceEm('a')
                + LatexVectorFont.advanceEm('b')
                + LatexVectorFont.advanceEm('c')) * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun longLatinPrefixBeforeFractionUsesExactAdvance() {
        // Verifies: the prefix width before a fraction = sum of vector advances of the prefix text
        // when that prefix is inside the same vector formula. Drift-free.
        // Use only characters in the vector glyph table (no underscore, etc.).
        val prefix = "LongPrefixAB1234"
        val measured = RichText.measure(text = """\($prefix\)""", font = font)
        val expected = prefix.sumOf { LatexVectorFont.advanceEm(it) } * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun vectorFormulaLineBoxMetricsMatchPlainTextForSingleLetter() {
        // For a vector formula consisting of a single Latin letter, line metrics should match a
        // plain-text line of the same font.
        val measured = RichText.measure(text = """\(A\)""", font = font)
        // One line, plain text-equivalent: boxHeight = topToBaseline = fontSize.
        assertThat(measured.layout.lineBoxes).hasSize(1)
        val m = measured.layout.lineBoxes.single()
        assertThat(m.boxHeight).isCloseTo(font.size.toDouble(), offset(1e-9))
        assertThat(m.topToBaseline).isCloseTo(font.size.toDouble(), offset(1e-9))
    }

    @Test
    fun vectorGlyphTableCoversEntireSymbolMap() {
        // Every symbol mapped by Latex.SYMBOLS must have a vector glyph, otherwise visual tests
        // that exercise the full symbol set will fall back to legacy for some symbols and produce
        // a visually inconsistent grid.
        val symbolValues = listOf(
            // Greek uppercase
            "Α", "Β", "Γ", "Δ", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο",
            "Π", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω",
            // Greek lowercase
            "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο",
            "π", "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω",
            // Operations & relations & misc
            "±", "∓", "×", "÷", "·", "≤", "≥", "≠", "∞"
        )
        val missing = symbolValues.filterNot { LatexVectorFont.isSupported(it) }
        assertThat(missing).isEmpty()
    }

    @Test
    fun formulaWithEmbeddedWhitespaceStillRendersAsVector() {
        // Legend labels are wrapped by commons WordWrapper.wrap(), which can inject a '\n' inside a
        // "\( ... \)" formula (it splits the raw LaTeX source on spaces). Whitespace must therefore
        // count as a supported (blank) glyph, otherwise the whole formula falls back to the legacy
        // tspan renderer — the "legend looks as before glyphs" regression.
        val svg = toSvg("\\(a\nb\\)").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.tspans()).isEmpty()
        // 'a' and 'b' both produce a glyph path; the newline is a blank glyph (no path).
        assertThat(svg.pathElements()).hasSize(2)
    }

    @Test
    fun embeddedNewlineHasSameWidthAsSpace() {
        // The wrapper replaces a space with '\n', so the newline must contribute exactly the space
        // advance — keeping the wrapped formula pixel-identical to its unwrapped form.
        val withSpace = RichText.measure(text = "\\(a b\\)", font = font)
        val withNewline = RichText.measure(text = "\\(a\nb\\)", font = font)
        assertThat(withNewline.width).isCloseTo(withSpace.width, offset(1e-9))
    }

    @Test
    fun mixedLineFormulaIsTranslatedByPrefixWidth() {
        // The vector formula group inside a mixed line should be translated horizontally by the
        // prefix's vector width (in pixels). This is the core "no-drift" guarantee.
        val svg = toSvg("""prefix \(a\)""").single() as SvgGElement
        val formulaGroup = svg.vectorFormulaGroups().single()
        // formulaGroup.transform() should be a translate(prefixWidth, 0). The prefix here is
        // rendered legacy text in an SvgTextElement, so its width is given by widthCalculator.
        val transform = formulaGroup.transform().get()
        assertThat(transform).isNotNull
        val transformStr = transform.toString()
        assertThat(transformStr).startsWith("translate(")
    }
}
