/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.Markdown.parse
import org.jetbrains.letsPlot.commons.markdown.Node.Text
import kotlin.test.Test
import kotlin.test.assertEquals

// Can't use * in the test name - there is a problem on Windows with such names
class MarkdownAsteriskTest {
    @Test
    fun `parse('')`() {
        assertEquals(
            expected = emptyList(),
            actual = parse("")
        )
    }

    @Test
    fun `parse(backslash)`() {
        assertEquals(
            expected = p { text("\\") },
            actual = parse("\\")
        )
    }

    @Test
    fun `parse(backslashg)`() {
        assertEquals(
            expected = p { text("\\g") },
            actual = parse("\\g")
        )
    }

    @Test
    fun `parse(_)`() {
        assertEquals(
            expected = p { text("*") },
            actual = parse("*")
        )
    }

    @Test
    fun `parse(__)`() {
        assertEquals(
            expected = p { text("**") },
            actual = parse("**")
        )
    }

    @Test
    fun `parse( _ )`() {
        assertEquals(
            expected = p { text(" ** ") },
            actual = parse(" ** ")
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
            expected = p {
                emph {
                    text("foo")
                }
            },
            actual = parse("*foo*")
        )
    }

    @Test
    fun `pase(__foo_)`() {
        assertEquals(
            expected = p {
                text("*")
                emph {
                    text("foo")
                }
            },
            actual = parse("**foo*")
        )
    }

    @Test
    fun `parse(___baz___)`() {
        assertEquals(
            expected = p {
                emph {
                    strong {
                        text("baz")
                    }
                }
            },
            actual = parse("***baz***")
        )
    }

    @Test
    fun `parse(_foo___bar_____baz___)`() {
        assertEquals(
            expected = p {
                emph { text("foo") }
                text(" ")
                strong { text("bar") }
                text(" ")
                emph { strong { text("baz") } }
            },
            actual = parse("*foo* **bar** ***baz***")
        )
    }

    @Test
    fun `parse(foo_bar_baz)`() {
        assertEquals(
            expected = p {
                text("foo")
                emph {
                    text("bar")
                }
                text("baz")
            }
            ,
            actual = parse("foo*bar*baz")
        )
    }

    @Test
    fun `parse(a _ foo bar_)`() {
        assertEquals(
            expected = p { text("a * foo bar*") },
            actual = parse("a * foo bar*")
        )
    }

    @Test
    fun `parse(_ a _)`() {
        assertEquals(
            expected = listOf(Text("* a *")),
            actual = parse("* a *")
        )
    }

    @Test
    fun `parse(+_a_)`() {
        assertEquals(
            expected = p { text("*a*") },
            actual = parse("\\*a*")
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
            actual = parse("foo-*(bar)*")
        )
    }

    @Test
    fun `parse(___foo__ bar_)`() {
        assertEquals(
            expected = p {
                emph {
                    strong {
                        text("foo")
                    }
                    text(" bar")
                }
            },
            actual = parse("***foo** bar*")
        )
    }

    @Test
    fun `parse(_bar __foo___)`() {
        assertEquals(
            expected = p {
                emph {
                    text("bar ")
                    strong {
                        text("foo")
                    }
                }
            },
            actual = parse("*bar **foo***")
        )
    }

    @Test
    fun `parse(_____Hello_world____)`() {
        assertEquals(
            expected = p {
                text("**")
                emph {
                    strong {
                        text("Hello")
                        emph {
                            text("world")
                        }
                    }
                }
            },
            actual = parse("*****Hello*world****")
        )
    }
}

fun p(block: MutableList<Node>.() -> Unit): List<Node> {
    val nodes = mutableListOf<Node>()
    nodes.apply(block)
    return nodes
}

fun MutableList<Node>.emph(block: MutableList<Node>.() -> Unit) {
    add(Node.Em)
    apply(block)
    add(Node.CloseEm)
}

fun MutableList<Node>.strong(block: MutableList<Node>.() -> Unit) {
    add(Node.Strong)
    apply(block)
    add(Node.CloseStrong)
}

fun MutableList<Node>.text(text: String) {
    add(Text(text))
}
