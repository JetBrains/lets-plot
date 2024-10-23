/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json

internal class JsonLexer(
    private val input: String
) {
    private var i = 0
    private var tokenStart = 0
    var currentToken: Token? = null
        private set

    private val currentChar: Char
        get() = input[i]

    init {
        nextToken() // read first token
    }

    fun nextToken() {
        advanceWhile { it.isWhitespace() }

        if (isFinished()) {
            return
        }

        when {
            currentChar == '{' -> Token.LEFT_BRACE.also { advance() }
            currentChar == '}' -> Token.RIGHT_BRACE.also { advance() }
            currentChar == '[' -> Token.LEFT_BRACKET.also { advance() }
            currentChar == ']' -> Token.RIGHT_BRACKET.also { advance() }
            currentChar == ',' -> Token.COMMA.also { advance() }
            currentChar == ':' -> Token.COLON.also { advance() }
            currentChar == 't' -> Token.TRUE.also { read("true") }
            currentChar == 'f' -> Token.FALSE.also { read("false") }
            currentChar == 'n' -> Token.NULL.also { read("null") }
            currentChar == '"' -> Token.STRING.also { readString() }
            readNumber() -> Token.NUMBER
            else -> error(formatErrorMessage())
        }.also { currentToken = it }
    }

    fun tokenValue() = input.substring(tokenStart, i)

    private fun readString() {
        startToken()
        advance() // opening quote
        while (currentChar != '"') {
            if (currentChar == '\\') {
                advance()
                when (currentChar) {
                    'u' -> {
                        advance()
                        repeat(4) {
                            require(currentChar.isHex())
                            advance()
                        }
                    }
                    in SPECIAL_CHARS -> advance()
                    else -> error("Invalid escape sequence")
                }
            } else {
                advance()
            }
        }
        advance() // closing quote
    }

    private fun readNumber(): Boolean {
        if (!(currentChar.isDigit() || currentChar == '-')) {
            return false
        }

        startToken()
        advanceIfCurrent('-')
        advanceWhile { it.isDigit() }

        advanceIfCurrent('.') {
            require(currentChar.isDigit()) { "Number should have decimal part" }
            advanceWhile { it.isDigit() }
        }

        advanceIfCurrent('e', 'E') {
            advanceIfCurrent('+', '-')
            advanceWhile { it.isDigit() }
        }

        return true
    }

    fun isFinished(): Boolean = i == input.length
    private fun startToken() { tokenStart = i }
    private fun advance() { ++i }

    private fun read(str: String) {
        return str.forEach {
            require(currentChar == it) { "Wrong data: $str" }
            require(!isFinished()) { "Unexpected end of string" }
            advance()
        }
    }

    private fun advanceWhile(pred: (Char) -> Boolean) {
        while (!isFinished() && pred(currentChar)) advance()
    }

    private fun advanceIfCurrent(vararg expected: Char, then: () -> Unit = {}) {
        if (!isFinished() && currentChar in expected) {
            advance()
            then()
        }
    }

    private fun formatErrorMessage(): String {
        val errLineStart = input.subSequence(0, i).lastIndexOf('\n').let { if (it == -1) 0 else it + 1 }
        val errLineEnd = input.indexOf('\n', i).let { if (it == -1) input.length else it }
        val errLineLength = errLineEnd - errLineStart

        val (errFragmentStart, errFragmentEnd) = when {
            errLineLength <= 81 -> Pair(errLineStart, errLineEnd)
            errLineLength > 81 -> Pair(
                maxOf(errLineStart, i - 40),
                minOf(errLineEnd, i + 41)
            )
            errLineLength < 0 -> error("Negative line length") // should never happen
            else -> error("Unexpected line length: $errLineLength") // should never happen either
        }

        // Note that "at $i" is a zero-based index, while "$errorLineNumber" and "$errorSymbolLinePos" are one-based.
        val errorLineNumber = input.subSequence(0, i).count { it == '\n' }.let { if (it == 0) 1 else it + 1 }
        val errorSymbolLinePos = i - errLineStart + 1

        return "Unknown token >$currentChar< at $i ($errorLineNumber:$errorSymbolLinePos)\n" +
                "${input.substring(errFragmentStart, errFragmentEnd)}\n" +
                " ".repeat(i - errFragmentStart) + "^"
    }

    companion object {
        private val digits: CharRange = '0'..'9'
        private fun Char?.isDigit() = this in digits
        private fun Char.isHex(): Boolean = isDigit() || this in 'a'..'f' || this in 'A'..'F'
    }
}
