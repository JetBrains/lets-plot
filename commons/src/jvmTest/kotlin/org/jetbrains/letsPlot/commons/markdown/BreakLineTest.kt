/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.junit.Test
import kotlin.test.assertEquals

class BreakLineTest {

    @Test
    fun softBreak() {
        assertEquals(
            expected = "Hello,<softbreak/>world!",
            actual = Markdown.mdToHtml("Hello,\nworld!")
        )
    }

    @Test
    fun lineBreak() {
        assertEquals(
            expected = "Hello,<br/>world!",
            actual = Markdown.mdToHtml("Hello,  \nworld!")
        )
    }

    @Test
    fun softBreakWithEmptyLine() {
        assertEquals(
            expected = "<softbreak/>",
            actual = Markdown.mdToHtml("\n")
        )
    }

    @Test
    fun lineBreakWithEmptyLine() {
        assertEquals(
            expected = "<br/>",
            actual = Markdown.mdToHtml("  \n")
        )
    }
}