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
        val ctx = output.newCollectionContext(list)

        output.startList(ctx)
        list.headTail()?.let { (head, tail) ->
            output.firstItem(ctx)
            handleValue(head)
            tail.forEach {
                output.nextItem(ctx)
                handleValue(it)
            }
        }
        output.endList(ctx)
    }

    private fun handleMap(map: Map<*, *>) {
        val ctx = output.newCollectionContext(map.values)

        output.startMap(ctx)
        map.entries.headTail()?.let {(head, tail) ->
            output.firstItem(ctx)
            handlePair(head)
            tail.forEach {
                output.nextItem(ctx)
                handlePair(it)
            }
        }
        output.endMap(ctx)
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

    private fun <E> Collection<E>.headTail(): Pair<E, Sequence<E>>? {
        if (isEmpty()) {
            return null
        }

        return Pair(first(), asSequence().drop(1))
    }

    interface Output {
        interface CollectionContext

        fun newCollectionContext(c: Collection<*>): CollectionContext

        fun append(s: String)

        fun startList(ctx: CollectionContext)
        fun endList(ctx: CollectionContext)

        fun startMap(ctx: CollectionContext)
        fun endMap(ctx: CollectionContext)

        fun firstItem(ctx: CollectionContext)
        fun nextItem(ctx: CollectionContext)

        fun asString(): String
    }

    class Simple : Output {
        private var buffer: StringBuilder = StringBuilder()

        override fun newCollectionContext(c: Collection<*>) = object : Output.CollectionContext {}

        override fun startList(ctx: Output.CollectionContext) = append("[")
        override fun endList(ctx: Output.CollectionContext) = append("]")
        override fun startMap(ctx: Output.CollectionContext) = append("{")
        override fun endMap(ctx: Output.CollectionContext) = append("}")
        override fun asString(): String = buffer.toString()
        override fun firstItem(ctx: Output.CollectionContext) = Unit

        override fun nextItem(ctx: Output.CollectionContext) {
            buffer.append(",")
        }

        override fun append(s: String) {
            buffer.append(s)
        }
    }


    class Pretty : Output {
        class PrettyContext(
            collection: Collection<*>,
        ) : Output.CollectionContext {
            val splitLines = collection.size > 10 || collection.any { it is Collection<*> || it is Map<*, *> }
        }

        override fun newCollectionContext(c: Collection<*>) = PrettyContext(c)

        private var indent = 0
        private var buffer: StringBuilder = StringBuilder()

        override fun startList(ctx: Output.CollectionContext) {
            buffer.append("[")
            indent++
        }

        override fun endList(ctx: Output.CollectionContext) {
            indent--

            if ((ctx as PrettyContext).splitLines) {
                buffer.append("\n")
                buffer.append(indent())
            } else {
                buffer.append(" ")
            }

            buffer.append("]")
        }

        override fun startMap(ctx: Output.CollectionContext) {
            buffer.append("{")
            indent++
        }

        override fun endMap(ctx: Output.CollectionContext) {
            indent--

            if ((ctx as PrettyContext).splitLines) {
                buffer.append("\n")
                buffer.append(indent())
            } else {
                buffer.append(" ")
            }

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

        override fun firstItem(ctx: Output.CollectionContext) {
            if ((ctx as PrettyContext).splitLines) {
                buffer.append("\n")
                buffer.append(indent())
            } else {
                buffer.append(" ")
            }
        }

        override fun nextItem(ctx: Output.CollectionContext) {
            buffer.append(", ")
            if ((ctx as PrettyContext).splitLines) {
                buffer.append("\n")
                buffer.append(indent())
            }
        }
    }
}
