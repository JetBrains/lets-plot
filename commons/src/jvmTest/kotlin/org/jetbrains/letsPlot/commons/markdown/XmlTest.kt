/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import kotlin.test.Test
import kotlin.test.assertEquals

class XmlTest {

    @Test
    fun `hello, World!`() {
        val xml = """<p>Hello, world!</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "p",
                attributes = emptyMap(),
                children = listOf(Xml.XmlNode.Text("Hello, world!"))
            ),
            actual = parsed
        )
    }

    @Test
    fun simple() {
        val xml = """<p>Hi</p>""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "p",
                attributes = emptyMap(),
                children = listOf(Xml.XmlNode.Text("Hi"))
            ),
            actual = parsed
        )
    }

    @Test
    fun selfClosing() {
        val xml = """<br/>""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "br",
                attributes = emptyMap(),
                children = emptyList()
            ),
            actual = parsed
        )
    }

    @Test
    fun selfClosingWithAttributeAndSpace() {
        val xml = """<img src="foobar.png"   />""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "img",
                attributes = mapOf("src" to "foobar.png"),
                children = emptyList()
            ),
            actual = parsed
        )
    }

    @Test
    fun anchor() {
        val xml = """<a href="https://example.com">Example</a>""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "a",
                attributes = mapOf("href" to "https://example.com"),
                children = listOf(Xml.XmlNode.Text("Example"))
            ),
            actual = parsed
        )
    }

    @Test
    fun theMoreThanBracketInAttributeValue() {
        val xml = """<formula expression="a > 0" />""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "formula",
                attributes = mapOf("expression" to "a > 0"),
                children = emptyList()
            ),
            actual = parsed
        )
    }

    @Test
    fun withSpanStyle() {
        val xml = """<p>Hello <span style="color:red">world</span></p>"""
        val parsed = parse(xml)
        assertEquals(
            expected = Xml.XmlNode.Element(
                name = "p",
                attributes = emptyMap(),
                children = listOf(
                    Xml.XmlNode.Text("Hello "),
                    Xml.XmlNode.Element(
                        name = "span",
                        attributes = mapOf("style" to "color:red"),
                        children = listOf(Xml.XmlNode.Text("world"))
                    )
                )
            ),
            actual = parsed
        )
    }

    internal fun parse(xml: String): Xml.XmlNode? {
        return if (false) {
            Xml.parse(xml)
        } else {
             Xml.Parser(Xml.Lexer(xml)).parse()
        }
    }
}
