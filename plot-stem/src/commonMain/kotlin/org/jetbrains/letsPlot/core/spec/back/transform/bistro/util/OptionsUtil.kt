/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.util

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import org.jetbrains.letsPlot.core.spec.Option

object OptionsUtil {
    fun toSpec(options: Options): MutableMap<String, Any> {
        return toSpec(options.properties)
    }

    private fun toSpec(prop: Any?): Any? {
        return when (prop) {
            null -> null
            is Options -> toSpec(prop)
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
            is Long -> v
            is Double -> v
            is Boolean -> v
            is Color -> v.toHexColor()
            is GeomKind -> Option.GeomName.fromGeomKind(v)
            is Aes<*> -> Option.Mapping.toOption(v)
            is Pair<*, *> -> listOf(v.first, v.second)
            is PointShape -> v.code
            is NamedLineType -> v.code
            is LineType -> null
            else -> v.also {
                @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // analyzer fails to see first check: null -> null
                println("WARNING: standardising unknown type: '${v!!::class.simpleName}'")
            }
        }
    }
}
