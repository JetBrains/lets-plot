/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt

object RichText {
    fun toSvg(
        text: String,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1
    ): List<SvgTextElement> {
        val lines = parseText(text, wrapLength, maxLinesCount)

        return lines.map { line ->
            SvgTextElement().apply {
                line.flatMap(Term::svg).forEach(::addTSpan)
            }
        }
    }

    fun estimateWidth(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        widthEstimator: (String, Font) -> Double,
    ): Double {
        return parseText(text, wrapLength, maxLinesCount)
            .maxOfOrNull { line -> line.sumOf { term -> term.estimateWidth(font, widthEstimator) } }
            ?: 0.0
    }

    private fun wrap(lines: List<List<Term>>, wrapLength: Int, maxLinesCount: Int): List<List<Term>> {
        val wrappedLines = lines.flatMap { line -> wrapLine(line, wrapLength) }
        return when {
            maxLinesCount < 0 -> wrappedLines
            wrappedLines.size < maxLinesCount -> wrappedLines
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) + mutableListOf(mutableListOf(TextTerm("...")))
        }
    }

    private fun parseText(text: String, wrapLength: Int = -1, maxLinesCount: Int = -1): List<List<Term>> {
        val lines = text.split("\n")
            .map { line ->
                val specialTerms = GreekLetterTerm.parse(line) + PowerTerm.toPowerTerms(line) + SubscriptTerm.toSubscriptTerms(line) + LinkTerm.parse(line)
                if (specialTerms.isEmpty()) {
                    listOf(TextTerm(line))
                } else {
                    val textTerms = subtractRange(line.indices, specialTerms.map { (_, termLocation) -> termLocation })
                        .map { pos -> TextTerm(line.substring(pos)) to pos }
                    (specialTerms + textTerms)
                        .sortedBy { (_, termLocation) -> termLocation.first }
                        .map { (term, _) -> term }
                }
            }

        val wrappedLines = wrap(lines, wrapLength, maxLinesCount)
        return wrappedLines
    }

    private fun wrapLine(line: List<Term>, wrapLength: Int = -1): List<List<Term>> {
        if (wrapLength <= 0) {
            return listOf(line)
        }

        val wrappedLines = mutableListOf(mutableListOf<Term>())
        line.forEach { term ->
            val availableSpace = wrapLength - wrappedLines.last().sumOf(Term::visualCharCount)
            when {
                term.visualCharCount <= availableSpace -> wrappedLines.last().add(term)
                term.visualCharCount <= wrapLength -> wrappedLines.add(mutableListOf(term)) // no need to split
                term !is TextTerm -> wrappedLines.add(mutableListOf(term)) // can't fit in one line, but can't split power or link
                else -> { // split text
                    wrappedLines.last().takeIf { availableSpace > 0 }?.add(TextTerm(term.text.take(availableSpace)))
                    wrappedLines += term.text
                        .drop(availableSpace)
                        .chunked(wrapLength)
                        .map { mutableListOf(TextTerm(it)) }
                }
            }
        }

        return wrappedLines
    }

    private fun subtractRange(range: IntRange, toSubtract: List<IntRange>): List<IntRange> {
        val sortedToSubtract = toSubtract.sortedBy(IntRange::first)
        val firstRange = IntRange(range.first, sortedToSubtract.first().first - 1)
        val intermediateRanges = sortedToSubtract.windowed(2).map { (prevRange, nextRange) ->
            IntRange(prevRange.last + 1, nextRange.first - 1)
        }
        val lastRange = IntRange(sortedToSubtract.last().last + 1, range.last)

        return (listOf(firstRange) + intermediateRanges + listOf(lastRange)).filterNot(IntRange::isEmpty)
    }

    private class TextTerm(
        val text: String,
    ) : Term {
        override val visualCharCount: Int = text.length
        override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(text))

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }
    }

    private class LinkTerm private constructor(
        private val text: String,
        private val href: String,
    ) : Term {
        override val visualCharCount: Int = text.length
        override val svg: List<SvgTSpanElement> = listOf(
            SvgTSpanElement(text).apply {
                fillColor().set(Colors.forName("blue")) // TODO: do not hardcode color
                setAttribute("lp-href", href)
            })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }

        companion object {
            private val anchorTagRegex = "<a\\s+[^>]*href=\"(?<href>[^\"]*)\"[^>]*>(?<text>[^<]*)</a>".toRegex()

            fun parse(text: String): List<Pair<Term, IntRange>> {
                return anchorTagRegex.findAll(text)
                    .map { match ->
                        val (href, label) = match.destructured
                        LinkTerm(label, href) to match.range
                    }.toList()
            }
        }
    }

    private class GreekLetterTerm(
        letter: String
    ) : Term {
        private val symbol = toSymbol(letter)
        override val visualCharCount: Int = 1
        override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(symbol))

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(symbol, font)
        }

        companion object {
            val LETTERS = mapOf(
                "Alpha" to "Α",
                "Beta" to "Β",
                "Gamma" to "Γ",
                "Delta" to "Δ",
                "Epsilon" to "Ε",
                "Zeta" to "Ζ",
                "Eta" to "Η",
                "Theta" to "Θ",
                "Iota" to "Ι",
                "Kappa" to "Κ",
                "Lambda" to "Λ",
                "Mu" to "Μ",
                "Nu" to "Ν",
                "Xi" to "Ξ",
                "Omicron" to "Ο",
                "Pi" to "Π",
                "Rho" to "Ρ",
                "Sigma" to "Σ",
                "Tau" to "Τ",
                "Upsilon" to "Υ",
                "Phi" to "Φ",
                "Chi" to "Χ",
                "Psi" to "Ψ",
                "Omega" to "Ω",
                "alpha" to "α",
                "beta" to "β",
                "gamma" to "γ",
                "delta" to "δ",
                "epsilon" to "ε",
                "zeta" to "ζ",
                "eta" to "η",
                "theta" to "θ",
                "iota" to "ι",
                "kappa" to "κ",
                "lambda" to "λ",
                "mu" to "μ",
                "nu" to "ν",
                "xi" to "ξ",
                "omicron" to "ο",
                "pi" to "π",
                "rho" to "ρ",
                "sigma" to "σ",
                "tau" to "τ",
                "upsilon" to "υ",
                "phi" to "φ",
                "chi" to "χ",
                "psi" to "ψ",
                "omega" to "ω",
            )
            private val REGEX = """\\\(\s*\\(?<letter>${LETTERS.keys.joinToString("|")})\s*\\\)""".toRegex()

            fun toSymbol(letter: String): String {
                return LETTERS[letter] ?: error("Unknown letter: $letter")
            }

            fun parse(text: String): List<Pair<Term, IntRange>> {
                return REGEX.findAll(text).map { match ->
                    val groups = match.groups as MatchNamedGroupCollection
                    GreekLetterTerm(groups["letter"]!!.value) to match.range
                }.toList()
            }
        }
    }

    private class PowerTerm(
        base: String,
        degree: String,
    ) : IndexTerm(base, degree, true) {
        companion object {
            fun toPowerTerms(text: String): List<Pair<Term, IntRange>> {
                return toTerms(text, "\\^") { base, degree -> PowerTerm(base, degree) }
            }
        }
    }

    private class SubscriptTerm(
        base: String,
        index: String,
    ) : IndexTerm(base, index, false) {
        companion object {
            fun toSubscriptTerms(text: String): List<Pair<Term, IntRange>> {
                return toTerms(text, "_") { base, index -> SubscriptTerm(base, index) }
            }
        }
    }

    private open class IndexTerm(
        private val base: String,
        private val index: String,
        isSuperior: Boolean,
    ) : Term {
        override val visualCharCount: Int = base.length + index.length
        override val svg: List<SvgTSpanElement>

        init {
            val shift = if (isSuperior) { "-" } else { "" }
            val backShift = if (isSuperior) { "" } else { "-" }
            val baseTSpan = SvgTSpanElement(base)
            val indentTSpan = SvgTSpanElement(INDENT_SYMBOL).apply {
                setAttribute(SvgTSpanElement.FONT_SIZE, "${INDENT_SIZE_FACTOR}em")
            }
            val indexTSpan = SvgTSpanElement(index).apply {
                setAttribute(SvgTSpanElement.FONT_SIZE, "${INDEX_SIZE_FACTOR}em")
                setAttribute(SvgTSpanElement.DY, "$shift${INDEX_RELATIVE_SHIFT}em")
            }
            // The following tspan element is used to restore the baseline after the index
            // Restoring works only if there is some symbol after the index, so we use ZERO_WIDTH_SPACE_SYMBOL
            // It could be considered as standard trick, see https://stackoverflow.com/a/65681504
            // Attribute 'baseline-shift' is better suited for such usecase -
            // it doesn't require to add an empty tspan at the end to restore the baseline (as 'dy').
            // Sadly we can't use 'baseline-shift' as it is not supported by CairoSVG.
            val restoreBaselineTSpan = SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
                // Size of shift depends on the font size, and it should be equal to the superscript shift size
                setAttribute(SvgTSpanElement.FONT_SIZE, "${INDEX_SIZE_FACTOR}em")
                setAttribute(SvgTSpanElement.DY, "$backShift${INDEX_RELATIVE_SHIFT}em")
            }

            svg = listOf(baseTSpan, indentTSpan, indexTSpan, restoreBaselineTSpan)
        }

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            val baseWidth = widthCalculator(base, font)
            val indexFontSize = (font.size * INDEX_SIZE_FACTOR).roundToInt()
            val indexFont = Font(font.family, indexFontSize, font.isBold, font.isItalic)
            val indexWidth = widthCalculator(index, indexFont)
            return baseWidth + indexWidth
        }

        companion object {
            private const val ZERO_WIDTH_SPACE_SYMBOL = "\u200B"
            private const val INDENT_SYMBOL = " "
            private const val INDENT_SIZE_FACTOR = 0.1
            private const val INDEX_SIZE_FACTOR = 0.7
            private const val INDEX_RELATIVE_SHIFT = 0.4

            fun toTerms(text: String, symbol: String, toTerm: (String, String) -> IndexTerm): List<Pair<Term, IntRange>> {
                return getRegex(symbol).findAll(text).map { match ->
                    val groups = match.groups as MatchNamedGroupCollection
                    toTerm(groups["base"]!!.value, groups["index"]!!.value) to match.range
                }.toList()
            }

            private fun getRegex(symbol: String): Regex {
                return """\\\(\s*(?<base>(?:-?[a-zA-Z0-9]+)*)$symbol(\{\s*)?(?<index>-?[a-zA-Z0-9]+)(\s*\})?\s*\\\)""".toRegex()
            }
        }
    }

    private interface Term {
        val visualCharCount: Int // in chars, used for line wrapping
        val svg: List<SvgTSpanElement>

        fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double
    }
}