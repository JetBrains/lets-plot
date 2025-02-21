/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

// Can't use * in the test name - there is a problem on Windows with such names
class MarkdownAsteriskTest {
    @Test
    fun `parse('')`() {
        assertEquals(
            expected = "",
            actual = Markdown.mdToHtml("")
        )
    }

    @Test
    fun `parse(backslash)`() {
        assertEquals(
            expected = "\\",
            actual = Markdown.mdToHtml("\\")
        )
    }

    @Test
    fun `parse(backslashg)`() {
        assertEquals(
            expected = "\\g",
            actual = Markdown.mdToHtml("\\g")
        )
    }

    @Test
    fun `parse(_)`() {
        assertEquals(
            expected = "*",
            actual = Markdown.mdToHtml("*")
        )
    }

    @Test
    fun `parse(__)`() {
        assertEquals(
            expected = "**",
            actual = Markdown.mdToHtml("**")
        )
    }

    @Test
    fun `parse( _ )`() {
        assertEquals(
            expected = " ** ",
            actual = Markdown.mdToHtml(" ** ")
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
            actual = Markdown.mdToHtml("*foo*")
        )
    }

    @Test
    fun `pase(__foo_)`() {
        assertEquals(
            expected = "*<em>foo</em>",
            actual = Markdown.mdToHtml("**foo*")
        )
    }

    @Test
    fun `parse(___baz___)`() {
        assertEquals(
            expected = "<em><strong>baz</strong></em>",
            actual = Markdown.mdToHtml("***baz***")
        )
    }

    @Test
    fun `parse(_foo___bar_____baz___)`() {
        assertEquals(
            expected = "<em>foo</em> <strong>bar</strong> <em><strong>baz</strong></em>",
            actual = Markdown.mdToHtml("*foo* **bar** ***baz***")
        )
    }

    @Test
    fun `parse(foo_bar_baz)`() {
        assertEquals(
            expected = "foo<em>bar</em>baz",
            actual = Markdown.mdToHtml("foo*bar*baz")
        )
    }

    @Test
    fun `parse(a _ foo bar_)`() {
        assertEquals(
            expected = "a * foo bar*",
            actual = Markdown.mdToHtml("a * foo bar*")
        )
    }

    @Test
    fun `parse(_ a _)`() {
        assertEquals(
            expected = "* a *",
            actual = Markdown.mdToHtml("* a *")
        )
    }

    @Test
    fun `parse(+_a_)`() {
        assertEquals(
            expected = "*a*",
            actual = Markdown.mdToHtml("\\*a*")
        )
    }

    @Test
    fun `parse(foo-_(bar)_)`() {
        assertEquals(
            expected = "foo-<em>(bar)</em>",
            actual = Markdown.mdToHtml("foo-*(bar)*")
        )
    }

    @Test
    fun `parse(___foo__ bar_)`() {
        assertEquals(
            expected = "<em><strong>foo</strong> bar</em>",
            actual = Markdown.mdToHtml("***foo** bar*")
        )
    }

    @Test
    fun `parse(_bar __foo___)`() {
        assertEquals(
            expected = "<em>bar <strong>foo</strong></em>",
            actual = Markdown.mdToHtml("*bar **foo***")
        )
    }

    @Test
    fun `parse(_____Hello_world____)`() {
        assertEquals(
            expected = "**<em><strong>Hello<em>world</em></strong></em>",
            actual = Markdown.mdToHtml("*****Hello*world****")
        )
    }
}
