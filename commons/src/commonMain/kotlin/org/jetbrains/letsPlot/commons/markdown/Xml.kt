/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

import org.jetbrains.letsPlot.commons.markdown.Xml.XmlNode.Element
import org.jetbrains.letsPlot.commons.markdown.Xml.XmlNode.Text

object Xml {
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
                val c = peek() ?: break
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
                    Token.SLASH_GT -> return Element(name, attributes)
                    Token.GT -> {}
                    else -> error("Expected '>' or '/>'")
                }

                val children = parseChildren()

                consumeToken(TokenType.LT_SLASH)
                consumeToken(TokenType.TEXT, skipSpaces = true)
                consumeToken(TokenType.GT)

                return Element(name, attributes, children)
            } catch (e: Throwable) {
                return Text(lexer.input.substring(pos, lexer.tokenPos))
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

                check(token().type in listOf(TokenType.TEXT, TokenType.QUOTED_STRING))
                attributes[key] = token().value
                consumeToken(skipSpaces = true)
            }

            return attributes
        }

        // <tag>this is a content</tag>
        private fun parseContent(): Text {
            val buffer = StringBuilder()
            while (token() != Token.EOF) {
                when (token()) {
                    Token.LT_SLASH, Token.LT, Token.EOF -> break
                    else -> buffer.append(token().value)
                }
                consumeToken()
            }
            return Text(buffer.toString())
        }

        private fun token() = lexer.token
    }
}
