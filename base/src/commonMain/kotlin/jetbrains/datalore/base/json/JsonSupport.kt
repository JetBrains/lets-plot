/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.json

object JsonSupport {
    fun parseJson(jsonString: String): MutableMap<String, Any?> {
        @Suppress("UNCHECKED_CAST")
        return JsonParser(jsonString).parseJson() as MutableMap<String, Any?>
    }
    fun formatJson(o: Any): String {
        return JsonFormatter().formatJson(o)
    }
}

class JsonFormatter {
    private lateinit var buffer: StringBuilder

    fun formatJson(o: Any): String {
        buffer = StringBuilder()
        formatMap(o as Map<*, *>)
        return buffer.toString()
    }

    private fun formatList(list: List<*>) {
        append("[")
        list.headTail(::formatValue) { tail -> tail.forEach { append(","); formatValue(it) } }
        append("]")
    }

    private fun formatMap(map: Map<*, *>) {
        append("{")
        map.entries.headTail(::formatPair) { tail -> tail.forEach { append(",\n"); formatPair(it) } }
        append("}")
    }

    private fun formatValue(v: Any?) {
        when (v) {
            null -> append("null")
            is String -> append("\"${v.escape()}\"")
            is Number, Boolean -> append(v.toString())
            is Array<*> -> formatList(v.asList())
            is List<*> -> formatList(v)
            is Map<*, *> -> formatMap(v)
            else -> throw IllegalArgumentException("Can't serialize object $v")
        }
    }

    private fun formatPair(pair: Map.Entry<Any?, Any?>) {
        append("\"${pair.key}\":"); formatValue(pair.value)
    }

    private fun append(s: String) = buffer.append(s)

    private fun <E> Collection<E>.headTail(head: (E) -> Unit, tail: (Sequence<E>) -> Unit) {
        if (!isEmpty()) {
            head(first())
            tail(asSequence().drop(1))
        }
    }
}


// Usefull resources:
// https://www.ietf.org/rfc/rfc4627.txt
// https://github.com/nst/JSONTestSuite

enum class Token {
    LEFT_BRACE,
    RIGHT_BRACE,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    COMMA,
    COLON,
    STRING,
    NUMBER,
    TRUE,
    FALSE,
    NULL,
}

class Lexer(
    private val input: String
) {
    private var i = 0
    private var tokenStart = 0
    var currentToken: Token? = null
        private set

    private val currentChar: Char
        get() = input[i]

    fun nextToken(): Token? {
        advanceWhile { it.isWhitespace() }

        if (isFinished()) {
            return null
        }

        return when {
            currentChar == '{' -> Token.LEFT_BRACE.also { advance() }
            currentChar == '}' -> Token.RIGHT_BRACE.also { advance() }
            currentChar == '[' -> Token.LEFT_BRACKET.also { advance() }
            currentChar == ']' -> Token.RIGHT_BRACKET.also { advance() }
            currentChar == ',' -> Token.COMMA.also { advance() }
            currentChar == ':' -> Token.COLON.also { advance() }
            currentChar == 't' -> Token.TRUE.also { read("true") }
            currentChar == 'f' -> Token.FALSE.also { read("false") }
            currentChar == 'n' -> Token.NULL.also { read("null") }
            isStringToken(currentChar) -> Token.STRING.also { readString() }
            readNumber() -> Token.NUMBER
            else -> error("Unkown token: ${currentChar}")
        }.also { currentToken = it }
    }

    fun tokenValue() = input.substring(tokenStart, i)

    private fun readString() {
        startToken()
        advance() // opening quote
        while(!isStringToken(currentChar)) {
            if(currentChar == '\\') {
                advance()
                when {
                    currentChar == 'u' -> {
                        advance()
                        repeat(4) {
                            require(currentChar.isHex());
                            advance()
                        }
                    }
                    currentChar in UNESCAPED -> advance()
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

    private fun isFinished(): Boolean = i == input.length
    private fun startToken() { tokenStart = i }
    private fun advance() { ++i }
    private fun isStringToken(c: Char) = c == '\"' || c == '\''

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
}

private val UNESCAPED = arrayOf('"', '\\', '/', 'b', 'f', 'n', 'r', 't')
private val ESCAPED = arrayOf('\"', '\\', '/', '\b', '\u000C', '\n', '\r', '\t')
private val digits: CharRange = '0'..'9'
private fun Char?.isDigit() = this in digits
private fun Char.isHex(): Boolean { return isDigit() || this in 'a'..'f' || this in 'A'..'F' }

class JsonParser(
    private val json: String
) {

    fun parseJson(): Any? {
        val lexer = Lexer(json).also { it.nextToken() }
        return parseValue(lexer)
    }

    private fun parseValue(lexer: Lexer): Any? {
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

    private fun parseArray(lexer: Lexer): MutableList<Any?> {
        val list = mutableListOf<Any?>()
        require(lexer.currentToken, Token.LEFT_BRACKET, "[Arr] ")
        lexer.nextToken()
        while (lexer.currentToken != Token.RIGHT_BRACKET) {
            if (list.isNotEmpty()) {
                require(lexer.currentToken, Token.COMMA, "[Arr] ")
                lexer.nextToken()
            }
            list.add(parseValue(lexer))
        }
        lexer.nextToken()

        return list
    }

    private fun parseObject(lexer: Lexer): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        require(lexer.currentToken, Token.LEFT_BRACE, "[Obj] ")
        lexer.nextToken()
        while (lexer.currentToken != Token.RIGHT_BRACE) {
            if (map.isNotEmpty()) {
                require(lexer.currentToken, Token.COMMA, "[Obj] ")
                lexer.nextToken()
            }

            require(lexer.currentToken, Token.STRING, "[Obj] ")
            val key = lexer.tokenValue().unescape()

            require(lexer.nextToken(), Token.COLON, "[Obj] ")

            lexer.nextToken()
            val value = parseValue(lexer)

            map[key] = value
        }
        lexer.nextToken()

        return map
    }

    private fun require(current: Token?, expected: Token?, messagePrefix: String? = null) {
        if (current != expected) {
            throw JsonException(messagePrefix + "Expected token: $expected, actual: $current")
        }
    }

    class JsonException(message: String) : Exception(message) {

    }
}

private fun String.unescape(): String {
    val output = StringBuilder()
    var i = 1
    var end = length - 1
    while(i < end) {
        output.append(
            if (get(i) == '\\') {
                i++
                when {
                    get(i) in UNESCAPED -> { ESCAPED[UNESCAPED.indexOf(get(i))].also { i++ } }
                    get(i) == 'u' -> { i++; substring(i, i + 4).toInt(16).toChar().also { i += 4 } }
                    else -> { error("Invalid escape sequence") }
                }
            } else { get(i++) }
        )
    }
    return output.toString()
}
