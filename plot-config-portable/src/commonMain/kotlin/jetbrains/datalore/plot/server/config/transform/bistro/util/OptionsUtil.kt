/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.config.Option

object OptionsUtil {
    fun toSpec(options: Options<*>): MutableMap<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return toSpec(options.properties)
    }

    private fun toSpec(prop: Any?): Any? {
        return when (prop) {
            null -> null
            is Options<*> -> toSpec(prop)
            is List<*> -> prop.map(this::toSpec)
            is Map<*, *> -> toSpec(prop)
            else -> standardise(prop)
        }
    }

    private fun toSpec(prop: Map<*, *>): MutableMap<String, Any> {
        return prop.mapNotNull { (key, value) ->
            val specKey = standardise(key)
            require(specKey != null) { "Spec key can't be null" }
            require(specKey is String) { "Spec key should be a string, but was '${specKey::class.simpleName}'" }
            toSpec(value)?.let { specValue -> specKey to specValue }
        }.toMap().toMutableMap()
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
            is Aes<*> -> Option.Mapping.toOption(v)
            is Pair<*, *> -> listOf(v.first, v.second)
            else -> v.also {
                @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // analyzer fails to see first check: null -> null
                println("WARNING: standardising unknown type: '${v!!::class.simpleName}'")
            }
        }
    }
}
