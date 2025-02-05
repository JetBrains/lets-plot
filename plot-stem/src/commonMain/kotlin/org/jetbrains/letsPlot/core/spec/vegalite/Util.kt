/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZone.Companion.UTC
import org.jetbrains.letsPlot.commons.intern.filterNotNullKeys
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.CoordOptions.CoordName
import org.jetbrains.letsPlot.core.spec.plotson.CoordOptions.CoordName.CARTESIAN
import org.jetbrains.letsPlot.core.spec.plotson.GuideOptions.Companion.guide
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.ENCODING
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channels
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Scale
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.TimeUnit
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.VALUE
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Title
import org.jetbrains.letsPlot.core.spec.vegalite.data.*

internal object Util {
    fun getChannelDefinitions(encoding: Map<*, *>): Map<String, Map<*, *>> {
        return Channels
            .filter(encoding::containsKey)
            .associateWith { channel -> encoding.getMap(channel) ?: emptyMap() }
    }

    internal fun readMark(spec: Any): Pair<String, Map<*, *>> {
        val options = when (spec) {
            is String -> mapOf(VegaOption.Mark.TYPE to spec)
            is Map<*, *> -> spec
            else -> error("Unsupported mark spec: $spec")
        }

        val mark = options.getString(VegaOption.Mark.TYPE) ?: error("Mark type is not specified")
        return Pair(mark, options)
    }

    fun transformTitle(vegaTitle: Any?): TitleOptions? {
        return when (vegaTitle) {
            is String -> title {
                titleText = vegaTitle
            }

            is Map<*, *> -> title {
                titleText = vegaTitle.getString(Title.TEXT)
                subtitleText = vegaTitle.getString(Title.SUBTITLE)
            }

            else -> null
        }
    }

    fun transformData(vegaData: Map<String, Any>): Map<String, List<Any?>> {
        val data = if (VegaOption.Data.URL in vegaData) {
            val url = vegaData.getString(VegaOption.Data.URL) ?: error("URL is not specified")
            val json = when (url) {
                "data/penguins.json" -> Penguins.json
                "data/cars.json" -> Cars.json
                "data/seattle-weather.csv" -> SeattleWeather.json
                "data/population.json" -> Population.json
                "data/barley.json" -> Barley.json
                "data/stocks.csv" -> Stocks.json
                else -> error("Unsupported URL: $url")
            }
            mapOf(VegaOption.Data.VALUES to JsonSupport.parse(json))
        } else vegaData
        val rows = data.getMaps(VegaOption.Data.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        return columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
    }

    fun iHorizontal(encodingVegaSpec: Map<*, *>): Boolean {
        return listOf(Channel.X, Channel.X2, Channel.Y).all(encodingVegaSpec::contains)
                && Channel.Y2 !in encodingVegaSpec
    }

    fun isContinuous(channel: String, encoding: Map<*, *>): Boolean {
        val channelEncoding = encoding.getMap(channel) ?: return false
        if (channel == Channel.LONGITUDE) return true
        if (channel == Channel.LATITUDE) return true
        if (channelEncoding[Encoding.TYPE] == Encoding.Types.QUANTITATIVE) return true
        if (channelEncoding[Encoding.TYPE] == Encoding.Types.TEMPORAL) return true
        if (channelEncoding[Encoding.TYPE] == Encoding.Types.ORDINAL) return false
        if (channelEncoding[Encoding.TYPE] == Encoding.Types.NOMINAL) return false
        if (channelEncoding.contains(Encoding.BIN)) return true
        if (channelEncoding.contains(Encoding.TIMEUNIT)) return true
        when (channelEncoding.getString(Encoding.AGGREGATE)) {
            null -> {} // continue checking
            Encoding.Aggregate.ARGMAX, Encoding.Aggregate.ARGMIN -> return false
            else -> return true
        }

        return false
    }

    // Convert from channel to field  to aes to variable
    // Aggregate and other transforms are already applied and not considered here
    fun transformMappings(
        encoding: Map<*, *>,
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ): Mapping {
        val groupingVar = encoding.getString(Channel.DETAIL, Encoding.FIELD)

        return Channels.map { channel ->
            val field = encoding.getString(channel, Encoding.FIELD) ?: return@map emptyList()
            val aesthetics = channelToAes(channel, customChannelMapping)
            aesthetics.map { aes -> aes to field }
        }
            .flatten()
            .fold(Mapping(groupingVar)) { mapping, (aes, field) -> mapping + (aes to field) }
    }

    fun transformPlotGuides(
        plotGuides: Map<Aes<*>, GuideOptions>?,
        encoding: Map<*, *>,
        customChannelMapping: List<Pair<String, Aes<*>>>
    ): Map<Aes<*>, GuideOptions>? {
        val generatedTitles = run {
            val titleByAes = getChannelDefinitions(encoding)
                .mapKeys { (channel, _) -> channelToAes(channel, customChannelMapping).firstOrNull() }
                .mapValues { (_, definition) -> definition.getString(Encoding.TITLE) }
                .filterNotNullKeys()
                .filterNotNullValues()

            if (titleByAes.isEmpty()) return plotGuides

            // fix cases when single channel is mapped to multiple aesthetics
            // E.g., COLOR mapped to fill/color, but only fill has a title.
            // LP will build two guides, because one have a title and another doesn't.
            // To merge them into one guide we need give explicit names to all aesthetics.

            val colorTitle = titleByAes[Aes.COLOR]
            val fillTitle = titleByAes[Aes.FILL]

            val dupColorTitle = when {
                colorTitle != null && fillTitle == null -> mapOf(Aes.FILL to colorTitle)
                colorTitle == null && fillTitle != null -> mapOf(Aes.COLOR to fillTitle)
                else -> emptyMap()
            }

            // Titles set by user override generated titles
            titleByAes + dupColorTitle
        }

        val mergedGuides = generatedTitles.mapValues { guide { } } + (plotGuides ?: emptyMap())

        mergedGuides.forEach { (aes, guide) ->
            guide.title = generatedTitles[aes]
        }

        return mergedGuides
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
            Channel.TEXT to Aes.LABEL,
            Channel.LONGITUDE to Aes.X,
            Channel.LATITUDE to Aes.Y,
        ).groupBy { (ch, _) -> ch }

        @Suppress("NAME_SHADOWING")
        val customChannelMapping = customChannelMapping.groupBy { (ch, _) -> ch }

        val channelToAes = (defaultChannelMapping + customChannelMapping) // custom mappings override default ones
            .mapValues { (_, mappings) -> mappings.map { (_, aes) -> aes } }

        return channelToAes[channel] ?: emptyList()
    }

    fun LayerOptions.applyConstants(
        layerSpec: Map<*, *>,
        customChannelMapping: List<Pair<String, Aes<*>>>,
        mapping: Mapping
    ) {
        fun readChannel(map: Map<*, *>, vararg path: Any): Map<Aes<*>, Any> {
            return Channels
                .filter { channel -> map.has(channel, *path) }
                .map { channel -> channelToAes(channel, customChannelMapping) to map.read(channel, *path)!! }
                .flatMap { (aesthetics, value) -> aesthetics.map { aes -> aes to value } }
                .toMap()
        }

        val markSpec = layerSpec.getMap(VegaOption.MARK) ?: emptyMap()
        val markChannelProps = readChannel(markSpec)

        val encoding = layerSpec.getMap(ENCODING) ?: emptyMap()
        val encodingChannelValues = readChannel(encoding, VALUE)

        val constants = (markChannelProps + encodingChannelValues - mapping.aesthetics.keys)

        constants.forEach { (aes, value) -> const(aes, value) }
    }

    // Data should be in columnar format, not in Vega format, i.e., a list of values rather than a list of objects.
    fun transformDataMeta(
        data: Map<String, List<Any?>>?,
        encodingVegaSpec: Map<*, *>,
        customChannelMapping: List<Pair<String, Aes<*>>>
    ): DataMetaOptions {
        val dataMeta = DataMetaOptions()

        Channels.forEach { channel ->
            val encoding = encodingVegaSpec.getMap(channel) ?: return@forEach
            // as? Map<*, *> ?: return@forEach
            if (channel == Channel.X2 || channel == Channel.Y2) {
                // secondary channels in vega-lite don't affect axis type
                // Yet need to check sorting and other options - they may affect series_meta or mapping_meta
                return@forEach
            }

            val field = encoding.getString(Encoding.FIELD) ?: return@forEach
            if (encoding[Encoding.TYPE] == Encoding.Types.TEMPORAL || encoding.contains(Encoding.TIMEUNIT)) {
                dataMeta.appendSeriesAnnotation {
                    type = SeriesAnnotationOptions.Types.DATE_TIME
                    column = field
                }
            }

            if (!isContinuous(channel, encodingVegaSpec)) {
                if (data?.get(field)?.all { it is String } != true) {
                    // lp treats strings as discrete by default
                    // No need to add an annotation

                    channelToAes(channel, customChannelMapping)
                        .forEach {
                            dataMeta.appendMappingAnnotation {
                                aes = it
                                annotation = MappingAnnotationOptions.AnnotationType.AS_DISCRETE
                                parameters {
                                    label = field
                                    order = MappingAnnotationOptions.OrderType.ASCENDING
                                }
                            }
                        }
                }
            }
        }

        return dataMeta
    }

    fun applyTimeUnit(
        data: Map<String, List<Any?>>,
        encodingVegaSpec: Map<*, *>
    ): Map<String, List<Any?>> {
        Channels.forEach { channel ->
            val field = encodingVegaSpec.getString(channel, Encoding.FIELD) ?: return@forEach
            val timeUnit = encodingVegaSpec.getString(channel, Encoding.TIMEUNIT) ?: return@forEach
            val timeSeries = data[field] ?: return@forEach

            val adjustedTimeSeries = timeSeries.map {
                val epoch = (it as? Number) ?: return@map null

                val instant = Instant(epoch.toLong())
                val dateTime = UTC.toDateTime(instant)
                val adjustedDateTime = applyTimeUnit(dateTime, timeUnit)
                UTC.toInstant(adjustedDateTime).timeSinceEpoch
            }

            data.write(field) { adjustedTimeSeries }
        }

        return data
    }


    fun transformPositionAdjust(encodings: Map<*, *>, stat: StatOptions?): PositionOptions? {
        run {
            val xOffsetField = encodings.getString(Channel.X_OFFSET, Encoding.FIELD)
            val yOffsetField = encodings.getString(Channel.Y_OFFSET, Encoding.FIELD)

            if (xOffsetField != null || yOffsetField != null) {
                // Lots of false positives here:
                // - check is the field is used as grouping variable
                // - check is plot direction matches the offset direction
                // But I don't see any other use cases for offset encoding other than dodging
                return dodge()
            }
        }

        run {
            val xStackDefinition = encodings.read(Channel.X, Encoding.STACK)
            val yStackDefinition = encodings.read(Channel.Y, Encoding.STACK)

            // Note that stack=null is a valid option in Vega-Lite for disabling stacking,
            // and it's not the same as missing stack option.
            val hasXStack = encodings.has(Channel.X, Encoding.STACK)
            val hasYStack = encodings.has(Channel.Y, Encoding.STACK)

            val defaultPos = when (stat?.kind) {
                StatKind.DENSITY -> stack()
                else -> null
            }

            val stackChannel = when {
                !hasXStack && !hasYStack -> return defaultPos
                hasXStack && xStackDefinition == null -> return identity()
                hasYStack && yStackDefinition == null -> return identity()
                xStackDefinition != null -> xStackDefinition
                yStackDefinition != null -> yStackDefinition
                else -> return@run
            }

            return when (stackChannel) {
                Encoding.Stack.ZERO -> stack()
                Encoding.Stack.NORMALIZE -> fill()
                else -> error("Unsupported stack type: $stackChannel")
            }
        }

        return null
    }

    fun transformCoordinateSystem(encoding: Map<*, *>, plotOptions: PlotOptions) {
        fun domain(channel: String): Pair<Double?, Double?> {
            val domainMin = encoding.getNumber(channel, Encoding.SCALE, Scale.DOMAIN_MIN)
            val domainMax = encoding.getNumber(channel, Encoding.SCALE, Scale.DOMAIN_MAX)
            val domain = encoding.getList(channel, Encoding.SCALE, Scale.DOMAIN)

            return Pair(
                (domainMin ?: (domain?.getOrNull(0) as? Number))?.toDouble(),
                (domainMax ?: (domain?.getOrNull(1) as? Number))?.toDouble()
            )
        }

        fun union(curDomain: Pair<Double?, Double?>?, newDomain: Pair<Double?, Double?>): Pair<Double?, Double?>? {
            val (curMin, curMax) = curDomain ?: Pair(null, null)
            val (newMin, newMax) = newDomain

            val resultMin = listOfNotNull(curMin, newMin).minOrNull()
            val resultMax = listOfNotNull(curMax, newMax).maxOrNull()

            return if (resultMin != null || resultMax != null) {
                Pair(resultMin, resultMax)
            } else {
                null
            }
        }

        val newXDomain = union(plotOptions.coord?.xLim, domain(Channel.X))
        val newYDomain = union(plotOptions.coord?.yLim, domain(Channel.Y))

        val coordName = when {
            Channel.LONGITUDE in encoding -> CoordName.MAP
            Channel.LATITUDE in encoding -> CoordName.MAP
            else -> null
        }

        if (newXDomain != null || newYDomain != null || coordName != null) {
            plotOptions.coord = (plotOptions.coord ?: CoordOptions()).apply {
                name = coordName ?: CARTESIAN
                xLim = newXDomain
                yLim = newYDomain
            }
        }
    }

    internal fun applyTimeUnit(dateTime: DateTime, unitsTemplate: String): DateTime {
        var year = 0
        var month = Month.JANUARY
        var day = 1
        var hours = 0
        var minutes = 0
        var seconds = 0
        var ms = 0

        if (TimeUnit.YEAR in unitsTemplate) year = dateTime.year
        if (TimeUnit.MONTH in unitsTemplate) month = Month.values()[dateTime.month.ordinal()]
        if (TimeUnit.DAY in unitsTemplate) day = dateTime.day
        if (TimeUnit.HOURS in unitsTemplate) hours = dateTime.time.hours
        if (TimeUnit.MINUTES in unitsTemplate) minutes = dateTime.time.minutes
        if (TimeUnit.SECONDS in unitsTemplate) seconds = dateTime.time.hours
        if (TimeUnit.MILLISECONDS in unitsTemplate) ms = dateTime.time.milliseconds

        return DateTime(
            Date(day = day, month = month, year = year),
            Time(hours = hours, minutes = minutes, seconds = seconds, milliseconds = ms)
        )
    }
}
