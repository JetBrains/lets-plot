/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.assertTSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.TestUtil.tspans
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import kotlin.test.Test

class RichTextMarkdownTest {
    @Test
    fun noMarkdown() {
        val richTextSvg = RichText.toSvg("Hello, world!", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(1)

        assertTSpan(
            richTextSvg.tspans().single(),
            "Hello, world!"
        )
    }

    @Test
    fun simpleStrong() {
        val richTextSvg = RichText.toSvg("**Hello, world!**", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(1)

        assertTSpan(
            richTextSvg.tspans().single(),
            "Hello, world!",
            bold = true
        )
    }

    @Test
    fun emphasisAndStrong() {
        val richTextSvg = RichText.toSvg("***Hello, world!***", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(1)

        assertTSpan(
            richTextSvg.tspans().single(),
            "Hello, world!",
            bold = true,
            italic = true
        )
    }

    @Test
    fun twoSpans() {
        val richTextSvg = RichText.toSvg("Hello, **world!**", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(2)

        val (hello, world) = richTextSvg.tspans()

        assertTSpan(hello, "Hello, ")
        assertTSpan(world, "world!", bold = true)
    }

    @Test
    fun emStrongAndColor() {
        val richTextSvg = RichText.toSvg("*Hello*, <span style=\"color:orange\">**orange**</span> and <span style=\"color:red\">***red***</span>!", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        val tspans = richTextSvg.tspans()

        assertThat(tspans).hasSize(6)

        assertTSpan(tspans[0], "Hello", italic = true)
        assertTSpan(tspans[1], ", ")
        assertTSpan(tspans[2], "orange", bold = true, color = "orange")
        assertTSpan(tspans[3], " and ")
        assertTSpan(tspans[4], "red", bold = true, italic = true, color = "red")
        assertTSpan(tspans[5], "!")
    }

    @Test
    fun stackOfStrong() {
        val richTextSvg = RichText.toSvg("****Foo** bar**", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(2)

        val (foo, bar) = richTextSvg.tspans()

        assertTSpan(foo, "Foo", bold = true)
        assertTSpan(bar, " bar", bold = true)
    }

    @Test
    fun stackOfColors() {
        val richTextSvg = RichText.toSvg("""Foo <span style="color:red">bar <span style="color:orange">baz</span> barbaz</span> spam""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(5)

        val (foo, bar, baz, barbaz, spam) = richTextSvg.tspans()

        assertTSpan(foo, "Foo ")
        assertTSpan(bar, "bar ", color = "red")
        assertTSpan(baz, "baz", color = "orange")
        assertTSpan(barbaz, " barbaz", color = "red")
        assertTSpan(spam, " spam")
    }

    @Test
    fun latex() {
        val richTextSvg = RichText.toSvg("""**foo** ***<span style="color:red">\\( bar^2 \\)</span>*** baz""", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true).single()

        assertThat(richTextSvg.tspans()).hasSize(8)

        val (foo, space, bar, pow, upper) = richTextSvg.tspans()
        val (square, lower, baz) = richTextSvg.tspans().drop(5)

        assertTSpan(foo, "foo", bold = true)
        assertTSpan(space, " ")
        assertTSpan(bar, "bar", bold = true, italic = true, color = "red")
        assertTSpan(pow, " ", bold = true, italic = true, color = "red")
        assertTSpan(upper, "\u200B", bold = true, italic = true, color = "red", sup = true) // upper baseline
        assertTSpan(square, "2", bold = true, italic = true, color = "red")
        assertTSpan(lower, "\u200B", bold = true, italic = true, color = "red", sub = true) // lower baseline
        assertTSpan(baz, " baz", sup = false)
    }

    @Test
    fun softBreak() {
        val richTextLines = RichText.toSvg("*Hello*,\n**world**", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true)

        assertThat(richTextLines).hasSize(1)

        val (hello, comma, softBreak, world) = richTextLines.single().tspans()

        assertTSpan(hello, "Hello", italic = true)
        assertTSpan(comma, ",")
        assertTSpan(softBreak, " ")
        assertTSpan(world, "world", bold = true)
    }

    @Test
    fun lineBreakWithTag() {
        val richTextLines = RichText.toSvg("*Hello*,<br/>**world**", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true)

        assertThat(richTextLines).hasSize(2)
        assertThat(richTextLines[0].tspans()).hasSize(2)
        assertThat(richTextLines[1].tspans()).hasSize(1)

        val (hello, comma) = richTextLines[0].tspans()
        val world = richTextLines[1].tspans().single()

        assertTSpan(hello, "Hello", italic = true)
        assertTSpan(comma, ",")
        assertTSpan(world, "world", bold = true)
    }

    @Test
    fun lineBreakWithSpaceSpaceNewLine() {
        val richTextLines = RichText.toSvg("*Hello*,  \n**world**", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true)

        assertThat(richTextLines).hasSize(2)
        assertThat(richTextLines[0].tspans()).hasSize(2)
        assertThat(richTextLines[1].tspans()).hasSize(1)

        val (hello, comma) = richTextLines[0].tspans()
        val (world) = richTextLines[1].tspans()


        assertTSpan(hello, "Hello", italic = true)
        assertTSpan(comma, ",")
        assertTSpan(world, "world", bold = true)
    }

    @Test
    fun spanStyleForMultilineText() {
        val richTextLines = RichText.toSvg("***<span style='color:red'>foo  \nbar  \nbaz</span>***", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true)

        assertThat(richTextLines).hasSize(3)
        assertThat(richTextLines[0].tspans()).hasSize(1)
        assertThat(richTextLines[1].tspans()).hasSize(1)
        assertThat(richTextLines[2].tspans()).hasSize(1)

        val (foo) = richTextLines[0].tspans()
        val (bar) = richTextLines[1].tspans()
        val (baz) = richTextLines[2].tspans()

        assertTSpan(foo, "foo", color = "red", bold = true, italic = true)
        assertTSpan(bar, "bar", color = "red", bold = true, italic = true)
        assertTSpan(baz, "baz", color = "red", bold = true, italic = true)
    }

    @Test
    fun spanWithHyperlink() {
        val richTextLines = RichText.toSvg("<span style=\"color:grey\">Powered by <a href=\"https://github.com/lets-plot\">lets-plot</a>  \nSource code</span>", DEF_FONT, TextWidthEstimator::widthCalculator, markdown = true)

        assertThat(richTextLines).hasSize(2)

        val poweredBy = richTextLines[0].children()[0] as SvgTSpanElement
        val hyperlink = richTextLines[0].children()[1] as SvgAElement
        val hyperlinkText = hyperlink.children()[0] as SvgTSpanElement

        assertTSpan(poweredBy, "Powered by ", color = "grey")
        assertTSpan(hyperlinkText, "lets-plot", color = null) // color is not inherited from the parent
        assertThat(hyperlinkText.hasClass(RichText.HYPERLINK_ELEMENT_CLASS)).isTrue()
        assertThat(hyperlink.href().get()).isEqualTo("https://github.com/lets-plot")
    }

    companion object {
        private val DEF_FONT = Font(family = FontFamily("sans-serif", false), size = 16, isBold = false, isItalic = false)
    }
}
