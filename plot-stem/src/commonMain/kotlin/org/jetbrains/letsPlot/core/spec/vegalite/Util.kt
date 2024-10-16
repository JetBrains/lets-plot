/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.CoordOptions.CoordName.CARTESIAN
import org.jetbrains.letsPlot.core.spec.plotson.SummaryStatOptions.AggFunction
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.FIELD
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Property
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Scale
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
                "data/stocks.csv" -> Stocks.json
                else -> error("Unsupported URL: $url")
            }
            mapOf(Option.Data.VALUES to JsonSupport.parse(json))
        } else vegaData
        val rows = data.getMaps(Option.Data.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        return columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
    }

    fun iHorizontal(encodingVegaSpec: Map<*, *>): Boolean {
        return listOf(Channel.X, Channel.X2, Channel.Y).all(encodingVegaSpec::containsKey)
                && Channel.Y2 !in encodingVegaSpec
    }

    fun isContinuous(channel: String, encoding: Map<*, Map<*, *>>): Boolean {
        val channelEncoding = encoding[channel] ?: return false
        if (channelEncoding[Property.TYPE] == Encoding.Types.QUANTITATIVE) return true
        if (channelEncoding[Property.TYPE] == Encoding.Types.ORDINAL) return false
        if (channelEncoding[Property.TYPE] == Encoding.Types.NOMINAL) return false
        if (channelEncoding.contains(Property.BIN)) return true
        if (channelEncoding.contains(Property.TIMEUNIT)) return true
        when (channelEncoding.getString(Property.AGGREGATE)) {
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
        val groupingVar = encodingVegaSpec.getString(Channel.DETAIL, FIELD)
        return encodingVegaSpec
            .flatMap { (channel, encoding) ->
                val aesthetics = channelToAes(channel.toString(), customChannelMapping)
                val field = encoding.getString(FIELD) ?: return@flatMap emptyList()

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

    fun LayerOptions.applyConstants(markSpec: Map<*, *>, customChannelMapping: List<Pair<String, Aes<*>>>) {
        markSpec.forEach { (prop, value) ->
            channelToAes(prop.toString(), customChannelMapping)
                .forEach { aes ->
                    const(aes, value)
                }
        }
    }

    // Data should be in columnar format, not in Vega format, i.e., a list of values rather than a list of objects.
    fun transformDataMeta(
        data: Map<String, List<Any?>>?,
        encodingVegaSpec: Map<*, Map<*, *>>,
        customChannelMapping: List<Pair<String, Aes<*>>>
    ): DataMetaOptions {
        val dataMeta = DataMetaOptions()

        encodingVegaSpec.entries.forEach { entry ->
            val ch = entry.key as? String ?: return@forEach
            val channelEncoding = entry.value

            if (ch == Channel.X2 || ch == Channel.Y2) {
                // secondary channels in vega-lite don't affect axis type
                // Yet need to check sorting and other options - they may affect series_meta or mapping_meta
                return@forEach
            }

            val encField = channelEncoding.getString(FIELD) ?: return@forEach
            if (channelEncoding[Property.TYPE] == Encoding.Types.TEMPORAL
                || channelEncoding.contains(Property.TIMEUNIT)
            ) {
                dataMeta.appendSeriesAnnotation {
                    type = SeriesAnnotationOptions.Types.DATE_TIME
                    column = encField
                }
            }


            if (!isContinuous(ch, encodingVegaSpec)) {
                if (data?.get(encField)?.all { it is String } != true) {
                    // lp treats strings as discrete by default
                    // No need to add annotation

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

    fun LayerOptions.transformStat(encodings: Map<*, Map<*, *>>): Boolean {
        val xAggregate = encodings.getString(Channel.X, Property.AGGREGATE)
        val yAggregate = encodings.getString(Channel.Y, Property.AGGREGATE)

        if (xAggregate != null && yAggregate != null) {
            error("Both x and y aggregates are not supported")
        }

        val aggregate = xAggregate ?: yAggregate ?: return false

        when (aggregate) {
            Encoding.Aggregate.COUNT -> countStat()
            Encoding.Aggregate.SUM -> summaryStat { f = AggFunction.SUM }
            Encoding.Aggregate.MEAN -> summaryStat { f = AggFunction.MEAN }
            else -> error("Unsupported aggregate function: $aggregate")
        }

        return true
    }

    fun transformPositionAdjust(encodings: Map<*, Map<*, *>>): PositionOptions? {
        if (encodings.getString(Channel.X_OFFSET, FIELD) != null
            || encodings.getString(Channel.Y_OFFSET, FIELD) != null
        ) {
            // Lots of false positives here:
            // - check is the field is used as grouping variable
            // - check is plot direction matches the offset direction
            // But I don't see any other use cases for offset encoding other than dodging
            return dodge()
        }

        if (!encodings.has(Channel.X, Property.STACK)
            && !encodings.has(Channel.Y, Property.STACK)
        ) {
            return null
        }

        val stack = encodings.getString(Channel.X, Property.STACK)
            ?: encodings.getString(Channel.Y, Property.STACK)

        return when (stack) {
            null -> identity()
            Encoding.Stack.ZERO -> stack()
            Encoding.Stack.NORMALIZE -> fill()
            else -> error("Unsupported stack type: $stack")
        }
    }

    fun transformCoordinateSystem(encoding: Map<*, Map<*, *>>, plotOptions: PlotOptions) {
        fun domain(channel: String): Pair<Double?, Double?> {
            val domainMin = encoding.getNumber(channel, Property.SCALE, Scale.DOMAIN_MIN)
            val domainMax = encoding.getNumber(channel, Property.SCALE, Scale.DOMAIN_MAX)
            val domain = encoding.getList(channel, Property.SCALE, Scale.DOMAIN)

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

        if (newXDomain != null || newYDomain != null) {
            if (plotOptions.coord == null) {
                plotOptions.coord = coord {
                    name = CARTESIAN
                }
            }

            plotOptions.coord!!.apply {
                xLim = newXDomain
                yLim = newYDomain
            }
        }
    }
}
