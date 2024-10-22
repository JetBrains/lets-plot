/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.*

class VegaSpecProp<T>(
    private val vegaSpec: Map<*, *>,
    private val path: List<Any> = emptyList(),
) {
    private fun thisValue(): Any? {
        return (path).fold<Any, Any?>(vegaSpec) { acc, cur ->
            if (acc == null) {
                return null
            }
            when (cur) {
                is String -> (acc as Map<*, *>)[cur]
                is Int -> (acc as List<*>)[cur]
                else -> error("Unexpected item type: $cur")
            }
        }
    }

    private fun read(vararg p: Any): VegaSpecProp<*>? {
        return p.fold<Any, VegaSpecProp<*>?>(this) { acc, cur ->
            if (acc == null) return null

            when (cur) {
                is String -> (acc as Map<*, *>)[cur]
                is Int -> (acc as List<*>)[cur]
                else -> error("Unexpected item type: $cur")
            }.let {
                VegaSpecProp<Any?>(vegaSpec, acc.path + cur)
            }
        }
    }

    val asMapEntries: Set<Map.Entry<*, VegaSpecProp<*>>>
        get() {
            val map = thisValue() as Map<*, *>
            return map.mapValues { (key, _) -> VegaSpecProp<Any?>(vegaSpec, path + key!!) }.entries
        }

    operator fun contains(key: String): Boolean {
        return (thisValue() as? Map<*, *>)?.contains(key) ?: false
    }

    fun getMaps(vararg path: String): List<VegaSpecProp<Map<*, *>>>? {
        val item = read(*path) ?: return null

        if (item.isList()) {
            return item.asList()
        }

        return read(path)?.getMaps(*path)?.map { VegaSpecProp(it) }
    }

    fun asList(): List<VegaSpecProp>? {
        return (thisValue() as? List<*>)?.withIndex()?.map { (i, _) -> VegaSpecProp(vegaSpec, path + i) }
    }

    operator fun get(key: String): Any? {
        return read(key)
    }

    fun getData(): Map<String, Any> {
        return vegaSpec.getMap(VegaOption.DATA) ?: emptyMap()
    }

    fun getMap(item: String): VegaSpecProp? {
        return read(item)
    }

    fun getAny(item: String): Any? {
        return read(item)
    }

    fun getString(vararg path: String): String? {
        return
    }

    operator fun plus(vegaSpecProp: VegaSpecProp): VegaSpecProp {
        return VegaSpecProp(vegaSpec + vegaSpecProp.vegaSpec)
    }

    operator fun plus(pair: Pair<String, Any?>): VegaSpecProp {
        return VegaSpecProp(vegaSpec + pair)
    }

    fun mapValues(transform: (Pair<*, *>) -> Any): VegaSpecProp {
        return VegaSpecProp(vegaSpec.mapValues {
            val value = if (it.value is Map<*, *>) {
                VegaSpecProp(it.value as Map<*, *>)
            } else {
                it.value
            }
            transform(it.key to value)
        })
    }

    operator fun minus(key: String): VegaSpecProp {
        return this//VegaSpecAccessor(vegaSpec - key)
    }

    fun any(predicate: (Map.Entry<*, *>) -> Boolean): Boolean {
        return vegaSpec.any { predicate(it) }
    }

    fun getNumber(key: String): Number? {
        return read(path + key) as? Number
    }

    fun getList(key: String): List<*>? {
        return vegaSpec.read(path + key) as? List<*>
    }

    fun mapKeys(transform: (Map.Entry<Any?, Any?>) -> String?): VegaSpecProp {
        return VegaSpecProp(vegaSpec.mapKeys { transform(it) })
    }

    fun filterNotNullKeys(): VegaSpecProp {
        return VegaSpecProp(vegaSpec.filter { it.key != null })
    }

    fun <K, V, R> flatMap(transform: (Map.Entry<K, V>) -> Iterable<*>): List<R> {
        return vegaSpec.flatMap { transform(it as Map.Entry<K, V>) } as List<R>
    }

    fun <R> map(transform: (Pair<*, *>) -> R): List<R> {
        return vegaSpec.map {
            val value = if (it.value is Map<*, *>) {
                VegaSpecProp(it.value as Map<*, *>)
            } else {
                it.value
            }
            transform(it.key to value)
        }
    }

    fun has(vararg path: String): Boolean {
        return vegaSpec.has(*path)
    }

    fun isMap(): Boolean = thisValue() is Map<*, *>
    fun isList(): Boolean = thisValue() is List<*>

    companion object {
        val EMPTY = VegaSpecProp(emptyMap<Any?, Any?>())
    }
}

