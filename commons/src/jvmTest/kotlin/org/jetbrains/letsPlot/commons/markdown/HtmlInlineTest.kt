/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.Markdown.parse
import org.junit.Test
import kotlin.test.assertEquals

class HtmlInlineTest {
    @Test
    fun simple() {
        assertEquals(
            expected = p {
                text("<span style=\"color:blue\">some ")
                emph { text("blue") }
                text(" text</span>.")
            },
            actual = parse("<span style=\"color:blue\">some *blue* text</span>.")
        )
    }

    @Test
    fun baz() {
        val xml = """<text>   I see <br/> the world   </text>""".trimIndent()

        val parsed = Xml.parse(xml)
        println(parsed)
    }
}