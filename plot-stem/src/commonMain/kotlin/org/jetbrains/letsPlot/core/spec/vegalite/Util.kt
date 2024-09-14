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
import kotlin.collections.get

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
                else -> error("Unsupported URL: $url")
            }
            mapOf(Option.Data.VALUES to JsonParser(json).parseJson())
        } else data
        val rows = data.getMaps(Option.Data.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        return columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
    }


    fun transformMappings(encodingVegaSpec: Map<*, *>): Map<Aes<*>, String> {
        val mappings: Map<Aes<*>, String> = encodingVegaSpec
            .mapValues { (_, encoding) -> (encoding as Map<*, *>).getString(Encodings.FIELD) }
            .filterNotNullValues()
            .mapKeys { (channel, _) -> channelToAes(channel) }
        return mappings
    }

    fun channelToAes(channel: Any?) = when (channel) {
        Encodings.Channels.X -> Aes.X
        Encodings.Channels.Y -> Aes.Y
        Encodings.Channels.X2 -> Aes.XEND
        Encodings.Channels.Y2 -> Aes.YEND
        Encodings.Channels.COLOR -> Aes.COLOR
        Encodings.Channels.FILL -> Aes.FILL
        Encodings.Channels.OPACITY -> Aes.ALPHA
        Encodings.Channels.FILL_OPACITY -> error("Unsupported encoding channel: FILL_OPACITY")
        Encodings.Channels.STROKE -> Aes.STROKE
        Encodings.Channels.STROKE_OPACITY -> error("Unsupported encoding channel: STROKE_OPACITY")
        Encodings.Channels.STROKE_WIDTH -> Aes.STROKE
        Encodings.Channels.STROKE_DASH -> error("Unsupported encoding channel: STROKE_DASH")
        Encodings.Channels.SIZE -> Aes.SIZE
        Encodings.Channels.ANGLE -> Aes.ANGLE
        Encodings.Channels.SHAPE -> Aes.SHAPE
        Encodings.Channels.TEXT -> Aes.LABEL
        else -> error("Unsupported encoding channel: $channel")
    }


}