/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

internal class MdLexer private constructor(
    private val text: String
) {
    internal enum class TokenType {
        EOF,
        BACKSLASH,
        WHITE_SPACE,
        PUNCTUATION,
        TEXT,
        ASTERISK,
        UNDERSCORE,
        LINE_BREAK,
    }

    companion object {
        fun tokenize(text: String): List<Token> {
            val lexer = MdLexer(text)
            val tokens = mutableListOf<Token>()
            while (!lexer.isFinished()) {
                lexer.nextToken()
                tokens += lexer.currentToken
            }
            return tokens
        }
    }

    data class Token(
        val type: TokenType,
        val value: String
    )

    private var i = 0
    private var tokenStart = 0
    lateinit var  currentToken: Token
        private set


    internal fun nextToken() {
        if (isFinished()) {
            currentToken = Token(TokenType.EOF, "")
            return
        }

        startToken()

        val token = currentToken()
        when (token) {
            TokenType.ASTERISK -> advance(1)
            TokenType.UNDERSCORE -> advance(1)
            TokenType.LINE_BREAK -> advance(1)
            TokenType.WHITE_SPACE -> advance(1)
            TokenType.BACKSLASH -> advance(1)
            TokenType.PUNCTUATION -> advance(1)
            TokenType.TEXT -> advanceText()
            else -> error("Unexpected token: $token")
        }

        currentToken = Token(token, text.substring(tokenStart, i))
    }

    private fun currentToken(): TokenType {
        val char = text[i]
        return when {
            char.isWhitespace() -> TokenType.WHITE_SPACE
            matchToken("*") -> TokenType.ASTERISK
            matchToken("_") -> TokenType.UNDERSCORE
            matchToken(" ") -> TokenType.WHITE_SPACE
            matchToken("\n") -> TokenType.LINE_BREAK
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

    internal fun isFinished(): Boolean {
        return i >= text.length
    }

    private fun advanceText() {
        while (!isFinished() && currentToken() == TokenType.TEXT) {
            advance()
        }
    }

    private fun startToken() {
        tokenStart = i
    }

    private fun advance(n: Int = 1) {
        i += n
    }

    internal fun tokenRange(): IntRange {
        return tokenStart until i
    }
}
