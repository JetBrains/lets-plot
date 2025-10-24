/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.estimateWidth
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.lineParts
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.stringParts
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.toSvg
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.toTestWidth
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import kotlin.test.Test

class PlaintextTest {
    @Test
    fun `unsupported tag should be preserved as plain text`() {
        val text = """Hello, <b>cruel</b> world!"""
        val richTextSvg = toSvg(text).single()
        assertThat(richTextSvg.stringParts()).containsExactly(text)
    }

    @Test
    fun `unsupported tag and hyperlink outside`() {
        val text = """Hello, <b>cruel</b> world from <a href="https://lets-plot.org">lets-plot</a>!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly("Hello, <b>cruel</b> world from ", "lets-plot", "!")
    }

    @Test
    fun `unsupported tag with hyperlink and text inside`() {
        val text = """Hello, <b>foo <a href="https://lets-plot.org">lets-plot</a> bar</b>!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly("Hello, <b>foo ", "lets-plot", " bar</b>!")
    }

    @Test
    fun `unsupported tag with hyperlink only`() {
        val text = """Hello, <b><a href="https://lets-plot.org">lets-plot</a></b>!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly("Hello, <b>", "lets-plot", "</b>!")
    }

    @Test
    fun `unsupported tag with hyperlink and left child`() {
        val text = """Hello, <b>foo <a href="https://lets-plot.org">lets-plot</a></b>!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly("Hello, <b>foo ", "lets-plot", "</b>!")
    }

    @Test
    fun `unsupported tag with hyperlink and right child`() {
        val text = """Hello, <b><a href="https://lets-plot.org">lets-plot</a> foo</b>!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly("Hello, <b>", "lets-plot", " foo</b>!")
    }

    @Test
    fun `unsupported tag without children`() {
        val text = """Hello, <b></b>!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly("Hello, <b></b>!")
    }

    @Test
    fun `lower than and greater than signs`() {
        val richTextSvg = toSvg("5 < 10 and 10 > 5").single()

        assertThat(richTextSvg.tspans()).hasSize(1)
        assertTSpan(richTextSvg.tspans().single(), "5 < 10 and 10 > 5")
    }

    @Test
    fun `lower than with hyperlink`() {
        val richTextSvg = toSvg("5 < 10 visit <a href=\"https://lets-plot.org\">lets-plot</a> now").single()

        assertThat(richTextSvg.stringParts()).containsExactly("5 < 10 visit ", "lets-plot", " now")
    }

    @Test
    fun `hyperlink with lower than `() {
        val richTextSvg = toSvg("<a href=\"https://lets-plot.org\">lets-plot</a> now 5 < 10 visit ").single()

        assertThat(richTextSvg.stringParts()).containsExactly("lets-plot", " now 5 < 10 visit ")
    }

    @Test
    fun `text with hyperlink and with lower than `() {
        val richTextSvg = toSvg("hello <a href=\"https://lets-plot.org\">lets-plot</a> now 5 < 10 visit ").single()

        assertThat(richTextSvg.stringParts()).containsExactly("hello ", "lets-plot", " now 5 < 10 visit ")
    }

    @Test
    fun `malformed hyperlink should be rendered as normal text`() {
        val richTextSvg = toSvg("Click <a href=''>here").single()

        assertTSpan(richTextSvg.tspans().single(), "Click <a href=''>here")
    }

    @Test
    fun `label with quotes`() {
        val text = """Hello, 'cruel' "world"!"""
        val richTextSvg = toSvg(text).single()

        assertThat(richTextSvg.stringParts()).containsExactly(text)
    }

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
    fun onlyLink() {
        val richTextSvg = toSvg("<a href=\"https://example.com\">world</a>")
        assertThat(richTextSvg.single().stringParts()).containsExactly("world")
    }

    @Test
    fun forHyperlinkDefaultTargetIsBlank() {
        val richTextSvg = toSvg("<a href=\"https://example.com\">world</a>")
        assertThat(richTextSvg.single().stringParts()).containsExactly("world")

        val a = richTextSvg.single().children().first() as SvgAElement
        assertThat(a.href().get()).isEqualTo("https://example.com")
        assertThat(a.target().get()).isEqualTo("_blank")
    }

    @Test
    fun userDefinedTarget() {
        val richTextSvg = toSvg("<a href=\"https://example.com\" target=\"_parent\">world</a>")
        assertThat(richTextSvg.single().stringParts()).containsExactly("world")

        val a = richTextSvg.single().children().first() as SvgAElement
        assertThat(a.href().get()).isEqualTo("https://example.com")
        assertThat(a.target().get()).isEqualTo("_parent")
    }

    // The Result is incorrect, but at least it works without exceptions.
    // Expected containsExactly("Hello, ", "wor", "ld", "!")
    @Test
    fun nestedLinks() {
        val richTextSvg = toSvg("Hello, <a href=\"https://example.com\">wor<a href=\"https://example.com\">ld</a></a>!")

        // Current incorrect result:
        assertThat(richTextSvg.single().stringParts()).containsExactly("Hello, ", "wor", "!")

        // Correct result (not implemented):
        //assertThat(richTextSvg.single().stringParts()).containsExactly("Hello, ", "wor", "ld", "!")
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
}
