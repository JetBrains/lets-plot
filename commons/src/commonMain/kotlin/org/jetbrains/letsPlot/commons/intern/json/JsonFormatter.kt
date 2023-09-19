/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json

class JsonFormatter {
    private lateinit var buffer: StringBuilder

    fun formatJson(o: Any): String {
        buffer = StringBuilder()
        handleValue(o)
        return buffer.toString()
    }

    private fun handleList(list: List<*>) {
        append("[")
        list.headTail(::handleValue) { tail -> tail.forEach { append(","); handleValue(it) } }
        append("]")
    }

    private fun handleMap(map: Map<*, *>) {
        append("{")
        map.entries.headTail(::handlePair) { tail -> tail.forEach { append(",\n"); handlePair(it) } }
        append("}")
    }

    private fun handleValue(v: Any?) {
        when (v) {
            null -> append("null")
            is String -> handleString(v)
            is Boolean -> append(v.toString())
            is Number -> append(v.toString())
            is Array<*> -> handleList(v.asList())
            is List<*> -> handleList(v)
            is Map<*, *> -> handleMap(v)
            else -> throw IllegalArgumentException("Can't serialize object `$v`(type ${v::class.simpleName}`)")
        }
    }

    private fun handlePair(pair: Map.Entry<Any?, Any?>) {
        handleString(pair.key); append(":"); handleValue(pair.value)
    }

    private fun handleString(v: Any?) {
        when (v) {
            null -> {}
            is String -> append("\"${v.escape()}\"")
            else -> throw IllegalArgumentException("Expected a string, but got '${v::class.simpleName}'")
        }
    }

    private fun append(s: String) = buffer.append(s)

    private fun <E> Collection<E>.headTail(head: (E) -> Unit, tail: (Sequence<E>) -> Unit) {
        if (!isEmpty()) {
            head(first())
            tail(asSequence().drop(1))
        }
    }

}
