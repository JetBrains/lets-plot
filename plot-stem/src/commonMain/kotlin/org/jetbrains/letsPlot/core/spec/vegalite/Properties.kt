/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

class Properties private constructor(
    private val vegaSpec: Map<*, *>,
    private val basePath: List<Any>,
    private val accessLogger: AccessLogger
) : Map<String, Any?> {
    constructor(vegaSpec: Map<*, *>, accessLogger: AccessLogger) : this(vegaSpec, emptyList(), accessLogger)

    @Suppress("UNCHECKED_CAST")
    private val thisMap: Map<String, Any?> get() = read() as Map<String, Any?>

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
                        is List<*> -> subProperties(listOf(key, i)).also { isLeaf = false }
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
        if (value is Properties) {
            // TODO: check basepath and vegaSpec for equality
            error("Properties instance can't be used as a value")
        }
        return thisMap.containsValue(value)
    }

    private fun read(path: List<Any> = emptyList()): Any? {
        val fullPath = basePath + path
        return fullPath.fold<Any, Any?>(vegaSpec) { acc, cur ->
            if (acc == null) return null

            when (cur) {
                is String -> (acc as Map<*, *>)[cur]
                is Int -> (acc as List<*>)[cur]
                else -> error("Unexpected item type: $cur")
            }
        }
    }

    class AccessLogger private constructor(
        private val basePath: List<Any>,
        private val accessLog: MutableMap<List<Any>, MutableSet<String>>
    ) {
        constructor() : this(emptyList(), mutableMapOf())

        fun nested(basePath: List<Any>) = AccessLogger(this.basePath + basePath, accessLog)

        fun log(property: List<Any>) {
            val fullPath = basePath + property
            if (accessLog.containsKey(fullPath)) {
                accessLog[fullPath]!!.add("")
            } else {
                accessLog[fullPath] = mutableSetOf("")
            }
        }

        fun findUnused(vegaPlotSpecMap: MutableMap<String, Any?>) {
            val out = mutableListOf<List<Any>>()

            fun containsMap(list: List<*>): Boolean {
                list.forEach {
                    when (it) {
                        is Map<*, *> -> return true
                        is List<*> -> if (containsMap(it)) return true
                    }
                }
                return false
            }

            fun flattenPaths(item: Any, basePath: List<Any> = emptyList()): List<List<Any>> {
                if (item is Map<*, *>) {
                    item.forEach { (key, value) ->
                        when (value) {
                            is Map<*, *> -> flattenPaths(value, basePath + key as Any)
                            is List<*> -> value.forEachIndexed { i, el ->
                                when (el) {
                                    is Map<*, *> -> flattenPaths(el, basePath + key as Any + i)
                                    is List<*> -> when {
                                        containsMap(el) -> flattenPaths(el, basePath + key as Any + i)
                                        else -> out.add(basePath + key as Any + i)
                                    }
                                    else -> out.add(basePath + key as Any)
                                }
                            }

                            else -> out.add(basePath + key as Any)
                        }
                    }
                }
                return out
            }

            val all = flattenPaths(vegaPlotSpecMap - "data")
            val unused = all.filter { it !in accessLog }
            println()
        }
    }

    private fun subProperties(path: List<Any>): Properties {
        return Properties(vegaSpec, basePath + path, accessLogger)
    }
}
