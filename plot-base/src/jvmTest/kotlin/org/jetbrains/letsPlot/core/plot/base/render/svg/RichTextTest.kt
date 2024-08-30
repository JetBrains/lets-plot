/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import kotlin.test.Test

class RichTextTest {
    @Test
    fun simple() {
        val richTextSvg = RichText().toSvg("Hello, world!")
        assertThat(extractText(richTextSvg)).containsExactly("Hello, world!")
    }

    @Test
    fun link() {
        val richTextSvg = RichText().toSvg("Hello, <a href=\"https://example.com\">world</a>!")
        assertThat(extractText(richTextSvg)).containsExactly("Hello, ", "world", "!")
    }

    @Test
    fun wrap() {
    }

    private fun extractText(svgText: SvgTextElement): List<String> {
        return svgText.children().flatMap { item ->
            when (item) {
                is SvgTextNode -> listOf(item.textContent().get())
                is SvgTSpanElement -> item.children().map { (it as SvgTextNode).textContent().get() }
                else -> error("Unexpected element type")
            }
        }
    }
}
