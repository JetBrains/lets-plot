/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.json

internal class JsonFormatter(pretty: Boolean = false) {
    private val output: Output = if (pretty) Pretty() else Simple()

    fun formatJson(o: Any): String {
        handleValue(o)
        return output.asString()
    }

    private fun handleList(list: List<*>) {
        output.startList()
        list.headTail({
            output.firstItem()
            handleValue(it)
        }) { tail ->
            tail.forEach {
                output.nextItem()
                handleValue(it)
            }
        }
        output.endList()
    }

    private fun handleMap(map: Map<*, *>) {
        output.startMap()
        map.entries.headTail({
            output.firstItem()
            handlePair(it)
        }) { tail ->
            tail.forEach {
                output.nextItem()
                handlePair(it)
            }
        }
        output.endMap()
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
        handleString(pair.key); append(": "); handleValue(pair.value)
    }

    private fun handleString(v: Any?) {
        when (v) {
            null -> {}
            is String -> append("\"${v.escape()}\"")
            else -> throw IllegalArgumentException("Expected a string, but got '${v::class.simpleName}'")
        }
    }

    private fun append(s: String) = output.append(s)

    private fun <E> Collection<E>.headTail(head: (E) -> Unit, tail: (Sequence<E>) -> Unit) {
        if (!isEmpty()) {
            head(first())
            tail(asSequence().drop(1))
        }
    }

    interface Output {
        fun append(s: String)

        fun startList()
        fun endList()

        fun startMap()
        fun endMap()

        fun firstItem()
        fun nextItem()

        fun asString(): String
    }

    class Simple : Output {
        private var buffer: StringBuilder = StringBuilder()

        override fun startList() = append("[")
        override fun endList() = append("]")
        override fun startMap() = append("{")
        override fun endMap() = append("}")
        override fun asString(): String = buffer.toString()
        override fun firstItem() = Unit

        override fun nextItem() {
            buffer.append(",")
        }

        override fun append(s: String) {
            buffer.append(s)
        }
    }


    class Pretty : Output {
        private var indent = 0
        private var buffer: StringBuilder = StringBuilder()

        override fun startList() {
            buffer.append("[")
            indent++
        }

        override fun endList() {
            indent--
            buffer.append("\n")
            buffer.append(indent())
            buffer.append("]")
        }

        override fun startMap() {
            buffer.append("{")
            indent++
        }

        override fun endMap() {
            indent--
            buffer.append("\n")
            buffer.append(indent())
            buffer.append("}")
        }

        override fun asString(): String {
            return buffer.toString()
        }

        override fun append(s: String) {
            buffer.append(s)
        }

        private fun indent(): String {
            return "  ".repeat(indent * 2)
        }

        override fun firstItem() {
            buffer.append("\n")
            buffer.append(indent())
        }

        override fun nextItem() {
            buffer.append(", ")
            buffer.append("\n")
            buffer.append(indent())
        }
    }
}
