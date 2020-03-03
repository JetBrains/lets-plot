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


// Usefull resources:
// https://www.ietf.org/rfc/rfc4627.txt
// https://github.com/nst/JSONTestSuite

internal enum class Token {
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

internal val ESCAPING_MAP = mapOf(
    '"' to '"',
    '\\' to '\\',
    '/' to '/',
    'b' to '\b',
    'f' to '\u000C',
    'n' to '\n',
    'r' to '\r',
    't' to '\t'
)

fun String.escape() =
    this.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

fun String.unescape(): String {
    var output: StringBuilder? = null
    val start = 1
    val end = length - 1

    var i = start
    while(i < end) {
        val ch = get(i)
        if (ch == '\\') {
            output = output ?: StringBuilder(substring(start, i))
            when(get(++i)) {
                in ESCAPING_MAP -> ESCAPING_MAP[get(i)].also { i++ }
                'u' -> substring(i + 1, i + 5).toInt(16).toChar().also { i += 5 }
                else -> throw JsonParser.JsonException("Invalid escape character: ${get(i)}")
            }.let { output.append(it) }
        } else {
            output?.append(ch); i++
        }
    }
    return output?.toString() ?: substring(start, end)
}
