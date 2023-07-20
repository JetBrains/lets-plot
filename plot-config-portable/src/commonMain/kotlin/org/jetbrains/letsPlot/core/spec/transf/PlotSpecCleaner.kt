/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transf

object PlotSpecCleaner {

    /**
     * Makes muitable 'copy' and converts keys and values to canonic form.
     */
    fun apply(plotSpec: Map<*, *>): MutableMap<String, Any> {
        return cleanCopyOfMap(plotSpec)
    }

    private fun cleanCopyOfMap(map: Map<*, *>): MutableMap<String, Any> {
        // - drops key-value pair if value is null
        // - converts all keys to strings
        val out = LinkedHashMap<String, Any>()
        for (k in map.keys) {
            val v = map[k]
            if (v != null) {
                val key = k.toString()
                out[key] = cleanValue(v)
            }
        }
        return out
    }

    private fun cleanValue(v: Any): Any {
        if (v is Map<*, *>) {
            return cleanCopyOfMap(v)
        } else if (v is List<*>) {
            return cleanList(v)
        }
        return v
    }

    private fun cleanList(list: List<*>): List<*> {
        if (!containSpecs(list)) {
            // do not change data vectors
            return list
        }
        val copy = ArrayList<Any>(list.size)
        for (o in list) {
            copy.add(cleanValue(o!!))
        }
        return copy
    }

    private fun containSpecs(list: List<*>): Boolean {
        return list.any { o -> o is Map<*, *> || o is List<*> }
    }
}
