/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator
import org.jetbrains.letsPlot.commons.values.Colors.parseColor
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.*

object TestUtil {
    private val DEF_FONT = Font(family = FontFamily.SERIF, size = 16, isBold = false, isItalic = false)

    internal fun toSvg(
        text: String,
        wrapLength: Int = -1,
        markdown: Boolean = false,
        anchor: Text.HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    ): List<SvgElement> {
        return RichText.toSvg(
            text = text,
            font = DEF_FONT,
            wrapLength = wrapLength,
            markdown = markdown,
            anchor = anchor
        )
    }

    internal fun estimateWidth(
        text: String,
        font: Font = DEF_FONT,
        wrapLength: Int = -1,
        markdown: Boolean = false,
    ): Double {
        return RichText.measure(
            text = text,
            font = font,
            wrapLength = wrapLength,
            markdown = markdown
        ).width
    }

    internal fun toTestWidth(text: String, baseFont: Font = DEF_FONT): Double {
        return TextMetricsEstimator.widthCalculator(text, baseFont)
    }

    internal fun toTestWidth(texts: Iterable<String>, baseFont: Font = DEF_FONT): Double {
        return texts.maxOf { toTestWidth(it, baseFont) }
    }

    fun SvgTSpanElement.wholeText(): String {
        return children()
            .map { it as SvgTextNode }
            .joinToString(separator = "") { it.textContent().get() }
    }

    fun SvgTextElement.tspans(): List<SvgTSpanElement> = children().map { it as SvgTSpanElement }

    // Convenience for tests that operate on a generic line element. For text-only lines this is the
    // same as SvgTextElement.tspans(). For mixed lines (vector formulas + text) it recursively
    // collects tspans from all descendant <text> children, in document order.
    fun SvgElement.tspans(): List<SvgTSpanElement> = when (this) {
        is SvgTextElement -> children().map { it as SvgTSpanElement }
        else -> children().flatMap { child ->
            when (child) {
                is SvgTextElement -> child.tspans()
                is SvgElement -> child.tspans()
                else -> emptyList()
            }
        }
    }

    // Find all path elements anywhere under this element (used by vector-formula tests).
    fun SvgElement.pathElements(): List<SvgPathElement> =
        children().flatMap { child ->
            when (child) {
                is SvgPathElement ->
                    if (child.classAttribute().get()
                            ?.split(' ')
                            ?.contains(org.jetbrains.letsPlot.core.plot.base.render.text.Latex.VECTOR_BBOX_CLASS) == true
                    ) emptyList() else listOf(child)
                is SvgElement -> child.pathElements()
                else -> emptyList()
            }
        }

    // Find all class-marked LaTeX fallback <text> elements (one per unsupported-glyph run) under
    // this element, in document order.
    fun SvgElement.vectorTextElements(): List<SvgTextElement> {
        val out = mutableListOf<SvgTextElement>()
        fun walk(e: SvgElement) {
            if (e is SvgTextElement &&
                e.classAttribute().get()?.split(' ')?.contains(org.jetbrains.letsPlot.core.plot.base.render.text.Latex.VECTOR_TEXT_CLASS) == true) {
                out.add(e)
            }
            e.children().forEach { c -> if (c is SvgElement) walk(c) }
        }
        walk(this)
        return out
    }

    // Find all vector-formula groups (SvgGElement with the marker class) under this element.
    fun SvgElement.vectorFormulaGroups(): List<SvgGElement> {
        val out = mutableListOf<SvgGElement>()
        fun walk(e: SvgElement) {
            if (e is SvgGElement &&
                e.classAttribute().get()?.split(' ')?.contains(org.jetbrains.letsPlot.core.plot.base.render.text.Latex.VECTOR_FORMULA_CLASS) == true) {
                out.add(e)
            }
            e.children().forEach { c -> if (c is SvgElement) walk(c) }
        }
        walk(this)
        return out
    }

    fun SvgTextElement.stringParts(): List<String> = children().flatMap { item ->
        when (item) {
            is SvgTextNode -> listOf(item.textContent().get())
            is SvgTSpanElement -> item.children().map { (it as SvgTextNode).textContent().get() }
            is SvgAElement -> item.children().map { aChild ->
                (aChild as SvgTSpanElement).children().single().let { tSpanChild ->
                    (tSpanChild as SvgTextNode).textContent().get()
                }
            }
            else -> error("Unexpected element type")
        }
    }

    // SvgElement-receiver variant — for tests that may receive either an SvgTextElement (legacy
    // tspan-only line) or an SvgGElement wrapper (mixed line with vector formulas). For groups,
    // collects parts from descendant <text> children in document order.
    fun SvgElement.stringParts(): List<String> = when (this) {
        is SvgTextElement -> stringParts()
        else -> children().flatMap { child ->
            when (child) {
                is SvgTextElement -> child.stringParts()
                is SvgElement -> child.stringParts()
                else -> emptyList()
            }
        }
    }

    fun Iterable<SvgElement>.lineParts(): List<List<String>> = map { it.stringParts() }


    fun assertTSpan(
        tspan: SvgTSpanElement,
        text: String,
        x: Double? = null,
        bold: Boolean = false,
        italic: Boolean = false,
        color: String? = null
    ) {
        assertThat(tspan.wholeText()).isEqualTo(text)

        if (x != null) {
            assertThat(tspan.x().get()).isEqualTo(x)
        } else {
            assertThat(tspan.x().get()).isNull()
        }

        if (bold) {
            assertThat(tspan.fontWeight().get()).isEqualTo("bold")
        } else {
            assertThat(tspan.fontWeight().get()).isNull()
        }

        if (italic) {
            assertThat(tspan.fontStyle().get()).isEqualTo("italic")
        } else {
            assertThat(tspan.fontStyle().get()).isNull()
        }

        if (color != null) {
            assertThat(tspan.fill().get()).isEqualTo(SvgColors.create(parseColor(color)))
        } else {
            assertThat(tspan.fill().get()).isNull()
        }
    }

}
