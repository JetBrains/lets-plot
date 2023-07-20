/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.corr

import org.jetbrains.letsPlot.commons.values.Color

object DataUtil {

    fun standardiseData(rawData: Map<*, *>): Map<String, List<Any?>> {
        val standardisedData = LinkedHashMap<String, List<Any?>>()
        for ((rawKey, rawValue) in rawData) {
            val key = rawKey.toString()
            standardisedData[key] = toList(key, rawValue!!)
        }
        return standardisedData
    }

    fun toList(key: String, rawValue: Any): List<Any?> {
        return when (rawValue) {
            is List<*> -> standardizeList(rawValue)
            is Iterable<*> -> standardizeIterable(rawValue).toList()
            is Sequence<*> -> standardizeIterable(rawValue.asIterable()).toList()
            is Array<*> -> standardizeList(rawValue.asList())
            is ByteArray -> standardizeList(rawValue.asList())
            is ShortArray -> standardizeList(rawValue.asList())
            is IntArray -> standardizeList(rawValue.asList())
            is LongArray -> standardizeList(rawValue.asList())
            is FloatArray -> standardizeList(rawValue.asList())
            is DoubleArray -> standardizeList(rawValue.asList())
            is CharArray -> standardizeList(rawValue.asList())
            is Pair<*, *> -> standardizeList(rawValue.toList())
            else -> throw IllegalArgumentException("Can't transform data[\"$key\"] of type ${rawValue::class.simpleName} to a list")
        }
    }

    private fun standardizeList(series: List<*>): List<*> {
        // avoid 'toList' on lists (makes copy)
        return standardizeIterable(series) as List<*>
    }

    private fun standardizeIterable(series: Iterable<*>): Iterable<*> {
        fun noTimeZoneError(time: Any): Nothing {
            throw IllegalArgumentException(
                "Can't convert ${time::class.simpleName} to the number of milliseconds from the epoch of 1970-01-01T00:00:00Z."
            )
        }

        fun toDouble(n: Number): Double? {
            return when (n) {
                is Float -> if (n.isFinite()) n.toDouble() else null
                is Double -> if (n.isFinite()) n else null
                else -> n.toDouble()
            }
        }
        return if (needToStandardizeValues(series)) {
            series.map {
                when (it) {
                    null -> it
                    is String -> it
                    is Number -> toDouble(it)
                    is Char -> it.toString()
                    is Color -> it.toHexColor()
                    else -> throw IllegalArgumentException("Can't standardize the value \"$it\" of type ${it::class.simpleName} as a string, number or date-time.")
                }
            }
        } else {
            series
        }
    }

    private fun needToStandardizeValues(series: Iterable<*>): Boolean {
        return series.any {
            it != null &&
                    (!(it is String || it is Double) ||
                            it is Double && !it.isFinite())
        }
    }
}
