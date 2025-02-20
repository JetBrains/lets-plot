/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.Xml.XmlNode.Element
import org.jetbrains.letsPlot.commons.markdown.Xml.XmlNode.Text

class Xml {
    sealed class XmlNode {
        data class Element(
            val name: String,
            val attributes: Map<String, String> = emptyMap(),
            val children: List<XmlNode> = emptyList()
        ) : XmlNode()

        data class Text(val content: String) : XmlNode()
    }

    enum class TokenType {
        LT, LT_SLASH, GT, SLASH, SLASH_GT, EQUALS, EOF, WHITESPACE, QUOTED_STRING, TEXT
    }

    data class Token(
        val type: TokenType,
        val value: String
    ) {
        companion object {
            val LT = Token(TokenType.LT, "<")
            val LT_SLASH = Token(TokenType.LT_SLASH, "</")
            val GT = Token(TokenType.GT, ">")
            val SLASH = Token(TokenType.SLASH, "/")
            val SLASH_GT = Token(TokenType.SLASH_GT, "/>")
            val EQUALS = Token(TokenType.EQUALS, "=")
            val EOF = Token(TokenType.EOF, "")
        }
    }

    private class Lexer(
        val input: String
    ) {
        var token: Token = Token.EOF
            private set

        var tokenPos = 0
        private set

        private var pos = 0

        private fun peek(): Char? = input.getOrNull(pos)
        private fun advance(): Char? = input.getOrNull(pos++)

        init {
            nextToken()
        }

        fun nextToken(): Token {
            tokenPos = pos
            token = nextTokenImpl()
            return token
        }

        private fun nextTokenImpl(): Token {
            if (pos >= input.length) return Token.EOF

            return when (val c = peek()) {
                null -> Token.EOF
                '<' -> {
                    advance()
                    when {
                        peek() == '/' -> Token.LT_SLASH.also { advance() }
                        else -> Token.LT
                    }
                }

                '>' -> Token.GT.also { advance() }
                '/' -> {
                    advance()
                    when {
                        peek() == '>' -> Token.SLASH_GT.also { advance() }
                        else -> Token.SLASH
                    }
                }

                '=' -> Token.EQUALS.also { advance() }
                ' ', '\t' -> Token(TokenType.WHITESPACE, " ").also { advance() }
                '"' -> {
                    advance() // consume opening quote
                    val token = Token(TokenType.QUOTED_STRING, readUntil(listOf(eq('"'))))
                    advance() // consume closing quote

                    token
                }

                else -> when {
                    c.isWhitespace() -> Token(TokenType.WHITESPACE, c.toString())
                    else -> Token(
                        TokenType.TEXT,
                        readUntil(listOf(eq('<'), eq('/'), eq('>'), eq('"'), eq('='), Char::isWhitespace))
                    )
                }
            }
        }

        fun eq(char: Char) = char::equals

        private fun readUntil(predicate: List<(Char) -> Boolean>): String {
            val sb = StringBuilder()
            while (true) {
                val c = peek()
                if (c == null) break
                if (predicate.any { it(c) }) break

                sb.append(advance())
            }
            return sb.toString()
        }
    }

    private class Parser(
        val lexer: Lexer
    ) {
        fun parse(): XmlNode {
            return parseElement()
        }

        private fun skipSpaces() {
            while (lexer.token.type == TokenType.WHITESPACE) {
                lexer.nextToken()
            }
        }

        private fun consumeToken(): Token {
            return lexer.token.also { lexer.nextToken() }
        }

        private fun parseElement(): XmlNode {
            val pos = lexer.tokenPos
            try {
                check(consumeToken() == Token.LT) { "Expected '<'" }
                val name = parseElementName()
                val attributes = parseAttributes()

                if (parseSelfClosingElement()) {
                    return Element(name, attributes, emptyList())
                } else {
                    check(consumeToken() == Token.GT) { "Expected '>'" }
                }

                val children = parseChildren()

                check(consumeToken() == Token.LT_SLASH) { "Expected \"</\"" }
                check(consumeToken() == Token(TokenType.TEXT, name)) { "Expected element name" }
                skipSpaces()
                check(consumeToken() == Token.GT) { "Expected '>'" }

                return Element(name, attributes, children)
            } catch (e: Throwable) {
                return Text(lexer.input.substring(pos, lexer.tokenPos))
            }
        }

        private fun parseSelfClosingElement(): Boolean {
            skipSpaces()

            if (lexer.token != Token.SLASH_GT) {
                return false
            }

            consumeToken()
            return true
        }

        private fun parseChildren(): MutableList<XmlNode> {
            val children = mutableListOf<XmlNode>()
            while (lexer.token != Token.EOF) {
                val token = lexer.token
                children += when (token.type) {
                    TokenType.LT_SLASH, TokenType.SLASH_GT, TokenType.EOF -> break
                    TokenType.LT -> parseElement()
                    else -> parseContent()
                }
            }
            return children
        }

        private fun parseElementName(): String {
            val token = consumeToken()
            if (token.type != TokenType.TEXT) error("Expected element name, but got: $token")
            return token.value
        }

        private fun parseAttributes(): Map<String, String> {
            val attributes = mutableMapOf<String, String>()
            while (lexer.token !in listOf(Token.GT, Token.SLASH_GT, Token.EOF)) {
                val token = consumeToken()
                when (token.type) {
                    TokenType.WHITESPACE -> {}
                    TokenType.TEXT -> {
                        val key = token.value
                        skipSpaces()
                        check(consumeToken() == Token.EQUALS)
                        skipSpaces()
                        val value = consumeToken()
                        check(value.type in listOf(TokenType.TEXT, TokenType.QUOTED_STRING))
                        attributes[key] = value.value
                    }

                    else -> error("Unexpected token: $token")
                }
            }

            return attributes
        }

        private fun parseContent(): Text {
            val buffer = StringBuilder()
            while (lexer.token != Token.EOF) {
                val token = lexer.token
                when (token.type) {
                    TokenType.LT_SLASH, TokenType.LT, TokenType.EOF -> break
                    else -> buffer.append(token.value)
                }
                consumeToken()
            }
            return Text(buffer.toString())
        }
    }

    companion object {
        fun parse(xml: String): XmlNode? {
            val lexer = Lexer(xml)
            val parser = Parser(lexer)
            val doc = parser.parse()

            return if (lexer.nextToken() != Token.EOF) {
                // Parsing was not completed, add the rest of the input as text node
                when (doc) {
                    is Element -> Element(
                        doc.name,
                        doc.attributes,
                        doc.children + Text(lexer.input.substring(lexer.tokenPos))
                    )

                    is Text -> Text(doc.content + lexer.input.substring(lexer.tokenPos))
                }
            } else {
                doc
            }

        }
    }
}
