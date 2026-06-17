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
        assertThat(svg.pathElements()).hasSize(3)
        assertThat(svg.tspans()).isEmpty()
    }

    @Test
    fun unsupportedSymbolSplicesOnlyTheUnsupportedBox() {
        // `\bar` is parsed as literal text; only its unsupported backslash falls back to <text>.
        val svg = toSvg("""\(\bar{x}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.pathElements()).hasSize(4)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("\\")
    }

    @Test
    fun unsupportedNestedSplicesOnlyTheUnsupportedBox() {
        val svg = toSvg("""\(\frac{\unknown}{b}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.pathElements()).hasSize(9)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("\\")
    }

    @Test
    fun mixedTextAndVectorFormula() {
        val svg = toSvg("""prefix \(a + b\) suffix""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        val children = (svg as SvgGElement).children()
        assertThat(children).hasSize(3)
        assertThat(children[0]).isInstanceOf(SvgTextElement::class.java)
        assertThat(children[1]).isInstanceOf(SvgGElement::class.java)
        assertThat(children[2]).isInstanceOf(SvgTextElement::class.java)
        assertThat(svg.pathElements()).hasSize(3)
    }

    @Test
    fun mixedFullySupportedAndPartiallyUnsupportedFormulas() {
        val svg = toSvg("""\(a\) \(\bar{x}\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.vectorFormulaGroups()).hasSize(2)
        assertThat(svg.pathElements()).hasSize(5)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("\\")
    }

    @Test
    fun widthFromMeasureUsesExactVectorAdvances() {
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
    fun explicitSpacesUseTeXWidths() {
        fun spaceWidth(command: String): Double {
            val withSpace = RichText.measure(text = """\(X${command}X\)""", font = font).width
            val withoutSpace = RichText.measure(text = """\(XX\)""", font = font).width
            return withSpace - withoutSpace
        }

        val thin = spaceWidth("""\,""")
        val medium = spaceWidth("""\:""")
        val interword = spaceWidth("""\ """)
        val quad = spaceWidth("""\quad """)
        val qquad = spaceWidth("""\qquad """)

        assertThat(thin).isGreaterThan(0.0)
        assertThat(thin).isLessThan(medium)
        assertThat(medium).isLessThan(interword)
        assertThat(interword).isLessThan(quad)
        assertThat(quad).isLessThan(qquad)
        assertThat(thin).isCloseTo(font.size * 3.0 / 18.0, offset(1e-9))
        assertThat(medium).isCloseTo(font.size * 4.0 / 18.0, offset(1e-9))
        assertThat(interword).isCloseTo(font.size * 6.0 / 18.0, offset(1e-9))
        assertThat(quad).isCloseTo(font.size.toDouble(), offset(1e-9))
        assertThat(qquad).isCloseTo(2.0 * quad, offset(1e-9))
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
                max(LatexVectorFont.advanceEm('c'), LatexVectorFont.advanceEm('d')) * font.size +
                2.0 * font.size * FRACTION_SIDE_SPACING_EM
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun fractionHasThinGapFromInteriorSiblings() {
        val formula = """\(a\frac{b}{c}d\)"""
        val svg = toSvg(formula).single()
        assertThat(svg.pathElements()).hasSize(5)

        val measured = RichText.measure(text = formula, font = font)
        val expected = LatexVectorFont.advanceEm('a') * font.size +
                max(LatexVectorFont.advanceEm('b'), LatexVectorFont.advanceEm('c')) * font.size +
                LatexVectorFont.advanceEm('d') * font.size +
                2.0 * font.size * FRACTION_SIDE_SPACING_EM
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun loneFractionHasNoThinGap() {
        val formula = """\(\frac{a}{b}\)"""
        val svg = toSvg(formula).single()
        assertThat(svg.pathElements()).hasSize(3)

        val measured = RichText.measure(text = formula, font = font)
        val expected = max(LatexVectorFont.advanceEm('a'), LatexVectorFont.advanceEm('b')) * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun fractionAfterExplicitSpaceHasNoExtraThinGap() {
        val formula = """\(a\,\frac{b}{c}\)"""
        val svg = toSvg(formula).single()
        assertThat(svg.pathElements()).hasSize(4)

        val measured = RichText.measure(text = formula, font = font)
        val expected = LatexVectorFont.advanceEm('a') * font.size +
                font.size * FRACTION_SIDE_SPACING_EM +
                max(LatexVectorFont.advanceEm('b'), LatexVectorFont.advanceEm('c')) * font.size
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
        paths.forEach { path ->
            assertThat(path.fill().get()).isEqualTo(red)
        }
    }

    @Test
    fun longLatinPrefixBeforeFractionUsesExactAdvance() {
        val prefix = "LongPrefixAB1234"
        val measured = RichText.measure(text = """\($prefix\)""", font = font)
        val expected = prefix.sumOf { LatexVectorFont.advanceEm(it) } * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun vectorFormulaLineBoxMetricsMatchPlainTextForSingleLetter() {
        val measured = RichText.measure(text = """\(A\)""", font = font)
        assertThat(measured.layout.lineBoxes).hasSize(1)
        val m = measured.layout.lineBoxes.single()
        assertThat(m.boxHeight).isCloseTo(font.size.toDouble(), offset(1e-9))
        assertThat(m.topToBaseline).isCloseTo(font.size.toDouble(), offset(1e-9))
    }

    @Test
    fun superscriptLineBoxMetricsIncludeRenderedShift() {
        // Must include the same superscript shift used by rendering; otherwise a phantom lower band appears.
        // For font 16: shifted fraction top=20.832, bottom=4.032, merged with 'A' gives height 24.864.
        val measured = RichText.measure(text = """\(A^{\frac{b}{c}}\)""", font = font)
        assertThat(measured.layout.lineBoxes).hasSize(1)
        val m = measured.layout.lineBoxes.single()
        assertThat(m.boxHeight).isCloseTo(24.864, offset(1e-6))
        assertThat(m.topToBaseline).isCloseTo(20.832, offset(1e-6))
    }

    @Test
    fun vectorGlyphTableCoversEntireSymbolMap() {
        // Keep Latex.SYMBOLS fully vector-backed to avoid mixed vector/legacy symbol grids.
        val symbolValues = listOf(
            "Α", "Β", "Γ", "Δ", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο",
            "Π", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω",
            "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο",
            "π", "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω",
            "±", "∓", "×", "÷", "·", "≤", "≥", "≠", "∞"
        )
        val missing = symbolValues.filterNot { LatexVectorFont.isSupported(it) }
        assertThat(missing).isEmpty()
    }

    @Test
    fun formulaWithEmbeddedWhitespaceStillRendersAsVector() {
        // WordWrapper can inject '\n' inside raw formulas; whitespace must stay a supported blank glyph.
        val svg = toSvg("\\(a\nb\\)").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        assertThat(svg.tspans()).isEmpty()
        assertThat(svg.pathElements()).hasSize(2)
    }

    @Test
    fun embeddedNewlineHasSameWidthAsSpace() {
        val withSpace = RichText.measure(text = "\\(a b\\)", font = font)
        val withNewline = RichText.measure(text = "\\(a\nb\\)", font = font)
        assertThat(withNewline.width).isCloseTo(withSpace.width, offset(1e-9))
    }

    @Test
    fun mixedLineFormulaIsTranslatedByPrefixWidth() {
        val svg = toSvg("""prefix \(a\)""").single() as SvgGElement
        val formulaGroup = svg.vectorFormulaGroups().single()
        val transform = formulaGroup.transform().get()
        assertThat(transform).isNotNull
        val transformStr = transform.toString()
        assertThat(transformStr).startsWith("translate(")
    }

    @Test
    fun plainTextPrefixPinBehaviorUnchanged() {
        val svg = toSvg("""prefix \(a + b\)""").single() as SvgGElement
        val prefixTextEl = svg.children()[0] as SvgTextElement
        val prefixTspan  = prefixTextEl.children()[0] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("prefix ")
        assertThat(prefixTspan.textAnchor().get()).isEqualTo("end")
        assertThat(prefixTspan.x().get()).isEqualTo(formulaLeftEdge)
    }

    @Test
    fun pureFormulaLineHasNoPrefixPin() {
        val svg = toSvg("""\(a + b\)""").single()
        assertThat(svg.tspans()).isEmpty()
    }

    @Test
    fun linkPrefixBeforeVectorFormulaPinsRightEdge() {
        // Empty parsed text is dropped, so the chunk anchor moves to the first tspan inside <a>.
        val svg = toSvg("""<a href="https://example.com">GitHub</a> & \(a + b\)""").single()
        assertThat(svg).isInstanceOf(SvgGElement::class.java)
        val outer = svg as SvgGElement
        assertThat(outer.children()).hasSize(2)
        val prefixTextEl = outer.children()[0] as SvgTextElement
        assertThat(prefixTextEl.children()).hasSize(2)
        val aEl         = prefixTextEl.children()[0] as SvgAElement
        val githubTspan = aEl.children().single() as SvgTSpanElement
        val andTspan    = prefixTextEl.children()[1] as SvgTSpanElement
        val formulaLeftEdge = toTestWidth("GitHub") + toTestWidth(" & ")
        assertThat(githubTspan.textAnchor().get()).isEqualTo("end")
        assertThat(githubTspan.x().get()).isEqualTo(formulaLeftEdge)
        assertThat(andTspan.textAnchor().get()).isEqualTo("end")
        assertThat(andTspan.x().get()).isNull()
        val formulaTransform = (outer.children()[1] as SvgGElement).transform().get()
        assertThat(formulaTransform).isNotNull
        assertThat(formulaTransform.toString()).startsWith("translate(")
    }

    @Test
    fun markdownBoldPrefixBeforeVectorFormulaPinsRightEdge() {
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

    @Test
    fun unsupportedGlyphInMixedRunSplicesOneFallbackText() {
        val svg = toSvg("""\(Č + b\)""").single() as SvgGElement
        assertThat(svg.pathElements()).hasSize(2)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("Č")
    }

    @Test
    fun mixedRunWidthIsSupportedAdvancesPlusEstimatedUnsupportedRun() {
        val measured = RichText.measure(text = """\(Č + b\)""", font = font)
        val expected = toTestWidth("Č") +
                (LatexVectorFont.advanceEm('+') + LatexVectorFont.advanceEm('b')) * font.size
        assertThat(measured.width).isCloseTo(expected, offset(1e-9))
    }

    @Test
    fun fallbackTextIsPositionedAfterPrecedingSupportedBoxes() {
        val svg = toSvg("""\(bČ\)""").single() as SvgGElement
        assertThat(svg.pathElements()).hasSize(1)
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("Č")
        assertThat(fallback.x().get()).isCloseTo(LatexVectorFont.advanceEm('b') * font.size, offset(1e-9))
    }

    @Test
    fun fallbackTextInSuperscriptCarriesReducedFontSize() {
        val svg = toSvg("""\(b^{Č}\)""").single() as SvgGElement
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("Č")
        assertThat(fallback.getAttribute(SvgTextContent.FONT_SIZE).get()).isEqualTo("11px")
    }

    @Test
    fun pureUnsupportedRunIsOneFallbackTextWithNoPaths() {
        val svg = toSvg("""\(ČŠ\)""").single() as SvgGElement
        assertThat(svg.pathElements()).isEmpty()
        val fallback = svg.vectorTextElements().single()
        assertThat(fallback.tspans().single().wholeText()).isEqualTo("ČŠ")
        val measured = RichText.measure(text = """\(ČŠ\)""", font = font)
        assertThat(measured.width).isCloseTo(toTestWidth("ČŠ"), offset(1e-9))
    }

    @Test
    fun boldFormulaUsesDistinctGlyphPaths() {
        val regularPaths = glyphPathData("""\(x + 1\)""", font)
        val boldPaths = glyphPathData("""\(x + 1\)""", Font(font.family, font.size, isBold = true))
        assertThat(boldPaths).hasSameSizeAs(regularPaths)
        assertThat(boldPaths).isNotEqualTo(regularPaths)
    }

    @Test
    fun italicFormulaUsesDistinctGlyphPaths() {
        val regularPaths = glyphPathData("""\(x + 1\)""", font)
        val italicPaths = glyphPathData("""\(x + 1\)""", Font(font.family, font.size, isItalic = true))
        assertThat(italicPaths).hasSameSizeAs(regularPaths)
        assertThat(italicPaths).isNotEqualTo(regularPaths)
    }

    @Test
    fun boldItalicFormulaUsesDistinctGlyphPaths() {
        val regularPaths = glyphPathData("""\(x + 1\)""", font)
        val boldPaths = glyphPathData("""\(x + 1\)""", Font(font.family, font.size, isBold = true))
        val italicPaths = glyphPathData("""\(x + 1\)""", Font(font.family, font.size, isItalic = true))
        val boldItalicPaths = glyphPathData("""\(x + 1\)""", Font(font.family, font.size, isBold = true, isItalic = true))
        assertThat(boldItalicPaths).hasSameSizeAs(regularPaths)
        assertThat(boldItalicPaths).isNotEqualTo(regularPaths)
        assertThat(boldItalicPaths).isNotEqualTo(boldPaths)
        assertThat(boldItalicPaths).isNotEqualTo(italicPaths)
    }

    @Test
    fun boldFormulaIsWiderThanRegular() {
        val regularWidth = RichText.measure(text = """\(x + 1\)""", font = font).width
        val boldWidth = RichText.measure(text = """\(x + 1\)""", font = Font(font.family, font.size, isBold = true)).width
        assertThat(boldWidth).isGreaterThan(regularWidth)
    }

    @Test
    fun greekLetterIsBoldWhenLabelIsBold() {
        val regularPaths = glyphPathData("""\(\Omega\)""", font)
        val boldPaths = glyphPathData("""\(\Omega\)""", Font(font.family, font.size, isBold = true))
        assertThat(boldPaths).hasSize(1)
        assertThat(boldPaths).isNotEqualTo(regularPaths)
    }

    private fun glyphPathData(formula: String, font: Font): List<String> {
        return toSvg(formula, font = font).single().pathElements()
            .map { it.getAttribute("d").get().toString() }
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
        private const val FRACTION_SIDE_SPACING_EM = 3.0 / 18.0
    }
}
