/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

internal class Lexer private constructor(
    private val text: String
) {
    companion object {
        fun tokenize(text: String): List<Token> {
            val lexer = Lexer(text)
            val tokens = mutableListOf<Token>()
            while (!lexer.isFinished()) {
                lexer.nextToken()
                tokens += lexer.currentToken
            }
            return tokens
        }
    }

    private var i = 0
    private var tokenStart = 0
    private lateinit var  currentToken: Token

    private fun nextToken() {
        if (isFinished()) {
            currentToken = Token(TokenType.EOF, "")
            return
        }

        startToken()

        val token = peek()
        when (token) {
            TokenType.LINE_BREAK -> advance(3)

            TokenType.ASTERISK,
            TokenType.UNDERSCORE,
            TokenType.SOFT_BREAK,
            TokenType.WHITE_SPACE,
            TokenType.BACKSLASH,
            TokenType.PUNCTUATION -> advance(1)

            TokenType.TEXT -> advanceText()
            TokenType.EOF -> {}
        }

        currentToken = Token(token, text.substring(tokenStart, i))
    }

    private fun peek(): TokenType {
        val char = text[i]
        return when {
            matchToken("  \n") -> TokenType.LINE_BREAK
            matchToken("\n") -> TokenType.SOFT_BREAK
            char.isWhitespace() -> TokenType.WHITE_SPACE
            matchToken("*") -> TokenType.ASTERISK
            matchToken("_") -> TokenType.UNDERSCORE
            matchToken(" ") -> TokenType.WHITE_SPACE
            matchToken("\\") -> TokenType.BACKSLASH
            isPunctuation(char) -> TokenType.PUNCTUATION
            else -> TokenType.TEXT
        }
    }

    private fun isPunctuation(ch: Char): Boolean {
        return ch in setOf('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '`', '{', '|', '}', '~')
    }

    private fun matchToken(token: String): Boolean {
        if (text.length - i < token.length) {
            return false
        }

        if (text.regionMatches(i, token, 0, token.length)) {
            return true
        }
        return false
    }

    private fun isFinished(): Boolean {
        return i >= text.length
    }

    private fun advanceText() {
        while (!isFinished() && peek() == TokenType.TEXT) {
            advance()
        }
    }

    private fun startToken() {
        tokenStart = i
    }

    private fun advance(n: Int = 1) {
        i += n
    }

    internal enum class TokenType {
        EOF,
        BACKSLASH,
        WHITE_SPACE,
        PUNCTUATION,
        TEXT,
        ASTERISK,
        UNDERSCORE,
        LINE_BREAK,
        SOFT_BREAK,
    }

    internal data class Token(
        val type: TokenType,
        val value: String
    )
}
