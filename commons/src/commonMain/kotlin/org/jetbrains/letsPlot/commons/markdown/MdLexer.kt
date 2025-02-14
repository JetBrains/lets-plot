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


    internal fun createToken(type: TokenType): Token {
        return Token(type, text.substring(tokenRange()))
    }

    internal fun nextToken() {
        if (isFinished()) {
            currentToken = createToken(TokenType.EOF)
            return
        }

        startToken()

        when (val token = parseToken()) {
            TokenType.ASTERISK -> advance(1).let { createToken(token) }
            TokenType.UNDERSCORE -> advance(1).let { createToken(token) }
            TokenType.LINE_BREAK -> advance(1).let { createToken(token) }
            TokenType.WHITE_SPACE -> advance(1).let { createToken(token) }
            TokenType.BACKSLASH -> advance(1).let { createToken(token) }
            TokenType.TEXT -> {
                readText()
                createToken(token)
            }

            else -> error("Unexpected token: $token")
        }.also { currentToken = it }
    }

    private fun parseToken(): TokenType? {
        val char = text[i]
        return when {
            char.isWhitespace() -> TokenType.WHITE_SPACE
            matchToken("*") -> TokenType.ASTERISK
            matchToken("_") -> TokenType.UNDERSCORE
            matchToken(" ") -> TokenType.WHITE_SPACE
            matchToken("\n") -> TokenType.LINE_BREAK
            matchToken("\\") -> TokenType.BACKSLASH
            else -> TokenType.TEXT
        }
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

    private fun readText() {
        while (!isFinished() && parseToken() == TokenType.TEXT) {
            advance()
        }
    }

    private fun startToken() {
        tokenStart = i
    }

    private fun advance(n: Int = 1) {
        i += n
    }

    internal fun tokenValue(): String {
        return text.substring(tokenStart, i)
    }

    internal fun tokenRange(): IntRange {
        return tokenStart until i
    }

}