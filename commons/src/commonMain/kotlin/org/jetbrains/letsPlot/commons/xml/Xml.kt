/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml

object Xml {
    fun parse(xml: String): XmlNode {
        val lexer = Lexer(xml)
        val parser = Parser(lexer)
        return parser.parse()
    }

    // Parse XML and return the parsed node and the rest of the input if parsing was not complete
    fun parseSafe(xml: String): Pair<XmlNode, String> {
        val lexer = Lexer(xml)
        val parser = Parser(lexer)
        val doc = parser.parse()

        return when (Token.EOF) {
            lexer.nextToken() -> doc to ""
            else -> doc to lexer.input.substring(lexer.tokenPos)
        }
    }

    sealed class XmlNode {
        data class Element(
            val name: String,
            val attributes: Map<String, String> = emptyMap(),
            val children: List<XmlNode> = emptyList()
        ) : XmlNode()

        data class Text(val content: String) : XmlNode()
    }
}
