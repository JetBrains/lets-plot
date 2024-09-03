/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import kotlin.test.Test

class RichTextTermTest {
    @Test
    fun simple() {
        val richTextSvg = RichText.toSvg("Hello, world!")
        assertThat(extractTextLine(richTextSvg.single())).containsExactly("Hello, world!")
    }

    @Test
    fun link() {
        val richTextSvg = RichText.toSvg("Hello, <a href=\"https://example.com\">world</a>!")
        assertThat(extractTextLine(richTextSvg.single())).containsExactly("Hello, ", "world", "!")
    }

    @Test
    fun consecutiveLinks() {
        val richTextSvg = RichText.toSvg("<a href=\"https://example.com\">A</a><a href=\"https://example.com\">B</a>")
        assertThat(extractTextLine(richTextSvg.single())).containsExactly("A", "B")
    }

    @Test
    fun emptyLink() {
        val richTextSvg = RichText.toSvg("<a href=\"https://example.com\"></a>")
        assertThat(extractTextLine(richTextSvg.single())).containsExactly("")
    }

    @Test
    fun emptyText() {
        val richTextSvg = RichText.toSvg("")
        assertThat(extractTextLine(richTextSvg.single())).containsExactly("")
    }

    @Test
    fun shouldFitInOneLineAsLinkNotCountedByWrapper() {
        RichText
            .toSvg("Hello, <a href=\"https://example.com\">world</a>!", wrapLength = 20)
            .let {
                assertThat(it).hasSize(1)
                assertThat(extractTextLine(it.single())).containsExactly("Hello, ", "world", "!")
            }
    }

    @Test
    fun estimateWidth() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = RichText.estimateWidth("Hello, world!", arial) { text, _ -> text.length * 5.5 }
        assertThat(width).isEqualTo(5.5 * "Hello, world!".length)
    }

    @Test
    fun estimateWidthWithWrap() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = RichText.estimateWidth("Hello, world!", arial, wrapLength = 6) { text, _ -> text.length * 5.5 }
        assertThat(width).isEqualTo(5.5 * 6)
    }

    @Test
    fun estimateWithLink() {
        val arial = Font(FontFamily("Arial", monospaced = false), 12)
        val width = RichText.estimateWidth("Hello, <a href=\"https://example.com\">world</a>!", arial) { text, _ -> text.length * 5.5 }
        assertThat(width).isEqualTo(5.5 * "Hello, world!".length)
    }

    private fun extractTextLine(svgTextLine: SvgTextElement): List<String> {
        return svgTextLine.children().flatMap { item ->
            when (item) {
                is SvgTextNode -> listOf(item.textContent().get())
                is SvgTSpanElement -> item.children().map { (it as SvgTextNode).textContent().get() }
                else -> error("Unexpected element type")
            }
        }
    }
}
