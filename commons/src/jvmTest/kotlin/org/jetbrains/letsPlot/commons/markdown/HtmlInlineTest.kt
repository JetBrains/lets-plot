/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.junit.Test
import kotlin.test.assertEquals

class HtmlInlineTest {
    @Test
    fun simple() {
        assertEquals(
            expected = """some <span style="color:blue">blue</span> text""",
            actual = Markdown.mdToHtml("""some <span style="color:blue">blue</span> text""")
        )
    }

    @Test
    fun htmlWithEmphasis() {
        assertEquals(
            expected = """some <span style="color:blue"><em>blue</em></span> text""",
            actual = Markdown.mdToHtml("""some <span style="color:blue">*blue*</span> text""")
        )
    }

    @Test
    fun nestedHtml() {
        assertEquals(
            expected = """<span style="color:blue">blue text with <span style="color:red"><strong>red</strong></span> inside</span>""",
            actual = Markdown.mdToHtml("""<span style="color:blue">blue text with <span style="color:red">**red**</span> inside</span>""")
        )
    }
}
