/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.json

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
