/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml

import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode
import org.junit.Test
import kotlin.test.assertEquals

class XmlTest {
    @Test
    fun simple() {
        val xml = """<p>Hi</p>""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                children = listOf(XmlNode.Text("Hi"))
            ),
            actual = parsed
        )
    }

    @Test
    fun `hello, World!`() {
        val xml = """<p>Hello, world!</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                children = listOf(XmlNode.Text("Hello, world!"))
            ),
            actual = parsed
        )
    }

    @Test
    fun contentWithTokens() {
        val xml = """<p>foo / = > bar</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                children = listOf(XmlNode.Text("foo / = > bar"))
            ),
            actual = parsed
        )
    }

    @Test
    fun selfClosing() {
        val xml = """<br/>""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element("br"),
            actual = parsed
        )
    }

    @Test
    fun selfClosingWithSpaces() {
        val xml = """<br  />""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element("br"),
            actual = parsed
        )
    }

    @Test
    fun selfClosingWithAttributeAndSpace() {
        val xml = """<img src="foobar.png"   />""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "img",
                attributes = mapOf("src" to "foobar.png")
            ),
            actual = parsed
        )
    }

    @Test
    fun anchor() {
        val xml = """<a href="https://example.com">Example</a>""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "a",
                attributes = mapOf("href" to "https://example.com"),
                children = listOf(XmlNode.Text("Example"))
            ),
            actual = parsed
        )
    }

    @Test
    fun theMoreThanBracketInAttributeValue() {
        val xml = """<formula expression="a > 0" />""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "formula",
                attributes = mapOf("expression" to "a > 0")
            ),
            actual = parsed
        )
    }

    @Test
    fun withSpanStyle() {
        val xml = """<p>Hello <span style="color:red">world</span></p>"""
        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                attributes = emptyMap(),
                children = listOf(
                    XmlNode.Text("Hello "),
                    XmlNode.Element(
                        name = "span",
                        attributes = mapOf("style" to "color:red"),
                        children = listOf(XmlNode.Text("world"))
                    )
                )
            ),
            actual = parsed
        )
    }

    @Test
    fun attrValueWithoutQuotes() {
        val xml = """<p style=color:red>Hello</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                attributes = mapOf("style" to "color:red"),
                children = listOf(XmlNode.Text("Hello"))
            ),
            actual = parsed
        )
    }

    @Test
    fun twoAttributes() {
        val xml = """<p style="color:red" class="foo">Hello</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                attributes = mapOf("style" to "color:red", "class" to "foo"),
                children = listOf(XmlNode.Text("Hello"))
            ),
            actual = parsed
        )
    }

    @Test
    fun extraSpacesEverywhere() {
        val xml = """<p    style  = "color:red"  >  Hello  </p >""".trimIndent()

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                attributes = mapOf("style" to "color:red"),
                children = listOf(XmlNode.Text("  Hello  "))
            ),
            actual = parsed
        )
    }

    @Test
    fun nested() {
        val xml = """<p>press <button>send<img src="send.png"/></button> button</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                children = listOf(
                    XmlNode.Text("press "),
                    XmlNode.Element(
                        name = "button",
                        children = listOf(
                            XmlNode.Text("send"),
                            XmlNode.Element(
                                name = "img",
                                attributes = mapOf("src" to "send.png"),
                            )
                        )
                    ),
                    XmlNode.Text(" button")
                )
            ),
            actual = parsed
        )
    }

    @Test
    fun malformed() {
        val xml = """<p> foo <b style="color:red"/> bar <p =foobar</p>"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Element(
                name = "p",
                children = listOf(
                    XmlNode.Text(" foo "),
                    XmlNode.Element(name = "b", attributes = mapOf("style" to "color:red")),
                    XmlNode.Text(" bar "),
                    XmlNode.Text("<p ="),
                    XmlNode.Text("foobar"),
                )
            ),
            actual = parsed
        )
    }

    @Test
    fun `malformed - not closed`() {
        val xml = """<br"""

        val parsed = parse(xml)
        assertEquals(
            expected = XmlNode.Text("<br"),
            actual = parsed
        )
    }

    internal fun parse(xml: String): XmlNode? {
        return Xml.parse(xml)
    }
}