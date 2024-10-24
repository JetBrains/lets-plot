/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.has

class Properties private constructor(
    private val vegaSpec: Map<*, *>,
    private val basePath: List<Any>,
    private val accessLogger: AccessLogger
) : Map<String, Any?> {
    constructor(vegaSpec: Map<*, *>, accessLogger: AccessLogger): this(vegaSpec, emptyList(), accessLogger)

    @Suppress("UNCHECKED_CAST")
    private val thisMap: Map<String, Any?> get() = read() as Map<String, Any?>

    fun getAny(vararg path: Any): Any? {
        if (!has(*path)) {
            return null
        }

        accessLogger.log(basePath + path.toList())
        return when (val v = read(path.toList())) {
            is Map<*, *> -> subProperties(path.toList())
            else -> v
        }
    }

    fun getString(vararg path: String): String? {
        if (!has(*path)) {
            return null
        }

        accessLogger.log(basePath + path.toList())
        return read(path.toList()) as? String
    }

    fun getDouble(vararg path: String): Double? {
        if (!has(*path)) {
            return null
        }

        accessLogger.log(basePath + path.toList())
        return read(path.toList()) as? Double
    }

    fun getMap(vararg path: Any): Properties? {
        if (!has(*path)) {
            return null
        }

        accessLogger.log(basePath + path.toList())
        return subProperties(path.toList())
    }

    fun getMaps(vararg path: Any): List<Properties>? {
        if (!has(*path)) {
            return null
        }

        accessLogger.log(basePath + path.toList())
        val maps = read(path.toList()) as? List<*> ?: return null

        return maps.mapIndexed { i, map ->
            when (map) {
                is Map<*, *> -> subProperties(path.toList() + i)
                null -> null
                else -> error("Unexpected item type: ${map::class.simpleName}")
            }
        }.filterNotNull()
    }

    fun getList(vararg path: Any): List<*>? {
        if (!has(*path)) {
            return null
        }

        accessLogger.log(basePath + path.toList())
        val list = read(path.toList()) as? List<*> ?: return null
        return list.mapIndexed { i, v ->
            when (v) {
                is Map<*, *> -> subProperties(path.toList() + i)
                else -> v
            }
        }
    }

    fun has(vararg path: Any): Boolean {
        val properties = read(path.dropLast(1)) ?: return false
        return (properties as Map<*, *>).has(path.last())
    }

    override val entries: Set<Map.Entry<String, Any?>>
        get() {
            return thisMap.entries.map { (key, v) ->
                val value = when (v) {
                    is Map<*, *> -> subProperties(listOf(key))
                    else -> v
                }
                object : Map.Entry<String, Any?> {
                    override val key = key
                    override val value = value
                }
            }.toSet()
        }

    override val keys: Set<String> get() = (read() as Map<*, *>).keys.map { it as String }.toSet()
    override val size: Int get() = (read() as Map<*, *>).size
    override val values: Collection<Any?> get() = thisMap.values

    override fun get(key: String): Any? = getAny(key)
    override fun isEmpty(): Boolean = thisMap.isEmpty()

    override fun containsValue(value: Any?): Boolean {
        if (value is Properties) {
            // TODO: check basepath and vegaSpec for equality
            error("Properties instance can't be used as a value")
        }
        return thisMap.containsValue(value)
    }

    override fun containsKey(key: String): Boolean = thisMap.containsKey(key)
    operator fun contains(key: String): Boolean = containsKey(key)

    fun any(predicate: (Map.Entry<*, *>) -> Boolean): Boolean = vegaSpec.any(predicate)

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
        private val accessLog: MutableMap<String, MutableSet<String>>
    ) {
        constructor() : this(emptyList(),  mutableMapOf())
        fun nested(basePath: List<Any>) = AccessLogger(basePath, accessLog)

        fun log(property: List<Any>) {
            val str = (basePath + property).joinToString(separator = ".", transform = Any::toString)
            if (accessLog.containsKey(str)) {
                accessLog[str]!!.add("")
            } else {
                accessLog[str] = mutableSetOf("")
            }
        }
    }


    private fun subProperties(path: List<Any>): Properties {
        return Properties(vegaSpec, basePath + path, accessLogger)
    }
}
