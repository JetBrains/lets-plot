/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c.jsObject

import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.platf.w3c.interop.get
import org.jetbrains.letsPlot.platf.w3c.interop.getJsObjectKeys
import org.jetbrains.letsPlot.platf.w3c.interop.getJsProperty
import org.jetbrains.letsPlot.platf.w3c.interop.isJsArray
import kotlin.js.*

private val LOG = PortableLogging.logger("JsObjectSupportWasm")

// --- WasmJS Interop Helpers ---


fun dynamicObjectToMap(o: JsAny?): MutableMap<String, Any> {
    @Suppress("UNCHECKED_CAST")
    return dynamicToAnyQ(o) as MutableMap<String, Any>
}

/**
 * Copies object recursively with the exception of
 * arrays containing only simple values (str,number,boolean,null).
 * 'simple' arrays are wrapped in immutable lists (not copied).
 */
fun dynamicToAnyQ(o: JsAny?): Any? {
    return handleAnyNullable(o)
}

private fun handleAnyNullable(obj: JsAny?): Any? {
    if (obj == null) return null

    return when {
        obj is JsString -> obj.toString()
        obj is JsBoolean -> obj.toBoolean()
        obj is JsNumber -> obj.toDouble()
        isJsArray(obj) -> handleArray(obj.unsafeCast<JsArray<JsAny?>>())
        else -> handleObject(obj)
    }
}

private fun handleAnyNotNull(obj: JsAny?): Any {
    return handleAnyNullable(obj) ?: throw IllegalArgumentException("Null value is not expected")
}

private fun handleObject(obj: JsAny): MutableMap<String, Any> {
    val map: LinkedHashMap<String, Any> = LinkedHashMap()
    val keys: JsArray<JsString> = getJsObjectKeys(obj)

    for (i in 0 until keys.length) {
        val keyJs = keys[i]!!
        val key = keyJs.toString()
        val value = getJsProperty(obj, keyJs) ?: continue // drop nulls
        try {
            map[key] = handleAnyNotNull(value)
        } catch (e: RuntimeException) {
            LOG.error(e) { "Unexpected situation in 'dynamicObjectToMap.handleObject()'." }
        }
    }
    return map
}

private fun handleArray(arr: JsArray<JsAny?>): List<Any?> {
    return if (isArrayOfPrimitives(arr)) {
        // do not copy data vectors - wrap in a lightweight WasmJS bridge list
        JsPrimitiveList(arr)
    } else {
        val l = ArrayList<Any?>(arr.length)
        for (i in 0 until arr.length) {
            l.add(handleAnyNullable(arr[i]))
        }
        l
    }
}

private fun isArrayOfPrimitives(arr: JsArray<JsAny?>): Boolean {
    for (i in 0 until arr.length) {
        if (!isPrimitiveOrNull(arr[i])) return false
    }
    return true
}

private fun isPrimitiveOrNull(o: JsAny?): Boolean {
    return o == null || o is JsNumber || o is JsString || o is JsBoolean
}

/**
 * A wrapper to avoid copying large arrays of primitives across the Wasm/JS boundary
 * during object conversion. Values are lazily converted when requested.
 */
private class JsPrimitiveList(private val jsArray: JsArray<JsAny?>) : AbstractList<Any?>() {
    override val size: Int get() = jsArray.length

    override fun get(index: Int): Any? {
        return when (val item = jsArray[index]) {
            null -> null
            is JsString -> item.toString()
            is JsNumber -> item.toDouble()
            is JsBoolean -> item.toBoolean()
            else -> throw IllegalStateException("Expected primitive in JsPrimitiveList")
        }
    }
}