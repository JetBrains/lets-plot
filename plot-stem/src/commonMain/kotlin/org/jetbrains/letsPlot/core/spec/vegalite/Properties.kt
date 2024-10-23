/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.has
import org.jetbrains.letsPlot.core.spec.write

class Properties(
    private val vegaSpec: Map<*, *>,
    private val sectionPath: List<Any> = emptyList(),
) {

    fun write(vararg path: String, value: () -> Any) {
        vegaSpec.write(sectionPath.map { it.toString() } + path.toList(), value())
    }

    fun getAny(vararg path: Any): Any? {
        return read(path.toList())
    }

    fun getString(vararg path: String): String? {
        return read(path.toList()) as? String
    }

    fun getDouble(vararg path: String): Double? {
        return read(path.toList()) as? Double
    }

    fun getMap(vararg path: Any): Properties? {
        val map = read(path.toList()) as? Map<*, *> ?: return null
        return Properties(vegaSpec, sectionPath + path)
    }

    fun getNumber(key: String): Number? {
        return read(listOf(key)) as? Number
    }

    fun getMaps(vararg path: Any): List<Properties>? {
        val list = read(path.toList()) as? List<*> ?: return null

        return list.mapIndexed { i, v ->
            when (v) {
                is Map<*, *> -> Properties(vegaSpec, sectionPath + path + i)
                null -> null
                else -> error("Unexpected item type: ${v::class.simpleName}")
            }
        }.filterNotNull()
    }

    fun getList(vararg path: Any): List<*>? {
        val list = read(path.toList()) as? List<*> ?: return null
        return list.mapIndexed { i, v ->
            when (v) {
                is Map<*, *> -> Properties(vegaSpec, sectionPath + path + i)
                else -> v
            }
        }
    }

    operator fun get(key: String): Any? {
        return read(listOf(key))
    }

    val entries: List<Pair<*, *>>
        get() {
            val map = read() as Map<*, *>
            return map.entries.map { (key, v) ->
                when (v) {
                    is Map<*, *> -> key to Properties(vegaSpec, sectionPath + key!!)
                    else -> key to v
                }
            }
        }

    operator fun contains(key: String): Boolean {
        return (read() as? Map<*, *>)?.contains(key) ?: false
    }

    //operator fun plus(vegaSpec: MapObj<*>): MapObj<*> {
    //    return MapObj(this@VegaSpecObject.vegaSpec + vegaSpec.vegaSpec)
    //}

    //operator fun plus(pair: Pair<String, Any?>): MapObj {
    //    return MapObj(vegaSpec + pair)
    //}

    fun mapValues(transform: (Pair<String, *>) -> Any?): Properties {
        return Properties(vegaSpec.mapValues { (key, value) ->
            val pair = (key as String) to when (value) {
                is Map<*, *> -> Properties(value)
                else -> value
            }
            transform(pair)
        })
    }

    //operator fun minus(key: String): MapObj {
    //    return this//VegaSpecAccessor(vegaSpec - key)
    //}

    fun any(predicate: (Map.Entry<*, *>) -> Boolean): Boolean {
        return vegaSpec.any { predicate(it) }
    }

    //fun mapKeys(transform: (Map.Entry<Any?, Any?>) -> String?): MapObj {
    //    return MapObj(vegaSpec.mapKeys { transform(it) })
    //}

    //fun filterNotNullKeys(): MapObj {
    //    return MapObj(vegaSpec.filter { it.key != null })
    //}

    fun <K, V, R> flatMap(transform: (Map.Entry<K, V>) -> Iterable<*>): List<R> {
        return (read() as Map<*, *>).flatMap { transform(it as Map.Entry<K, V>) } as List<R>
    }

    fun <R> map(transform: (Pair<*, *>) -> R): List<R> {
        return (read() as Map<*, *>).map {
            val value = if (it.value is Map<*, *>) {
                Properties(it.value as Map<*, *>)
            } else {
                it.value
            }
            transform(it.key to value)
        }
    }

    fun has(vararg path: String): Boolean {
        val item = read(path.dropLast(1)) ?: return false

        return (item as Map<*, *>).has(path.last())
    }

    fun getData(): Map<String, Any> {
        return vegaSpec.getMap(VegaOption.DATA) ?: emptyMap()
    }


    private fun read(p: List<Any> = emptyList()): Any? {
        val fullPath = sectionPath + p
        return fullPath.fold<Any, Any?>(vegaSpec) { acc, cur ->
            if (acc == null) return null

            when (cur) {
                is String -> (acc as Map<*, *>)[cur]
                is Int -> (acc as List<*>)[cur]
                else -> error("Unexpected item type: $cur")
            }
        }
    }

    fun merge(other: Properties): Properties {
        //require(sectionPath == other.sectionPath) { "Properties sections are different: $sectionPath != ${other.sectionPath}" }
        return Properties(vegaSpec + other.vegaSpec, sectionPath)
    }

    companion object {
        val EMPTY = Properties(emptyMap<Any?, Any?>())
    }
}

