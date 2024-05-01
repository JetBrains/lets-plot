/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.jsObject

/**
 * Converts a Kotlin Map to a JavaScript object recursively.
 * Simple values (String, Boolean, Number, null) are copied as is.
 * Lists and arrays are converted to JavaScript arrays.
 * Maps are converted to JavaScript objects.
 */
fun dynamicObjectFromMap(map: Map<String, Any>): dynamic {
    val jsObject = js("{}")

    var handleAny: (v: Any?) -> dynamic = {}

    fun handleList(list: List<*>): dynamic {
        val jsArray = js("[]")
        for (item in list) {
            jsArray.push(handleAny(item))
        }
        return jsArray
    }

    fun handleMap(map: Map<String, Any>): dynamic {
        @Suppress("NAME_SHADOWING")
        val jsObject = js("{}")
        for ((key, value) in map) {
            jsObject[key] = handleAny(value)
        }
        return jsObject
    }

    handleAny = { value: Any? ->
        when (value) {
            is String, is Boolean, is Number, null -> value
            is List<*> -> handleList(value)
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                handleMap(value as Map<String, Any>)
            }

            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
    }
//    fun handleAny(value: Any?): dynamic {
//        return when (value) {
//            is String, is Boolean, is Number, null -> value
//            is List<*> -> handleList(value)
//            is Map<*, *> -> handleMap(value as Map<String, Any>)
//            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
//        }
//    }

    for ((key, value) in map) {
        jsObject[key] = handleAny(value)
    }

    return jsObject
}