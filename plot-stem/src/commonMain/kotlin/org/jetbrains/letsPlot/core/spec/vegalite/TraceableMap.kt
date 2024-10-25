/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.getPaths

class TraceableMap private constructor(
    private val baseMap: Map<*, *>,
    private val basePath: List<Any>,
    private val accessLogger: AccessLogger
) : Map<String, Any?> {
    constructor(vegaSpec: Map<*, *>, accessLogger: AccessLogger) : this(vegaSpec, emptyList(), accessLogger)

    private val thisMap: Map<String, Any?> = run {
        // Do not use OptionsSelector to get to the element - this will return AuditMap as it will use AuditMap::get(key)
        basePath.fold<Any, Any?>(baseMap) { acc, cur ->
            require(acc != null) {
                "Null or missing item: ${basePath.joinToString(separator = ".")}"
            }

            when (cur) {
                is String -> (acc as Map<*, *>)[cur]
                is Int -> (acc as List<*>)[cur]
                else -> error("Unexpected item type: $cur")
            }
        }
            .let {
                @Suppress("UNCHECKED_CAST")
                it as Map<String, Any?>
            }
    }

    override val keys: Set<String> get() = thisMap.keys.toSet()
    override val size: Int get() = thisMap.size
    override val values: Collection<Any?> get() = thisMap.values

    override fun isEmpty() = thisMap.isEmpty()
    override fun containsKey(key: String) = thisMap.containsKey(key)

    override fun get(key: String): Any? {
        if (key !in thisMap) {
            return null
        }

        return when (val v = thisMap[key]) {
            is Map<*, *> -> subProperties(listOf(key))
            is List<*> -> {
                var isLeaf = true
                val out = v.mapIndexed { i, el ->
                    when (el) {
                        is Map<*, *> -> subProperties(listOf(key, i)).also { isLeaf = false }
                        else -> el
                    }
                }
                if (isLeaf) {
                    accessLogger.log(basePath + listOf(key))
                }
                out
            }

            else -> v.also { accessLogger.log(basePath + listOf(key)) } // log only leaf properties, not intermediate collections
        }
    }

    override val entries: Set<Map.Entry<String, Any?>>
        get() {
            return thisMap.keys.map { key ->
                object : Map.Entry<String, Any?> {
                    override val key: String get() = key
                    override val value: Any? get() = get(key)
                }
            }.toSet()
        }

    override fun containsValue(value: Any?): Boolean {
        if (value is TraceableMap) {
            error("baseMap should not contain TraceableMap - it contain only original data")
        }
        return thisMap.containsValue(value)
    }

    class AccessLogger private constructor(
        private val basePath: List<Any>,
        private val accessLog: MutableSet<List<Any>>
    ) {
        constructor() : this(emptyList(), mutableSetOf())

        fun nested(basePath: List<Any>) = AccessLogger(this.basePath + basePath, accessLog)

        fun log(property: List<Any>) {
            val fullPath = basePath + property
            accessLog += fullPath
        }

        fun findUnusedProperties(vegaPlotSpecMap: Map<String, Any?>): List<List<Any>> {
            val all = (vegaPlotSpecMap - "data").getPaths()
            return all.filter { it !in accessLog }
        }
    }

    private fun subProperties(path: List<Any>): TraceableMap {
        return TraceableMap(baseMap, basePath + path, accessLogger)
    }
}
