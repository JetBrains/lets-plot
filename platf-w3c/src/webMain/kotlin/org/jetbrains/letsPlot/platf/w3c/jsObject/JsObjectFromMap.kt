/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.jsObject

import kotlin.js.*

private fun createJsArray(): JsAny = js("[]")
private fun pushToJsArray(array: JsAny, item: JsAny?): Unit = js("array.push(item)")

// Note: "({})" forces JS to evaluate it as an object literal instead of an empty code block
private fun createJsObject(): JsAny = js("({})")
private fun setJsProperty(obj: JsAny, key: JsString, value: JsAny?): Unit = js("obj[key] = value")


/**
 * Converts Kotlin primitives, lists and maps to Kotlin/WasmJS JsAny.
 * Simple values (String, Boolean, Number, null) are explicitly mapped to JS primitives.
 * Lists and arrays are converted to JavaScript arrays.
 * Maps are converted to JavaScript objects.
 */
fun dynamicFromAnyQ(input: Any?): JsAny? {
    fun handleAny(value: Any?): JsAny? {
        return when (value) {
            is String -> value.toJsString()
            is Boolean -> value.toJsBoolean()
            is Number -> value.toDouble().toJsNumber()
            null -> null

            is List<*> -> {
                val jsArray = createJsArray()
                for (item in value) {
                    pushToJsArray(jsArray, handleAny(item))
                }
                jsArray
            }
            is Array<*> -> {
                val jsArray = createJsArray()
                for (item in value) {
                    pushToJsArray(jsArray, handleAny(item))
                }
                jsArray
            }
            is Map<*, *> -> {
                val jsObject = createJsObject()
                for ((key, v) in value) {
                    setJsProperty(
                        obj = jsObject,
                        key = (key as String).toJsString(),
                        value = handleAny(v)
                    )
                }
                jsObject
            }

            else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
        }
    }

    return handleAny(input)
}