/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.json.JsonParser
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.plotson.DataMetaOptions
import org.jetbrains.letsPlot.core.spec.plotson.MappingAnnotationOptions
import org.jetbrains.letsPlot.core.spec.plotson.SeriesAnnotationOptions
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels

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
        return listOf(Channels.X, Channels.X2, Channels.Y).all(encodingVegaSpec::containsKey)
                && Channels.Y2 !in encodingVegaSpec
    }

    fun transformMappings(
        encodingVegaSpec: Map<*, Map<*, *>>,
        vararg channelMappingOverriding: Pair<String, Aes<*>>
    ): Map<Aes<*>, String> {

        val defaultChannelToAes = mapOf<String, List<Aes<*>>>(
            Channels.X to listOf(Aes.X),
            Channels.Y to listOf(Aes.Y),
            Channels.COLOR to listOf(Aes.COLOR),
            Channels.FILL to listOf(Aes.FILL),
            Channels.OPACITY to listOf(Aes.ALPHA),
            Channels.STROKE to listOf(Aes.STROKE),
            Channels.SIZE to listOf(Aes.SIZE),
            Channels.ANGLE to listOf(Aes.ANGLE),
            Channels.SHAPE to listOf(Aes.SHAPE),
            Channels.TEXT to listOf(Aes.LABEL)
        )

        val overriding = channelMappingOverriding
            .groupBy { (channel, _) -> channel }
            .mapValues { (_, channelToAes) -> channelToAes.map { (_, aes) -> aes } }

        val channelToAesConverter = defaultChannelToAes + overriding

        return encodingVegaSpec
            .flatMap { (channel, encoding) ->
                val aesthetics = channelToAesConverter[channel.toString()] ?: return@flatMap emptyList()
                val field = encoding.getString(Encodings.FIELD) ?: return@flatMap emptyList()

                aesthetics.map { aes -> aes to field }
            }.toMap()

    }

    fun transformDataMeta(
        encodingVegaSpec: Map<*, Map<*, *>>,
    ): DataMetaOptions {
        encodingVegaSpec.entries.forEach { (channel, encoding) ->
            val type = encoding.getString(Encodings.TYPE) ?: return@forEach
            val field = encoding.getString(Encodings.FIELD) ?: return@forEach
            val seriesAnnotationOptions = SeriesAnnotationOptions()
            val mappingAnnotationOptions = MappingAnnotationOptions()
        }

        return DataMetaOptions()
    }
}
