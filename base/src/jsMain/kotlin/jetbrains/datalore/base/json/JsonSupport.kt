/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.json

actual object JsonSupport {
    actual fun parseJson(jsonString: String): MutableMap<String, Any?> {
        return JsonParser().handleObject(JSON.parse(jsonString))
    }

    actual fun formatJson(o: Any): String {
        return JsonFormatter().formatJson(o)
    }
}


class JsonParser {
    fun handleObject(v: dynamic): MutableMap<String, Any?> {
        return js("Object").entries(v)
            .unsafeCast<Array<Array<*>>>()
            .map { (k, v) -> k as String to handleValue(v) }
            .toMap(HashMap())
    }

    private fun handleArray(v: Array<*>) = v.map { handleValue(it) }

    private fun handleValue(v: Any?): Any? {
        return when (v) {
            is String, Boolean, null -> v
            is Number -> v.toDouble()
            is Array<*> -> handleArray(v)
            else -> handleObject(v)
        }
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

