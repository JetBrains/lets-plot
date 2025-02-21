/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml

internal class Parser(
    val lexer: Lexer
) {
    fun parse(): Xml.XmlNode {
        return parseElement()
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

    private fun parseElement(): Xml.XmlNode {
        val pos = lexer.tokenPos
        try {
            consumeToken(TokenType.LT)
            val name = consumeToken(TokenType.TEXT).value

            skipSpaces()

            val attributes = parseAttributes()

            skipSpaces()

            when (consumeToken()) {
                Token.Companion.SLASH_GT -> return Xml.XmlNode.Element(name, attributes)
                Token.Companion.GT -> {}
                else -> error("Expected '>' or '/>'")
            }

            val children = parseChildren()

            consumeToken(TokenType.LT_SLASH)
            consumeToken(TokenType.TEXT, skipSpaces = true)
            consumeToken(TokenType.GT)

            return Xml.XmlNode.Element(name, attributes, children)
        } catch (e: Throwable) {
            return Xml.XmlNode.Text(lexer.input.substring(pos, lexer.tokenPos))
        }
    }

    private fun parseChildren(): MutableList<Xml.XmlNode> {
        val children = mutableListOf<Xml.XmlNode>()
        while (token() != Token.Companion.EOF) {
            children += when (token()) {
                Token.Companion.LT_SLASH, Token.Companion.SLASH_GT -> break
                Token.Companion.LT -> parseElement()
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

            check(token().type in listOf(TokenType.TEXT, TokenType.QUOTED_STRING))
            attributes[key] = token().value
            consumeToken(skipSpaces = true)
        }

        return attributes
    }

    // <tag>this is a content</tag>
    private fun parseContent(): Xml.XmlNode.Text {
        val buffer = StringBuilder()
        while (token() != Token.Companion.EOF) {
            when (token()) {
                Token.Companion.LT_SLASH, Token.Companion.LT, Token.Companion.EOF -> break
                else -> buffer.append(token().value)
            }
            consumeToken()
        }
        return Xml.XmlNode.Text(buffer.toString())
    }

    private fun token() = lexer.token
}