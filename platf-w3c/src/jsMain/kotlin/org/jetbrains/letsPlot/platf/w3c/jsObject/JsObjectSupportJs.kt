/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.jsObject

import jetbrains.datalore.base.logging.PortableLogging

private val LOG = PortableLogging.logger("JsObjectSupportJs")


/**
 * Copies all object's properties to hash map recursively with the exception of
 * arrays containing only simple values (str,number,boolean,null).
 * 'simple' arrays are wrapped in immutable lists (not copied)
 */
@Suppress("UNUSED_ANONYMOUS_PARAMETER", "NAME_SHADOWING")
fun dynamicObjectToMap(o: dynamic): MutableMap<String, Any> {

    var handleAnyNotNull: (o: dynamic) -> Any = {}
    var handleAnyNullable: (o: dynamic) -> Any? = {}

    val handleObject: (o: dynamic) -> MutableMap<String, Any> = { o: dynamic ->
        val map = HashMap<String, Any>()
        val entries = js("Object.entries(o)")
        for (entry in entries) {
            val key = entry[0] as String
            val value = entry[1] ?: continue            // drop nulls
            try {
                map[key] = handleAnyNotNull(value)
            } catch (e: RuntimeException) {
                LOG.error(e) { "Unexpected situation in 'dynamicObjectToMap.handleObject()'." }
            }
        }
        map
    }

    val handleArray = { o: dynamic ->
        if (isArrayOfPrimitives(o)) {  // do not copy data vectors
            listOf(*(o as Array<*>))
        } else {
            val l = ArrayList<Any?>()
            for (e in o) {
                l.add(handleAnyNullable(e))
            }
            l
        }
    }

    handleAnyNotNull = { o: dynamic ->
        handleAnyNullable(o) ?: throw IllegalArgumentException("Null value is not expected")
    }
    handleAnyNullable = { o: dynamic ->
        when (o) {
            is String,
            is Boolean,
            null -> o

            is Number -> o.toDouble()
            is Array<*> -> handleArray(o)
            else -> handleObject(o)
        }
    }

    // expecting an `object`
    return handleObject(o)
}

private fun isArrayOfPrimitives(o: dynamic) = (o as Array<*>).all { isPrimitiveOrNull(it) }

private fun isPrimitiveOrNull(o: dynamic): Boolean {
    return when (o) {
        is Number,
        is String,
        is Boolean,
        null -> true

        else -> false
    }
}
