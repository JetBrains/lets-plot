/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

// Can't use * in the test name - there is a problem on Windows with such names
class EmphasisWithUnderscoreTest {
    @Test
    fun `parse('')`() {
        assertEquals(
            expected = "",
            actual = Markdown.mdToHtml("")
        )
    }

    @Test
    fun `parse(_)`() {
        assertEquals(
            expected = "_",
            actual = Markdown.mdToHtml("_")
        )
    }

    @Test
    fun `parse(__)`() {
        assertEquals(
            expected = "__",
            actual = Markdown.mdToHtml("__")
        )
    }

    @Test
    fun `parse(foo)`() {
        assertEquals(
            expected = "foo",
            actual = Markdown.mdToHtml("foo")
        )
    }

    @Test
    fun `parse(foo, bar!)`() {
        assertEquals(
            expected = "foo, bar!",
            actual = Markdown.mdToHtml("foo, bar!")
        )
    }

    @Test
    fun `pase(_foo_)`() {
        assertEquals(
            expected = "<em>foo</em>",
            actual = Markdown.mdToHtml("_foo_")
        )
    }

    @Test
    fun `pase(__foo_)`() {
        assertEquals(
            expected = "_<em>foo</em>",
            actual = Markdown.mdToHtml("__foo_")
        )
    }

    @Test
    fun `parse(_foo___bar_____baz___)`(){
        assertEquals(
            expected = "<em>foo</em> <strong>bar</strong> <em><strong>baz</strong></em>",
            actual = Markdown.mdToHtml("_foo_ __bar__ ___baz___")
        )
    }

    @Test
    fun `parse(foo_bar_baz)`() {
        assertEquals(
            expected = "foo_bar_baz",
            actual = Markdown.mdToHtml("foo_bar_baz")
        )
    }

    @Test
    fun `parse(a _ foo bar_)`() {
        assertEquals(
            expected = "a _ foo bar_",
            actual = Markdown.mdToHtml("a _ foo bar_")
        )
    }

    @Test
    fun `parse(_ a _)`() {
        assertEquals(
            expected = "_ a _",
            actual = Markdown.mdToHtml("_ a _")
        )
    }

    @Test
    fun `parse(+_a_)`() {
        assertEquals(
            expected = "_a_",
            actual = Markdown.mdToHtml("\\_a_")
        )
    }

    @Test
    fun `parse(aa_'bb'_cc)`() {
        assertEquals(
            expected = """aa_"bb"_cc""",
            actual = Markdown.mdToHtml("""aa_"bb"_cc""")
        )
    }

    @Test
    fun `parse(aa_'bb'cc)`() {
        assertEquals(
            expected = """aa_"bb"cc""",
            actual = Markdown.mdToHtml("""aa_"bb"cc""")
        )
    }

    @Test
    fun `parse(5_6_78)`() {
        assertEquals(
            expected = "5_6_78",
            actual = Markdown.mdToHtml("5_6_78")
        )
    }

    @Test
    fun `parse(foo-_(bar)_)`() {
        assertEquals(
            expected = "foo-<em>(bar)</em>",
            //expected = p { text("foo-") emph { text("(bar)") } },
            actual = Markdown.mdToHtml("foo-_(bar)_")
        )
    }


}
