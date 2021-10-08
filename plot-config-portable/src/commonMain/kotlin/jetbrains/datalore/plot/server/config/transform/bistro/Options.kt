/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.config.Option.Mapping.toOption
import jetbrains.datalore.plot.config.asMutable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class Options<T>(
    val properties: MutableMap<String, Any?>
) {
    constructor() : this(mutableMapOf())

    inline fun <T, reified TValue> T.map(key: String): ReadWriteProperty<T, TValue?> {
        return object : ReadWriteProperty<T, TValue?> {
            override fun getValue(thisRef: T, property: KProperty<*>): TValue? = properties.get(key) as TValue?
            override fun setValue(thisRef: T, property: KProperty<*>, value: TValue?) = run { properties[key] = value }
        }
    }

    fun toSpec(): MutableMap<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return toSpec(properties).filter { it.value != null }.asMutable()
    }

    private fun toSpec(prop: Any?): Any? {
        return when (prop) {
            null -> null
            is Options<*> -> prop.toSpec()
            is List<*> -> prop.map(this::toSpec)
            is Map<*, *> -> toSpec(prop)
            else -> standardise(prop)
        }
    }

    private fun toSpec(prop: Map<*, *>): Map<*, *> {
        return prop.asSequence().associate { (key, value) -> standardise(key) to toSpec(value) }
    }

    private inline fun <reified TValue> standardise(v: TValue?): Any? {
        return when (v) {
            null -> null
            is String -> v
            is Int -> v
            is Double -> v
            is Boolean -> v
            is Color -> v.toHexColor()
            is GeomKind -> v.name.lowercase()
            is Aes<*> -> toOption(v)
            is Pair<*, *> -> listOf(v.first, v.second)
            else -> v.also { println("WARNING: standardising unknown type: '${v!!::class.simpleName}'") }
        }
    }
}
