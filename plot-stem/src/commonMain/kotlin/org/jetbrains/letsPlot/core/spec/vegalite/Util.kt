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

    fun transformData(vegaData: Map<String, Any?>): Map<String, List<Any?>> {
        val data = if (Option.Data.URL in vegaData) {
            val url = vegaData.getString(Option.Data.URL) ?: error("URL is not specified")
            val json = when (url) {
                "data/penguins.json" -> Penguins.json
                "data/cars.json" -> Cars.json
                else -> error("Unsupported URL: $url")
            }
            mapOf(Option.Data.VALUES to JsonParser(json).parseJson())
        } else vegaData
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
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ): Map<Aes<*>, String> {
        return encodingVegaSpec
            .flatMap { (channel, encoding) ->
                val aesthetics = channelToAes(channel.toString(), customChannelMapping)
                val field = encoding.getString(Encodings.FIELD) ?: return@flatMap emptyList()

                aesthetics.map { aes -> aes to field }
            }.toMap()
    }

    private fun channelToAes(
        channel: String,
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ): List<Aes<*>> {
        val defaultChannelMapping = listOf(
            Channels.X to Aes.X,
            Channels.Y to Aes.Y,
            Channels.COLOR to Aes.COLOR,
            Channels.FILL to Aes.FILL,
            Channels.OPACITY to Aes.ALPHA,
            Channels.STROKE to Aes.STROKE,
            Channels.SIZE to Aes.SIZE,
            Channels.ANGLE to Aes.ANGLE,
            Channels.SHAPE to Aes.SHAPE,
            Channels.TEXT to Aes.LABEL
        ).groupBy { (ch, _) -> ch }

        @Suppress("NAME_SHADOWING")
        val customChannelMapping = customChannelMapping.groupBy { (ch, _) -> ch }

        val channelToAes = (defaultChannelMapping + customChannelMapping) // custom mappings override default ones
            .mapValues { (_, mappings) -> mappings.map { (_, aes) -> aes } }

        return channelToAes[channel] ?: emptyList()
    }

    // Data should be in columnar format, not in Vega format, i.e., a list of values rather than a list of objects.
    fun transformDataMeta(
        ownData: Map<String, List<Any?>>?,
        commonData: Map<String, List<Any?>>?,
        encodingVegaSpec: Map<*, Map<*, *>>,
        customChannelMapping: List<Pair<String, Aes<*>>>
    ): DataMetaOptions {
        val dataMeta = DataMetaOptions()

        encodingVegaSpec.entries.forEach { entry ->
            val ch = entry.key as? String ?: return@forEach
            val encoding = entry.value

            if (ch == Channels.X2 || ch == Channels.Y2) {
                // secondary channels in vega-lite don't affect axis type
                // Yet need to check sorting and other options - they may affect series_meta or mapping_meta
                return@forEach
            }

            val encField = encoding.getString(Encodings.FIELD) ?: return@forEach
            val encType = encoding[Encodings.TYPE] ?: when {
                Encodings.TIMEUNIT in encoding -> Encodings.Types.QUANTITATIVE
                Encodings.BIN in encoding -> Encodings.Types.QUANTITATIVE
                Encodings.AGGREGATE in encoding -> Encodings.Types.QUANTITATIVE
                else -> Encodings.Types.NOMINAL
            }

            when (encType) {
                Encodings.Types.QUANTITATIVE -> {
                    // lp already treats data as continuous by default
                }

                Encodings.Types.TEMPORAL -> {
                    dataMeta.appendSeriesAnnotation {
                        type = SeriesAnnotationOptions.Types.DATE_TIME
                        column = encField
                    }
                }

                Encodings.Types.NOMINAL, Encodings.Types.ORDINAL -> {
                    if(ownData?.get(encField)?.all { it is String } == true
                        || commonData?.get(encField)?.all { it is String } == true
                        ) {
                        // lp treats strings as discrete by default
                        // No need to add annotation
                        return@forEach
                    }

                    channelToAes(ch, customChannelMapping)
                        .forEach {
                            dataMeta.appendMappingAnnotation {
                                aes = it
                                annotation = MappingAnnotationOptions.AnnotationType.AS_DISCRETE
                                parameters {
                                    label = encField
                                    order = MappingAnnotationOptions.OrderType.ASCENDING
                                }
                            }
                        }
                }
            }
        }

        return dataMeta
    }
}
