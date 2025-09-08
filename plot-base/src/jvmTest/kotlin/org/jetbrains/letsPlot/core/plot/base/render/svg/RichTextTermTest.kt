/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.lineParts
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.stringParts
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.test.Test

class RichTextTermTest {
    @Test
    fun newLines() {
        val richTextSvg = RichText.toSvg("Hello\nworld!", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf("world!")
        )
    }

    @Test
    fun endsWithNewLine() {
        val richTextSvg = RichText.toSvg("Hello\nworld!\n", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf("world!"),
            listOf()
        )
    }

    @Test
    fun blankLineInMiddle() {
        val richTextSvg = RichText.toSvg("Hello\n\nworld!", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf(),
            listOf("world!")
        )
    }

    @Test
    fun wholeLineLink() {
        val richTextSvg = RichText.toSvg("<a href=\"https://example.com\">link</a>\nnew line", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("link"),
            listOf("new line")
        )
    }

    @Test
    fun linkInMiddleOfLine() {
        val richTextSvg = RichText.toSvg("A <a href=\"https://example.com\">link</a> with\nnew\nline", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("A ", "link", " with"),
            listOf("new"),
            listOf("line"),
        )
    }

    @Test
    fun multilineWithLink() {
        val richTextSvg = RichText.toSvg("Hello\nworld\nwith a <a href=\"https://example.com\">link</a>!\nhey", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf("world"),
            listOf("with a ", "link", "!"),
            listOf("hey")
        )
    }

    @Test
    fun multilineWithTwoLinks() {
        val richTextSvg = RichText.toSvg("Hello\nworld\nwith a <a href=\"https://example.com\">link</a> and <a href=\"https://example.com\">link2</a> !\nhey", DEF_FONT)

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf("world"),
            listOf("with a ", "link", " and ", "link2", " !"),
            listOf("hey")
        )
    }

    @Test
    fun singleTextLine() {
        val richTextSvg = RichText.toSvg("Hello, world!", DEF_FONT)
        assertThat(richTextSvg.single().stringParts()).containsExactly("Hello, world!")
    }

    @Test
    fun link() {
        val richTextSvg = RichText.toSvg("Hello, <a href=\"https://example.com\">world</a>!", DEF_FONT)
        assertThat(richTextSvg.single().stringParts()).containsExactly("Hello, ", "world", "!")
    }

    @Test
    fun consecutiveLinks() {
        val richTextSvg = RichText.toSvg("<a href=\"https://example.com\">A</a><a href=\"https://example.com\">B</a>", DEF_FONT)
        assertThat(richTextSvg.single().stringParts()).containsExactly("A", "B")
    }

    @Test
    fun emptyLink() {
        val richTextSvg = RichText.toSvg("<a href=\"https://example.com\"></a>", DEF_FONT)
        assertThat(richTextSvg.single().stringParts()).containsExactly("")
    }

    @Test
    fun emptyText() {
        val richTextSvg = RichText.toSvg("", DEF_FONT)
        assertThat(richTextSvg).isEmpty()
    }

    @Test
    fun shouldFitInOneLineAsLinkNotCountedByWrapper() {
        RichText
            .toSvg("Hello, <a href=\"https://example.com\">world</a>!", DEF_FONT, wrapLength = 20)
            .let {
                assertThat(it).hasSize(1)
                assertThat(it.single().stringParts()).containsExactly("Hello, ", "world", "!")
            }
    }

    @Test
    fun estimateWidth() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = RichText.estimateWidth("Hello, world!", arial)
        assertThat(width).isEqualTo(toTestWidth("Hello, world!", arial))
    }

    @Test
    fun estimateWidthWithWrap() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = RichText.estimateWidth("Hello, world!", arial, wrapLength = 6)
        assertThat(width).isEqualTo(toTestWidth(listOf("Hello,", " world", "!"), arial))
    }

    @Test
    fun estimateWithLink() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = RichText.estimateWidth("Hello, <a href=\"https://example.com\">world</a>!", arial)
        assertThat(width).isEqualTo(toTestWidth("Hello, world!", arial))
    }

    companion object {
        internal const val DEF_FONT_SIZE = 16
        internal val DEF_FONT = Font(family = FontFamily.SERIF, size = DEF_FONT_SIZE, isBold = false, isItalic = false)

        internal fun toTestWidth(text: String, baseFont: Font = DEF_FONT, level: TestUtil.FormulaLevel = TestUtil.FormulaLevel()): Double {
            val font = level.sizeValue()?.let { levelSizeScale ->
                val levelFontSize = max(1, (baseFont.size * levelSizeScale).roundToInt())
                Font(baseFont.family, levelFontSize, baseFont.isBold, baseFont.isItalic)
            } ?: baseFont
            return TextWidthEstimator.widthCalculator(text, font)
        }

        internal fun toTestWidth(texts: Iterable<String>, baseFont: Font = DEF_FONT, level: TestUtil.FormulaLevel = TestUtil.FormulaLevel()): Double {
            return texts.maxOf { toTestWidth(it, baseFont, level) }
        }
    }
}
