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
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.test.Test

class RichTextTermTest {
    @Test
    fun newLines() {
        val richTextSvg = toSvg("Hello\nworld!")

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf("world!")
        )
    }

    @Test
    fun endsWithNewLine() {
        val richTextSvg = toSvg("Hello\nworld!\n")

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf("world!"),
            listOf()
        )
    }

    @Test
    fun blankLineInMiddle() {
        val richTextSvg = toSvg("Hello\n\nworld!")

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("Hello"),
            listOf(),
            listOf("world!")
        )
    }

    @Test
    fun wholeLineLink() {
        val richTextSvg = toSvg("<a href=\"https://example.com\">link</a>\nnew line")

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("link"),
            listOf("new line")
        )
    }

    @Test
    fun linkInMiddleOfLine() {
        val richTextSvg = toSvg("A <a href=\"https://example.com\">link</a> with\nnew\nline")

        val textLines = richTextSvg.lineParts()

        assertThat(textLines).containsExactly(
            listOf("A ", "link", " with"),
            listOf("new"),
            listOf("line"),
        )
    }

    @Test
    fun multilineWithLink() {
        val richTextSvg = toSvg("Hello\nworld\nwith a <a href=\"https://example.com\">link</a>!\nhey")

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
        val richTextSvg = toSvg("Hello\nworld\nwith a <a href=\"https://example.com\">link</a> and <a href=\"https://example.com\">link2</a> !\nhey")

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
        val richTextSvg = toSvg("Hello, world!")
        assertThat(richTextSvg.single().stringParts()).containsExactly("Hello, world!")
    }

    @Test
    fun link() {
        val richTextSvg = toSvg("Hello, <a href=\"https://example.com\">world</a>!")
        assertThat(richTextSvg.single().stringParts()).containsExactly("Hello, ", "world", "!")
    }

    @Test
    fun consecutiveLinks() {
        val richTextSvg = toSvg("<a href=\"https://example.com\">A</a><a href=\"https://example.com\">B</a>")
        assertThat(richTextSvg.single().stringParts()).containsExactly("A", "B")
    }

    @Test
    fun emptyLink() {
        val richTextSvg = toSvg("<a href=\"https://example.com\"></a>")
        assertThat(richTextSvg.single().stringParts()).containsExactly("")
    }

    @Test
    fun emptyText() {
        val richTextSvg = toSvg("")
        assertThat(richTextSvg).isEmpty()
    }

    @Test
    fun shouldFitInOneLineAsLinkNotCountedByWrapper() {
        toSvg("Hello, <a href=\"https://example.com\">world</a>!", wrapLength = 20)
            .let {
                assertThat(it).hasSize(1)
                assertThat(it.single().stringParts()).containsExactly("Hello, ", "world", "!")
            }
    }

    @Test
    fun estimateWidth() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = estimateWidth("Hello, world!", arial)
        assertThat(width).isEqualTo(toTestWidth("Hello, world!", arial))
    }

    @Test
    fun estimateWidthWithWrap() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = estimateWidth("Hello, world!", arial, wrapLength = 6)
        assertThat(width).isEqualTo(toTestWidth(listOf("Hello,", " world", "!"), arial))
    }

    @Test
    fun estimateWithLink() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = estimateWidth("Hello, <a href=\"https://example.com\">world</a>!", arial)
        assertThat(width).isEqualTo(toTestWidth("Hello, world!", arial))
    }

    companion object {
        private val DEF_FONT = Font(family = FontFamily.SERIF, size = 16, isBold = false, isItalic = false)

        internal fun toSvg(
            text: String,
            wrapLength: Int = -1,
            markdown: Boolean = false,
            anchor: Text.HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
        ): List<SvgTextElement> {
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
            return RichText.estimateWidth(
                text = text,
                font = font,
                wrapLength = wrapLength,
                markdown = markdown
            )
        }

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
