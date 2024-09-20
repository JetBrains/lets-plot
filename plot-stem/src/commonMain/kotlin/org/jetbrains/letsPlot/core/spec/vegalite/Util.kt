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
                && listOf(
            Encodings.Channels.X,
            Encodings.Channels.X2,
            Encodings.Channels.Y
        ).all(encodingVegaSpec::containsKey)
    }

    fun transformMappings(
        encodingVegaSpec: Map<*, Map<*, *>>,
        vararg channelMappingOverriding: Pair<String, Aes<*>>
    ): Map<Aes<*>, String> {

        val defaultChannelToAesConverter = mutableMapOf<String, MutableList<Aes<*>>>(
            Encodings.Channels.X to mutableListOf(Aes.X),
            Encodings.Channels.Y to mutableListOf(Aes.Y),
            Encodings.Channels.COLOR to mutableListOf(Aes.COLOR),
            Encodings.Channels.FILL to mutableListOf(Aes.FILL),
            Encodings.Channels.OPACITY to mutableListOf(Aes.ALPHA),
            Encodings.Channels.STROKE to mutableListOf(Aes.STROKE),
            Encodings.Channels.SIZE to mutableListOf(Aes.SIZE),
            Encodings.Channels.ANGLE to mutableListOf(Aes.ANGLE),
            Encodings.Channels.SHAPE to mutableListOf(Aes.SHAPE),
            Encodings.Channels.TEXT to mutableListOf(Aes.LABEL)
        )

        val overriding = channelMappingOverriding.fold(mutableMapOf<String, MutableList<Aes<*>>>()) { acc, (channel, aes) ->
            acc.getOrPut(channel, ::mutableListOf) += aes
            acc
        }

        //defaultChannelToAesConverter.keys.removeAll { it in overriding }

        //overriding.values.flatten().forEach { defaultChannelToAesConverter.values.forEach { l -> l.remove(it) } }

        val channelToAesConverter = defaultChannelToAesConverter + overriding

        val channelToField = encodingVegaSpec
            .mapValues { (_, encoding) -> encoding.getString(Encodings.FIELD) }
            .filterNotNullValues()

        val convertableAesthetics = channelToAesConverter.values.flatten()
        val aestheticsToChannelConverter =
            convertableAesthetics.associateWith { aes -> channelToAesConverter.filterValues { aes in it }.keys.last() }
        val encodedAesthetics =
            aestheticsToChannelConverter.mapValues { (aes, channel) -> channelToField[channel] }.filterNotNullValues()

        // validation
        if ((channelToField.keys - channelToAesConverter.keys).isNotEmpty()) {
            error("Error: unsupported channels: ${channelToField.keys - channelToAesConverter.keys}")
        }

        val aggregates = encodingVegaSpec.filter { (_, encoding) -> Encodings.AGGREGATE in encoding }
        if (aggregates.isNotEmpty()) {
            //error("Error: unsupported aggregate functions: $aggregates")
        }

        return encodedAesthetics
    }
}
