/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.jsObject

import org.jetbrains.letsPlot.commons.intern.json.escape

object JsObjectSupportCommon {
    fun mapToJsObjectInitializer(map: Map<String, *>): String {
        val buffer = StringBuilder()

        var handleValue: (v: Any?) -> Unit = {}
        val handleList = { list: List<*> ->
            buffer.append('[')
            var first = true
            for (v in list) {
                if (!first) buffer.append(',') else first = false
                handleValue(v)
            }
            buffer.append(']')
        }
        @Suppress("NAME_SHADOWING")
        val handleMap = { map: Map<*, *> ->
            buffer.append('{')
            var first = true
            for ((k, v) in map) {
                k as? String ?: throw IllegalArgumentException(
                    "Only `string` keys are supported, was: ${k!!::class.simpleName ?: "no class name"}"
                )
                if (!first) buffer.append(',') else first = false
                buffer.append('\n')
                buffer.append('"').append(k.escape()).append('"').append(':')
                handleValue(v)
            }

            buffer.append("\n}")
        }
        handleValue = { v: Any? ->
            when (v) {
                is String -> buffer.append('"').append(v.escape()).append('"')
                is Boolean,
                is Number -> buffer.append(v)
                null -> buffer.append("null")
                is Array<*> -> handleList(v.asList())
                is List<*> -> handleList(v)
                is Map<*, *> -> handleMap(v)
                else -> throw IllegalArgumentException("Can't serialize object $v")
            }
        }

        handleMap(map)
        return buffer.toString()
    }
}
