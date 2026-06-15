/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.jetbrains.letsPlot.commons.values.Colors.parseColor
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.estimateWidth
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.pathElements
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.toSvg
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.toTestWidth
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.vectorFormulaGroups
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.vectorTextElements
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.wholeText
import org.jetbrains.letsPlot.core.plot.base.render.text.LatexVectorFont
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import kotlin.math.max
import kotlin.test.Test

class RichTextLatexVectorTest {
    private val font = Font(family = FontFamily.SERIF, size = 16, isBold = false, isItalic = false)

    @Test
    fun noLatexRendersAsPlainTextLine() {
        val text = "There are no formulas here"
        val svg = toSvg(text).single()

        assertThat(svg).isInstanceOf(SvgTextElement::class.java)
        assertThat(svg.tspans()).hasSize(1)
        assertTSpan(svg.tspans().single(), text)
        assertThat(estimateWidth(text)).isEqualTo(toTestWidth(text))
    }

    @Test
    fun emptyFormulaHasNoGlyphsAndZeroWidth() {
        val formula = """\(\)"""
        val svg = toSvg(formula).single()

        assertThat(svg.tspans()).isEmpty()
        assertThat(svg.pathElements()).isEmpty()
        assertThat(estimateWidth(formula)).isEqualTo(0.0)
    }

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
    fun glyphPathHasNoBakedFillWhenUncolored() {
        val svg = toSvg("""\(a + \frac{b}{c}\)""").single()
        val paths = svg.pathElements()
        assertThat(paths).isNotEmpty
        paths.forEach { path ->
            assertThat(path.fill().get())
                .describedAs("uncolored glyph/bar path must have no baked fill")
                .isNull()
        }
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
    fun unsupportedSymbolSplicesOnlyTheUnsupportedBox() {
        // `\bar` is not in Latex.SYMBOLS, so the parser emits literal text `\bar`. Only the backslash
        // is missing from the vector glyph table, so just that one box falls back to a <text> run;
        // the supported letters `b`, `a`, `r` (and `x`) still render as vector paths.
        val svg = toSvg("""\(\bar{x}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.pathElements()).hasSize(4) // b, a, r, x
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("\\")
    }

    @Test
    fun unsupportedNestedSplicesOnlyTheUnsupportedBox() {
        val svg = toSvg("""\(\frac{\unknown}{b}\)""").single()
        // A single unsupported box (the backslash of `\unknown`) no longer forces the whole formula
        // to legacy: the supported letters, `b`, and the fraction bar still render as vector paths.
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        // 7 numerator letter paths (u, n, k, n, o, w, n) + 1 denominator (b) + 1 fraction bar = 9.
        assertThat(svg.pathElements()).hasSize(9)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("\\")
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
    fun mixedFullySupportedAndPartiallyUnsupportedFormulas() {
        // Both formulas are now vector groups; the second splices its unsupported backslash box as a
        // <text> run while still drawing its supported letters as paths.
        val svg = toSvg("""\(a\) \(\bar{x}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.vectorFormulaGroups()).hasSize(2)
        // 'a' + (b, a, r, x) = 5 glyph paths across the two formulas.
        assertThat(svg.pathElements()).hasSize(5)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("\\")
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
    fun superscriptWidthUsesReducedLevelAdvance() {
        val svg = toSvg("""\(a^b\)""").single()
        assertThat(svg.pathElements()).hasSize(2)

        val measured = RichText.measure(text = """\(a^b\)""", font = font)
        val expected = LatexVectorFont.advanceEm('a') * font.size +
                LatexVectorFont.advanceEm('b') * font.size * INDEX_SIZE_FACTOR
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun subscriptWidthUsesReducedLevelAdvance() {
        val svg = toSvg("""\(a_b\)""").single()
        assertThat(svg.pathElements()).hasSize(2)

        val measured = RichText.measure(text = """\(a_b\)""", font = font)
        val expected = LatexVectorFont.advanceEm('a') * font.size +
                LatexVectorFont.advanceEm('b') * font.size * INDEX_SIZE_FACTOR
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun multipleFractionsWidthIsSumOfMaxima() {
        val formula = """\(\frac{a}{b}+\frac{c}{d}\)"""
        val svg = toSvg(formula).single()
        assertThat(svg.pathElements()).hasSize(7)

        val measured = RichText.measure(text = formula, font = font)
        val expected = max(LatexVectorFont.advanceEm('a'), LatexVectorFont.advanceEm('b')) * font.size +
                LatexVectorFont.advanceEm('+') * font.size +
                max(LatexVectorFont.advanceEm('c'), LatexVectorFont.advanceEm('d')) * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun textAfterFormulaStartsFreshTextRunWithExplicitX() {
        val firstFormulaWidth = max(LatexVectorFont.advanceEm('a'), LatexVectorFont.advanceEm('b')) * font.size
        val svg = toSvg("""\(\frac{a}{b}\)+c""").single() as SvgGElement
        val children = svg.children()

        assertThat(children).hasSize(2)
        assertThat(children[0]).isInstanceOf(SvgGElement::class.java)
        assertThat(children[1]).isInstanceOf(SvgTextElement::class.java)
        val textRun = children[1] as SvgTextElement
        assertThat(textRun.tspans()).hasSize(1)
        assertTSpan(textRun.tspans().single(), "+c", x = firstFormulaWidth)
    }

    @Test
    fun textBetweenTwoFormulasFlowsByAdvance() {
        val firstFormulaWidth = max(LatexVectorFont.advanceEm('a'), LatexVectorFont.advanceEm('b')) * font.size
        val svg = toSvg("""\(\frac{a}{b}\)XY\(\frac{c}{d}\)""").single() as SvgGElement
        val children = svg.children()

        assertThat(children).hasSize(3)
        assertThat(children[0]).isInstanceOf(SvgGElement::class.java)
        assertThat(children[1]).isInstanceOf(SvgTextElement::class.java)
        assertThat(children[2]).isInstanceOf(SvgGElement::class.java)
        assertThat((children[1] as SvgTextElement).tspans().single().wholeText()).isEqualTo("XY")
        val secondFormulaX = extractTranslateX((children[2] as SvgGElement).transform().get()!!)
        assertThat(secondFormulaX).isCloseTo(firstFormulaWidth + toTestWidth("XY"), offset(1e-9))
    }

    @Test
    fun twoFormulaLinesEachRenderAsGroup() {
        val svg = toSvg("\\(\\frac{a}{b}\\)\n\\(c_i\\)")

        assertThat(svg).hasSize(2)
        svg.forEach { assertThat(it).isInstanceOf(SvgGElement::class.java) }
        assertThat(svg[0].pathElements()).hasSize(3)
        assertThat(svg[1].pathElements()).hasSize(2)
    }

    @Test
    fun specialSymbolRendersAsSingleVectorPath() {
        val infinity = toSvg("""\(\infty\)""").single()
        assertThat(infinity.pathElements()).hasSize(1)
        assertThat(infinity.tspans()).isEmpty()
        assertThat(RichText.measure(text = """\(\infty\)""", font = font).width)
            .isCloseTo(LatexVectorFont.advanceEm('∞') * font.size, offset(1e-9))

        val omega = toSvg("""\(\Omega\)""").single()
        assertThat(omega.pathElements()).hasSize(1)
        assertThat(omega.tspans()).isEmpty()
        assertThat(RichText.measure(text = """\(\Omega\)""", font = font).width)
            .isCloseTo(LatexVectorFont.advanceEm('Ω') * font.size, offset(1e-9))
    }

    @Test
    fun anchorMiddleAndRightShiftVectorLineOrigin() {
        val text = """a+\(\frac{b}{c}\)"""
        val lineWidth = toTestWidth("a+") +
                max(LatexVectorFont.advanceEm('b'), LatexVectorFont.advanceEm('c')) * font.size

        val leftPrefixX = prefixTSpanX(toSvg(text, anchor = Text.HorizontalAnchor.LEFT).single() as SvgGElement)
        val middlePrefixX = prefixTSpanX(toSvg(text, anchor = Text.HorizontalAnchor.MIDDLE).single() as SvgGElement)
        val rightPrefixX = prefixTSpanX(toSvg(text, anchor = Text.HorizontalAnchor.RIGHT).single() as SvgGElement)

        assertThat(middlePrefixX).isCloseTo(leftPrefixX - lineWidth / 2.0, offset(1e-9))
        assertThat(rightPrefixX).isCloseTo(leftPrefixX - lineWidth, offset(1e-9))
    }

    @Test
    fun markdownColorPropagatesToFormulaGlyphPaths() {
        val svg = toSvg("""**foo** ***<span style="color:red">\( bar^2 \)</span>*** baz""", markdown = true)
            .single() as SvgGElement
        val tspans = svg.tspans()

        assertThat(tspans).hasSize(3)
        assertThat(tspans[0].wholeText()).isEqualTo("foo")
        assertThat(tspans[0].fontWeight().get()).isEqualTo("bold")
        assertThat(tspans[1].wholeText()).isEqualTo(" ")
        assertThat(tspans[2].wholeText()).isEqualTo(" baz")
        assertThat(tspans[2].fontWeight().get()).isNull()

        val paths = svg.pathElements()
        assertThat(paths).hasSize(4)
        val red = SvgColors.create(parseColor("red"))
        // Bold/italic are not assertable on vector glyph paths; only color reaches the vector group.
        paths.forEach { path ->
            assertThat(path.fill().get()).isEqualTo(red)
        }
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
    fun superscriptLineBoxMetricsIncludeRenderedShift() {
        // A fraction inside a superscript: the measured line box must reflect the same upward shift
        // the renderer applies (-INDEX_RELATIVE_SHIFT * content.levelFontSize), so the box top grows
        // and the bottom shrinks instead of leaving a phantom band below the formula.
        // Derivation (font 16, SERIF): fraction-in-superscript content top=16.352, bottom=8.512;
        // shift = 0.4 * 16 * 0.7 = 4.48; raised -> top=20.832, bottom=4.032; merged with 'A' (16, 0).
        // Under the old (buggy) behavior this would be top=16.352 (no shift) — so this test
        // distinguishes the fix.
        val measured = RichText.measure(text = """\(A^{\frac{b}{c}}\)""", font = font)
        assertThat(measured.layout.lineBoxes).hasSize(1)
        val m = measured.layout.lineBoxes.single()
        assertThat(m.boxHeight).isCloseTo(24.864, offset(1e-6))
        assertThat(m.topToBaseline).isCloseTo(20.832, offset(1e-6))
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

    @Test
    fun plainTextPrefixPinBehaviorUnchanged() {
        // Regression guard: a single plain-Text prefix before a vector formula was already pinned;
        // the new code must produce identical attributes.
        val svg = toSvg("""prefix \(a + b\)""").single() as SvgGElement
        val prefixTextEl = svg.children()[0] as SvgTextElement
        val prefixTspan  = prefixTextEl.children()[0] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("prefix ")
        assertThat(prefixTspan.textAnchor().get()).isEqualTo("end")
        assertThat(prefixTspan.x().get()).isEqualTo(formulaLeftEdge)
    }

    @Test
    fun pureFormulaLineHasNoPrefixPin() {
        // A line that starts with a vector formula has no text-like prefix; no pin is applied.
        val svg = toSvg("""\(a + b\)""").single()
        assertThat(svg.tspans()).isEmpty()
    }

    @Test
    fun linkPrefixBeforeVectorFormulaPinsRightEdge() {
        // Hyperlink prefix: <a>GitHub</a> & \(a + b\)
        // Text("") from Plaintext::parse is dropped by parseBreaks (empty content), so the prefix
        // spans are [HyperlinkElement("GitHub"), Text(" & ")]. The chunk anchor sits on the first
        // addressable tspan — the one inside <a> — with x=formulaLeftEdge, text-anchor=end.
        val svg = toSvg("""<a href="https://example.com">GitHub</a> & \(a + b\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        val outer = svg as SvgGElement
        // outer has 2 children: SvgTextElement (prefix) and SvgGElement (formula).
        assertThat(outer.children()).hasSize(2)
        val prefixTextEl = outer.children()[0] as SvgTextElement
        // prefixTextEl has 2 children: SvgAElement and SvgTSpanElement(" & ").
        assertThat(prefixTextEl.children()).hasSize(2)
        val aEl         = prefixTextEl.children()[0] as SvgAElement
        val githubTspan = aEl.children().single() as SvgTSpanElement
        val andTspan    = prefixTextEl.children()[1] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("GitHub") + toTestWidth(" & ")
        // GitHub tspan (first prefix tspan) anchors the chunk: text-anchor=end, x=formulaLeftEdge.
        assertThat(githubTspan.textAnchor().get()).isEqualTo("end")
        assertThat(githubTspan.x().get()).isEqualTo(formulaLeftEdge)
        // " & " tspan: end-anchored, no explicit x (flows within the chunk).
        assertThat(andTspan.textAnchor().get()).isEqualTo("end")
        assertThat(andTspan.x().get()).isNull()
        // Formula group still carries a translate transform (position unchanged).
        val formulaTransform = (outer.children()[1] as SvgGElement).transform().get()
        assertThat(formulaTransform).isNotNull
        assertThat(formulaTransform.toString()).startsWith("translate(")
    }

    @Test
    fun markdownBoldPrefixBeforeVectorFormulaPinsRightEdge() {
        // **GitHub** & \(a + b\) (markdown=true)
        // Parsed as [StrongStart, Text("GitHub"), StrongEnd, Text(" & "), VectorFormula].
        val svg = toSvg("""**GitHub** & \(a + b\)""", markdown = true).single() as SvgGElement
        val prefixTextEl = svg.children()[0] as SvgTextElement
        val githubTspan  = prefixTextEl.children()[0] as SvgTSpanElement
        val andTspan     = prefixTextEl.children()[1] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("GitHub") + toTestWidth(" & ")
        assertThat(githubTspan.textAnchor().get()).isEqualTo("end")
        assertThat(githubTspan.x().get()).isEqualTo(formulaLeftEdge)
        assertThat(andTspan.textAnchor().get()).isEqualTo("end")
        assertThat(andTspan.x().get()).isNull()
    }

    @Test
    fun markdownEmphasisPrefixBeforeVectorFormulaPinsRightEdge() {
        // *GitHub* & \(a + b\) (markdown=true)
        val svg = toSvg("""*GitHub* & \(a + b\)""", markdown = true).single() as SvgGElement
        val prefixTextEl = svg.children()[0] as SvgTextElement
        val githubTspan  = prefixTextEl.children()[0] as SvgTSpanElement
        val andTspan     = prefixTextEl.children()[1] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("GitHub") + toTestWidth(" & ")
        assertThat(githubTspan.textAnchor().get()).isEqualTo("end")
        assertThat(githubTspan.x().get()).isEqualTo(formulaLeftEdge)
        assertThat(andTspan.textAnchor().get()).isEqualTo("end")
        assertThat(andTspan.x().get()).isNull()
    }

    @Test
    fun markdownColorPrefixBeforeVectorFormulaPinsRightEdge() {
        // <span style='color:red'>GitHub</span> & \(a + b\) (markdown=true)
        val svg = toSvg("""<span style='color:red'>GitHub</span> & \(a + b\)""", markdown = true).single() as SvgGElement
        val prefixTextEl = svg.children()[0] as SvgTextElement
        val githubTspan  = prefixTextEl.children()[0] as SvgTSpanElement
        val andTspan     = prefixTextEl.children()[1] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("GitHub") + toTestWidth(" & ")
        assertThat(githubTspan.textAnchor().get()).isEqualTo("end")
        assertThat(githubTspan.x().get()).isEqualTo(formulaLeftEdge)
        assertThat(andTspan.textAnchor().get()).isEqualTo("end")
        assertThat(andTspan.x().get()).isNull()
    }

    // --- Per-glyph fallback splice (LATEX_PER_GLYPH_FALLBACK_PLAN, Step 4) -------------------

    @Test
    fun unsupportedGlyphInMixedRunSplicesOneFallbackText() {
        // `Č` is not in the vector glyph table, so the formula renders the supported boxes as paths
        // ('+' and 'b') and splices the unsupported `Č` as a single class-marked <text> run.
        val svg = toSvg("""\(Č + b\)""").single() as SvgGElement
        // '+' and 'b' are vector paths (spaces inside a formula are dropped by the tokenizer).
        assertThat(svg.pathElements()).hasSize(2)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("Č")
    }

    @Test
    fun mixedRunWidthIsSupportedAdvancesPlusEstimatedUnsupportedRun() {
        // Width = supported boxes via exact em-advances + the unsupported run via the text estimator.
        // No whole-formula estimator fallback: the supported parts stay drift-free.
        val measured = RichText.measure(text = """\(Č + b\)""", font = font)
        val expected = toTestWidth("Č") +
                (LatexVectorFont.advanceEm('+') + LatexVectorFont.advanceEm('b')) * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun fallbackTextIsPositionedAfterPrecedingSupportedBoxes() {
        // In a single TextNode "bČ", the fallback `Č` <text> must start at the advance of the
        // preceding supported box 'b' — proving box position is independent of drawing mode.
        val svg = toSvg("""\(bČ\)""").single() as SvgGElement
        assertThat(svg.pathElements()).hasSize(1) // 'b'
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("Č")
        assertThat(fallback.x().get()).isCloseTo(LatexVectorFont.advanceEm('b') * font.size, offset(1e-9))
    }

    @Test
    fun fallbackTextInSuperscriptCarriesReducedFontSize() {
        // `\(b^{Č}\)`: the unsupported `Č` is at superscript level 1, so its baked font-size is the
        // rounded reduced level size (16 * 0.7 = 11.2 -> roundToInt = 11), matching measurement.
        val svg = toSvg("""\(b^{Č}\)""").single() as SvgGElement
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("Č")
        assertThat(fallback.getAttribute(SvgTextContent.FONT_SIZE).get()).isEqualTo("11px")
    }

    @Test
    fun pureUnsupportedRunIsOneFallbackTextWithNoPaths() {
        // An all-unsupported run produces a single fallback <text>, no glyph paths, and the group
        // width equals the text-estimator width of the run.
        val svg = toSvg("""\(ČŠ\)""").single() as SvgGElement
        assertThat(svg.pathElements()).isEmpty()
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("ČŠ")
        val measured = RichText.measure(text = """\(ČŠ\)""", font = font)
        assertThat(measured.width).isCloseTo(toTestWidth("ČŠ"), offset(1e-9))
    }

    private fun prefixTSpanX(line: SvgGElement): Double {
        return ((line.children()[0] as SvgTextElement).children()[0] as SvgTSpanElement).x().get()!!
    }

    private fun extractTranslateX(transform: SvgTransform): Double {
        val match = Regex("""translate\(([^ ]+) ([^)]+)\)""").find(transform.toString())
            ?: error("Could not parse translate transform: $transform")
        return match.groupValues[1].trim().toDouble()
    }

    companion object {
        private const val INDEX_SIZE_FACTOR = 0.7
    }
}
