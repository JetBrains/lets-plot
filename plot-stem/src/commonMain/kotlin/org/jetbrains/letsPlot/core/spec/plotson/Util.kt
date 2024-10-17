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
import org.jetbrains.letsPlot.core.spec.typed

fun PlotOptions.toJson(): MutableMap<String, Any> {
    return toJson(properties)
}

internal fun toJson(v: Any?): Any? {
    return when (v) {
        null -> null
        is Options -> toJson(v.toSpec())
        is List<*> -> v.map(::toJson)
        is Map<*, *> -> toJson(v)
        else -> standardise(v)
    }
}

private fun toJson(obj: Map<*, *>): MutableMap<String, Any> {
    val map = mutableMapOf<String, Any>()
    obj.forEach { (key, v) ->
        val specKey = standardise(key) as? String ?: error("Map key must be a string, but was $key::class.simpleName")
        val specValue = toJson(v) ?: return@forEach

        if (v is InlineOptions && specValue is Map<*, *>) {
            map += specValue.typed(strict = true)
        }else {
            map[specKey] = specValue
        }
    }
    return map
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
        is CoordOptions.CoordName -> v.value
        is SummaryStatOptions.AggFunction -> v.value
        is PositionOptions.PosKind -> v.value
        is SeriesAnnotationOptions.Types -> v.value
        else -> v.also {
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // analyzer fails to see first check: null -> null
            println("WARNING: standardising unknown type: '${v!!::class.simpleName}'")
        }
    }
}
