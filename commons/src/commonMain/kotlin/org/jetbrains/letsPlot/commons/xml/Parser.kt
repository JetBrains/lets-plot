/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml

import org.jetbrains.letsPlot.commons.xml.Xml.XmlNode

internal class Parser(
    val lexer: Lexer
) {
    private val nodeLocations = mutableMapOf<XmlNode, IntRange>()
    var errorPosition: Int? = null
        private set

    fun parse(): Xml.ParsingResult {
        nodeLocations.clear()
        errorPosition = null

        val element = parseElement()
        
        return Xml.ParsingResult(
            root = element,
            nodeLocations = nodeLocations,
            errorPos = errorPosition
        )
    }

    private fun skipSpaces() {
        while (token().type == TokenType.WHITESPACE) {
            lexer.nextToken()
        }
    }

    private fun consumeToken(expectedType: TokenType? = null, skipSpaces: Boolean = false): Token {
        val consumed = token().also { lexer.nextToken() }
        if (expectedType != null) check(consumed.type == expectedType) { "Expected $expectedType, got $consumed" }
        if (skipSpaces) skipSpaces()
        return consumed
    }

    private fun parseElement(): XmlNode {
        val pos = lexer.tokenPos
        try {
            consumeToken(TokenType.LT)
            val name = consumeToken(TokenType.TEXT).value

            skipSpaces()

            val attributes = parseAttributes()

            skipSpaces()

            when (consumeToken()) {
                Token.SLASH_GT -> return createElement(name, attributes, emptyList(), pos)
                Token.GT -> {}
                else -> error("Expected '>' or '/>'")
            }

            val children = parseChildren()

            consumeToken(TokenType.LT_SLASH)
            consumeToken(TokenType.TEXT, skipSpaces = true)
            consumeToken(TokenType.GT)

            return createElement(name, attributes, children, pos)
        } catch (_: Throwable) {
            return if (lexer.token == Token.EOF) {
                errorPosition = pos
                val text = XmlNode.Text(lexer.input.substring(pos))
                nodeLocations[text] = IntRange(pos, lexer.input.length)
                text
            } else {
                errorPosition = pos
                val text = XmlNode.Text(lexer.input.substring(pos, lexer.tokenPos))
                nodeLocations[text] = IntRange(pos, lexer.tokenPos)
                text
            }
        }
    }

    private fun parseChildren(): MutableList<XmlNode> {
        val children = mutableListOf<XmlNode>()
        while (token() != Token.EOF) {
            children += when (token()) {
                Token.LT_SLASH, Token.SLASH_GT -> break
                Token.LT -> parseElement()
                else -> parseContent()
            }
        }
        return children
    }

    private fun parseAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()
        while (token().type == TokenType.TEXT) {
            val key = token().value
            consumeToken(skipSpaces = true)

            consumeToken(TokenType.EQUALS, skipSpaces = true)

            check(token().type in listOf(TokenType.TEXT, TokenType.SINGLE_QUOTED_STRING, TokenType.DOUBLE_QUOTED_STRING)) {}
            attributes[key] = token().value
            consumeToken(skipSpaces = true)
        }

        return attributes
    }

    // <tag>this is a content</tag>
    private fun parseContent(): XmlNode.Text {
        val startPos = lexer.tokenPos
        val buffer = StringBuilder()
        while (token() != Token.EOF) {
            when (token().type) {
                TokenType.LT_SLASH, TokenType.LT, TokenType.EOF -> break
                TokenType.SINGLE_QUOTED_STRING -> buffer.append("'${token().value}'")
                TokenType.DOUBLE_QUOTED_STRING -> buffer.append("\"${token().value}\"")

                else -> buffer.append(token().value)
            }
            consumeToken()
        }
        return createText(buffer.toString(), startPos)
    }

    private fun token() = lexer.token

    private fun createElement(name: String, attributes: Map<String, String>, children: List<XmlNode>, startPos: Int): XmlNode.Element {
        val element = XmlNode.Element(name, attributes, children)
        nodeLocations[element] = IntRange(startPos, lexer.tokenPos - 1)
        return element
    }

    private fun createText(content: String, startPos: Int): XmlNode.Text {
        val text = XmlNode.Text(content)
        nodeLocations[text] = IntRange(startPos, lexer.tokenPos - 1)
        return text
    }

}
