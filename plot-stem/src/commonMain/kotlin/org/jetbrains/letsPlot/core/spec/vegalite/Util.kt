/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.json.JsonParser
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.PosProto
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel
import org.jetbrains.letsPlot.core.spec.vegalite.data.*

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
                "data/seattle-weather.csv" -> SeattleWeather.json
                "data/population.json" -> Population.json
                "data/barley.json" -> Barley.json
                else -> error("Unsupported URL: $url")
            }
            mapOf(Option.Data.VALUES to JsonParser(json).parseJson())
        } else vegaData
        val rows = data.getMaps(Option.Data.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        return columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
    }

    fun iHorizontal(encodingVegaSpec: Map<*, *>): Boolean {
        return listOf(Channel.X, Channel.X2, Channel.Y).all(encodingVegaSpec::containsKey)
                && Channel.Y2 !in encodingVegaSpec
    }

    fun isQuantitative(channelEncoding: Map<*, *>): Boolean {
        if (channelEncoding[Encoding.Property.TYPE] == Encoding.Types.QUANTITATIVE) return true
        if (channelEncoding.contains(Encoding.Property.BIN)) return true
        when(channelEncoding.getString(Encoding.Property.AGGREGATE)) {
            null -> {} // continue checking
            Encoding.Aggregate.ARGMAX, Encoding.Aggregate.ARGMIN -> return false
            else -> return true
        }

        return false
    }

    fun transformMappings(
        encodingVegaSpec: Map<*, Map<*, *>>,
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ): Mapping {
        val groupingVar = encodingVegaSpec.getString(Channel.DETAIL, Encoding.FIELD)
        return encodingVegaSpec
            .flatMap { (channel, encoding) ->
                val aesthetics = channelToAes(channel.toString(), customChannelMapping)
                val field = encoding.getString(Encoding.FIELD) ?: return@flatMap emptyList()

                aesthetics.map { aes -> aes to field }
            }.fold(Mapping(groupingVar)) { mapping, (aes, field) -> mapping + (aes to field) }
    }

    private fun channelToAes(
        channel: String,
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ): List<Aes<*>> {
        val defaultChannelMapping = listOf(
            Channel.X to Aes.X,
            Channel.Y to Aes.Y,
            Channel.COLOR to Aes.COLOR,
            Channel.FILL to Aes.FILL,
            Channel.OPACITY to Aes.ALPHA,
            Channel.STROKE to Aes.STROKE,
            Channel.SIZE to Aes.SIZE,
            Channel.ANGLE to Aes.ANGLE,
            Channel.SHAPE to Aes.SHAPE,
            Channel.TEXT to Aes.LABEL
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

            if (ch == Channel.X2 || ch == Channel.Y2) {
                // secondary channels in vega-lite don't affect axis type
                // Yet need to check sorting and other options - they may affect series_meta or mapping_meta
                return@forEach
            }

            val encField = encoding.getString(Encoding.FIELD) ?: return@forEach
            val encType = encoding[Encoding.Property.TYPE] ?: when {
                Encoding.Property.TIMEUNIT in encoding -> Encoding.Types.QUANTITATIVE
                Encoding.Property.BIN in encoding -> Encoding.Types.QUANTITATIVE
                Encoding.Property.AGGREGATE in encoding -> Encoding.Types.QUANTITATIVE
                else -> Encoding.Types.NOMINAL
            }

            when (encType) {
                Encoding.Types.QUANTITATIVE -> {
                    // lp already treats data as continuous by default
                }

                Encoding.Types.TEMPORAL -> {
                    dataMeta.appendSeriesAnnotation {
                        type = SeriesAnnotationOptions.Types.DATE_TIME
                        column = encField
                    }
                }

                Encoding.Types.NOMINAL, Encoding.Types.ORDINAL -> {
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

    fun transformPositionAdjust(encodings: Map<*, Map<*, *>>): PositionOptions? {
        val xStack = encodings.getString(Channel.X, Encoding.Property.STACK)
        val yStack = encodings.getString(Channel.Y, Encoding.Property.STACK)

        val stack = xStack ?: yStack ?: return null

        return when (stack) {
            Encoding.Stack.ZERO -> PositionOptions().apply { name = PosProto.STACK }
            Encoding.Stack.NORMALIZE -> PositionOptions().apply { name = PosProto.FILL }
            else -> error("Unsupported stack type: $stack")
        }
    }
}
