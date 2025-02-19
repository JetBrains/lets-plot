/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.Markdown.parse
import kotlin.test.Test
import kotlin.test.assertEquals

// Can't use * in the test name - there is a problem on Windows with such names
class MarkdownUnderscoreTest {
    @Test
    fun `parse('')`() {
        assertEquals(
            expected = emptyList(),
            actual = parse("")
        )
    }

    @Test
    fun `parse(_)`() {
        assertEquals(
            expected = p { text("_") },
            actual = parse("_")
        )
    }

    @Test
    fun `parse(__)`() {
        assertEquals(
            expected = p { text("__") },
            actual = parse("__")
        )
    }

    @Test
    fun `parse(foo)`() {
        assertEquals(
            expected = p { text("foo") },
            actual = parse("foo")
        )
    }

    @Test
    fun `parse(foo, bar!)`() {
        assertEquals(
            expected = p { text("foo, bar!") },
            actual = parse("foo, bar!")
        )
    }

    @Test
    fun `pase(_foo_)`() {
        assertEquals(
            expected = p { emph { text("foo") } },
            actual = parse("_foo_")
        )
    }

    @Test
    fun `pase(__foo_)`() {
        assertEquals(
            expected = p {
                text("_")
                emph { text("foo") }
            },
            actual = parse("__foo_")
        )
    }

    @Test
    fun `parse(_foo___bar_____baz___)`(){
        assertEquals(
            expected = p {
                emph { text ("foo") }
                text(" ")
                strong { text("bar") }
                text(" ")
                emph { strong { text("baz") } }
            },
            actual = parse("_foo_ __bar__ ___baz___")
        )
    }

    @Test
    fun `parse(foo_bar_baz)`() {
        assertEquals(
            expected = p { text("foo_bar_baz") },
            actual = parse("foo_bar_baz")
        )
    }

    @Test
    fun `parse(a _ foo bar_)`() {
        assertEquals(
            expected = p { text("a _ foo bar_") },
            actual = parse("a _ foo bar_")
        )
    }

    @Test
    fun `parse(_ a _)`() {
        assertEquals(
            expected = p { text("_ a _") },
            actual = parse("_ a _")
        )
    }

    @Test
    fun `parse(+_a_)`() {
        assertEquals(
            expected = p { text("_a_") },
            actual = parse("\\_a_")
        )
    }

    @Test
    fun `parse(aa_'bb'_cc)`() {
        assertEquals(
            expected = p { text("aa_\"bb\"_cc") },
            actual = parse("""aa_"bb"_cc""")
        )
    }

    @Test
    fun `parse(aa_'bb'cc)`() {
        assertEquals(
            expected = p { text("aa_\"bb\"cc") },
            actual = parse("""aa_"bb"cc""")
        )
    }

    @Test
    fun `parse(5_6_78)`() {
        assertEquals(
            expected = p { text("5_6_78") },
            actual = parse("5_6_78")
        )
    }

    @Test
    fun `parse(foo-_(bar)_)`() {
        assertEquals(
            expected = p {
                text("foo-")
                emph {
                    text("(bar)")
                }
            },
            actual = parse("foo-_(bar)_")
        )
    }


}
