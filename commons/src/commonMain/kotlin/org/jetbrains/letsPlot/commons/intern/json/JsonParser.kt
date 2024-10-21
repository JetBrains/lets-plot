/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json

internal class JsonParser(
    private val json: String
) {
    fun parseJson(): Any? {
        val lexer = JsonLexer(json)
        return parseValue(lexer)
    }

    private fun parseValue(lexer: JsonLexer): Any? {
        return when(lexer.currentToken) {
            Token.STRING -> lexer.tokenValue().unescape().also { lexer.nextToken() }
            Token.NUMBER -> lexer.tokenValue().toDouble().also { lexer.nextToken() }
            Token.FALSE -> false.also { lexer.nextToken() }
            Token.TRUE -> true.also { lexer.nextToken() }
            Token.NULL -> null.also { lexer.nextToken() }
            Token.LEFT_BRACE -> parseObject(lexer)
            Token.LEFT_BRACKET -> parseArray(lexer)
            else -> error("Invalid token: ${lexer.currentToken}")
        }
    }

    private fun parseArray(lexer: JsonLexer): MutableList<Any?> {
        fun checkCurrentToken(token: Token) { require(lexer.currentToken, token, "[Arr] ") }

        val list = mutableListOf<Any?>()

        checkCurrentToken(Token.LEFT_BRACKET)
        lexer.nextToken()

        while (lexer.currentToken != Token.RIGHT_BRACKET) {
            if (list.isNotEmpty()) {
                checkCurrentToken(Token.COMMA)
                lexer.nextToken()
            }
            list.add(parseValue(lexer))
        }

        checkCurrentToken(Token.RIGHT_BRACKET)
        lexer.nextToken()

        return list
    }

    private fun parseObject(lexer: JsonLexer): Map<String, Any?> {
        fun checkCurrentToken(token: Token) { require(lexer.currentToken, token, "[Obj] ") }

        val map = mutableMapOf<String, Any?>()

        checkCurrentToken(Token.LEFT_BRACE)
        lexer.nextToken()

        while (lexer.currentToken != Token.RIGHT_BRACE) {
            if (map.isNotEmpty()) {
                checkCurrentToken(Token.COMMA)
                lexer.nextToken()
            }

            checkCurrentToken(Token.STRING)
            val key = lexer.tokenValue().unescape()
            lexer.nextToken()

            checkCurrentToken(Token.COLON)
            lexer.nextToken()

            val value = parseValue(lexer)
            map[key] = value
        }

        checkCurrentToken(Token.RIGHT_BRACE)
        lexer.nextToken()

        return map
    }

    private fun require(current: Token?, expected: Token?, messagePrefix: String? = null) {
        if (current != expected) {
            throw JsonException(messagePrefix + "Expected token: $expected, actual: $current")
        }
    }

    class JsonException(message: String) : Exception(message)
}
