/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml


object Xml {
    data class ParsingResult(
        val root: XmlNode,
        val nodeMap: Map<XmlNode, IntRange>,
        val unparsedRemainder: String
    )

    fun parse(xml: String): ParsingResult {
        val lexer = Lexer(xml)
        val parser = Parser(lexer)
        return parser.parse()
    }

    // Parse XML and return the parsed node and the rest of the input if parsing was not complete
    fun parseSafe(xml: String): ParsingResult {
        val lexer = Lexer(xml)
        val parser = Parser(lexer)
        return parser.parse()
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
