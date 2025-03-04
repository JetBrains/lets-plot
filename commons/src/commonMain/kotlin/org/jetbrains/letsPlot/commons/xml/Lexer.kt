/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.xml

import org.jetbrains.letsPlot.commons.xml.TokenType.*

internal class Lexer(
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
            '"' -> {
                advance() // consume opening quote
                val token = Token(QUOTED_STRING, readUntil(listOf(eq('"'))))
                advance() // consume closing quote

                token
            }
            '\'' -> {
                advance() // consume opening quote
                val token = Token(QUOTED_STRING, readUntil(listOf(eq('\''))))
                advance() // consume closing quote

                token
            }

            else -> when {
                c.isWhitespace() -> Token(WHITESPACE, c.toString()).also { advance() }
                else -> Token(TEXT, readUntil(listOf(eq('<'), eq('/'), eq('>'), eq('"'), eq('='), Char::isWhitespace)))
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

