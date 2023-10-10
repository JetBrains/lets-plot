/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transform

object PlotSpecCleaner {

    /**
     * Makes mutable 'copy' and converts keys and values to canonic form.
     */
    fun apply(plotSpec: Map<*, *>): MutableMap<String, Any> {
        return cleanCopyOfMap(plotSpec)
    }

    private fun cleanCopyOfMap(map: Map<*, *>): MutableMap<String, Any> {
        // - drops key-value pair if value is null
        // - converts all keys to strings

        return map.entries
            .mapNotNull { (key, value) -> value?.let { key.toString() to cleanValue(it) } }
            .toMap(LinkedHashMap())
    }

    private fun cleanValue(v: Any): Any = when (v) {
        is Map<*, *> -> cleanCopyOfMap(v)
        is List<*> -> cleanList(v)
        else -> v
    }

    private fun cleanList(list: List<*>): List<*> = when {
        containSpecs(list) -> list.filterNotNull().map(::cleanValue)
        else -> list // do not change data vectors
    }

    private fun containSpecs(list: List<*>): Boolean {
        return list.any { o -> o is Map<*, *> || o is List<*> }
    }
}
