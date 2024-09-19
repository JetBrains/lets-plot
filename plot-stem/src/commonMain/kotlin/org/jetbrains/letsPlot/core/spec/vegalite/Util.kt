/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.intern.json.JsonParser
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings

internal object Util {
    internal fun readMark(spec: Any): Pair<String, Map<*, *>> {
        val options = when (spec) {
            is String -> mapOf(Option.Mark.TYPE to spec)
            is Map<*, *> -> spec
            else -> error("Unsupported mark spec: $spec")
        }

        val mark = options.getString(Option.Mark.TYPE) ?: error("Mark type is not specified")
        return Pair(mark, options)
    }

    fun transformData(data: Map<*, *>): Map<String, List<Any?>> {
        val data = if (Option.Data.URL in data) {
            val url = data.getString(Option.Data.URL) ?: error("URL is not specified")
            val json = when (url) {
                "data/penguins.json" -> Penguins.json
                "data/cars.json" -> Cars.json
                else -> error("Unsupported URL: $url")
            }
            mapOf(Option.Data.VALUES to JsonParser(json).parseJson())
        } else data
        val rows = data.getMaps(Option.Data.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        return columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
    }

    fun iHorizontal(encodingVegaSpec: Map<*, *>): Boolean {
        return Encodings.Channels.Y2 !in encodingVegaSpec
                && listOf(Encodings.Channels.X, Encodings.Channels.X2, Encodings.Channels.Y).all(encodingVegaSpec::containsKey)
    }

    fun transformMappings(encodingVegaSpec: Map<*, Map<*, *>>, customChannelMapping: Map<String, Aes<*>> = emptyMap()): Map<Aes<*>, String> {
        val channelMapping = mapOf(
            Encodings.Channels.X to Aes.X,
            Encodings.Channels.Y to Aes.Y,
            Encodings.Channels.COLOR to Aes.COLOR,
            Encodings.Channels.FILL to Aes.FILL,
            Encodings.Channels.OPACITY to Aes.ALPHA,
            Encodings.Channels.STROKE to Aes.STROKE,
            Encodings.Channels.SIZE to Aes.SIZE,
            Encodings.Channels.ANGLE to Aes.ANGLE,
            Encodings.Channels.SHAPE to Aes.SHAPE,
            Encodings.Channels.TEXT to Aes.LABEL
        ) + customChannelMapping

        val channelEncoding = encodingVegaSpec
            .mapValues { (_, encoding) -> encoding.getString(Encodings.FIELD) }
            .filterNotNullValues()

        val unsupportedChannels = channelEncoding.keys - channelMapping.keys
        if (unsupportedChannels.isNotEmpty()) {
            error("Error: unsupported channels: $unsupportedChannels")
        }

        val aggregates = encodingVegaSpec.filter { (_, encoding) -> Encodings.AGGREGATE in encoding }
        if (aggregates.isNotEmpty()) {
            //error("Error: unsupported aggregate functions: $aggregates")
        }

        val mappings: Map<Aes<*>, String> = (channelEncoding - unsupportedChannels)
            .mapKeys { (channel, _) -> channelMapping[channel]!! }
        return mappings
    }
}
