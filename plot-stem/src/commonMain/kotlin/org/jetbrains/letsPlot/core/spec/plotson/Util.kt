/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption
import org.jetbrains.letsPlot.core.spec.StatKind

fun PlotOptions.toJson(): MutableMap<String, Any> {
    return toJson(properties)
}

internal fun toJson(v: Any?): Any? {
    return when (v) {
        null -> null
        is InlineOptions -> null
        is Options -> toJson(v.toSpec())
        is List<*> -> v.map(::toJson)
        is Map<*, *> -> toJson(v)
        else -> standardise(v)
    }
}

private fun toJson(obj: Map<*, *>): MutableMap<String, Any> {
    return obj.mapNotNull { (key, value) ->
        val specKey = standardise(key)
        require(specKey != null) { "Spec key can't be null" }
        require(specKey is String) { "Spec key should be a string, but was '${specKey::class.simpleName}'" }
        toJson(value)?.let { specValue -> specKey to specValue }
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
        is Aes<*> -> toOption(v)
        is Pair<*, *> -> listOf(v.first, v.second)
        is PointShape -> v.code
        is NamedLineType -> v.code
        is LineType -> null
        is Mapping -> v.toSpec()
        is MappingAnnotationOptions.AnnotationType -> v.value
        is MappingAnnotationOptions.OrderType -> v.value
        is StatKind -> v.name.lowercase()
        is ThemeOptions.ThemeName -> v.value
        is SummaryStatOptions.AggFunction -> v.value
        else -> v.also {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // analyzer fails to see first check: null -> null
            println("WARNING: standardising unknown type: '${v!!::class.simpleName}'")
        }
    }
}
