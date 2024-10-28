/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.getPaths

class TraceableMapWrapper private constructor(
    private val baseMap: Map<*, *>,
    private val basePath: List<Any>,
    private val accessLogger: AccessLogger
) : Map<String, Any?> {
    constructor(baseMap: Map<*, *>, accessLogger: AccessLogger) : this(baseMap, emptyList(), accessLogger)

    private val thisMap: Map<String, Any?> = run {
        // Avoid using OptionsSelector to access the element, as it will return a TraceableMapWrapper
        // instead of underneath map due to Map::get(key) -> TraceableMapWrapper::get(key) delegation.
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
                    accessLogger.logRead(basePath + listOf(key))
                }
                out
            }

            else -> v.also { accessLogger.logRead(basePath + listOf(key)) } // log only leaf properties, not intermediate collections
        }
    }

    override val entries: Set<Map.Entry<String, Any?>>
        get() {
            return thisMap.keys.map { key ->
                val value = get(key)
                object : Map.Entry<String, Any?> {
                    override val key: String get() = key
                    override val value: Any? get() = value
                }
            }.toSet()
        }

    override fun containsValue(value: Any?): Boolean {
        if (value is TraceableMapWrapper) {
            error("baseMap should not contain TraceableMapWrapper - it contain only original data")
        }
        return thisMap.containsValue(value)
    }

    class AccessLogger private constructor(
        private val basePath: List<Any>,
        private val accessLog: MutableSet<List<Any>>
    ) {
        constructor() : this(emptyList(), mutableSetOf())

        private var buildingReport = false

        fun nested(basePath: List<Any>) = AccessLogger(this.basePath + basePath, accessLog)

        fun logRead(property: List<Any>) {
            if (buildingReport) return

            val fullPath = basePath + property
            accessLog += fullPath
        }

        fun findUnusedProperties(vegaPlotSpecMap: Map<String, Any?>): List<List<Any>> {
            buildingReport = true
            val all = (vegaPlotSpecMap).getPaths()
            buildingReport = false
            return all.filter { it !in accessLog }
        }
    }

    private fun subProperties(path: List<Any>): TraceableMapWrapper {
        return TraceableMapWrapper(baseMap, basePath + path, accessLogger)
    }
}
