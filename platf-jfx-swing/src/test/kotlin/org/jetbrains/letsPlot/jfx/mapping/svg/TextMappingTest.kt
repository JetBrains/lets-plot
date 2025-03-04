/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.mapping.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextNode
import org.junit.Test

class TextMappingTest {
    @Test
    fun simple() {
        val helloSvg = SvgTextElement().apply {
            children().add(
                SvgTextNode("Hello")
            )
        }

        val textLine = render(helloSvg)

        assertThat(textLine.content).containsOnly(TextLine.TextRun("Hello"))
    }

    @Test
    fun withStyle() {

    }

    internal fun render(textSvg: SvgTextElement): TextLine {
        val peer = SvgJfxPeer()
        val textLine = TextLine()
        val mapper = SvgTextElementMapper(textSvg, textLine, peer)

        val ctx = MappingContext()
        mapper.attach(ctx)

        return textLine
    }
}
