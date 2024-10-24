/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.CoordOptions.CoordName.CARTESIAN
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Scale
import org.jetbrains.letsPlot.core.spec.vegalite.data.*

internal object Util {
    internal fun readMark(spec: Any): Pair<String, Map<*, *>> {
        val options = when (spec) {
            is String -> mapOf(VegaOption.Mark.TYPE to spec)
            is Map<*, *> -> spec
            else -> error("Unsupported mark spec: $spec")
        }

        val mark = options.getString(VegaOption.Mark.TYPE) ?: error("Mark type is not specified")
        return Pair(mark, options)
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

    fun iHorizontal(encodingVegaSpec: Properties): Boolean {
        return listOf(Channel.X, Channel.X2, Channel.Y).all(encodingVegaSpec::contains)
                && Channel.Y2 !in encodingVegaSpec
    }

    fun isContinuous(channel: String, encoding: Properties): Boolean {
        val channelEncoding = encoding.getMap(channel) ?: return false
        if (channelEncoding[Encoding.TYPE] == Encoding.Types.QUANTITATIVE) return true
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
        encoding: Properties,
        customChannelMapping: List<Pair<String, Aes<*>>> = emptyList()
    ): Mapping {
        val groupingVar = encoding.getString(Channel.DETAIL, Encoding.FIELD)
        return encoding
            .map { (channel, encoding) ->
                channel as String
                encoding as Properties
                val field = encoding.getString(Encoding.FIELD) ?: return@map emptyList()
                val aesthetics = channelToAes(channel, customChannelMapping)
                aesthetics.map { aes -> aes to field}
            }
            .flatten()
            .fold(Mapping(groupingVar)) { mapping, (aes, field) -> mapping + (aes to field) }
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
        encodingVegaSpec: Properties,
        customChannelMapping: List<Pair<String, Aes<*>>>
    ): DataMetaOptions {
        val dataMeta = DataMetaOptions()

        encodingVegaSpec.entries.forEach { (channel, encoding) ->
            require(encoding is Properties)
            if (channel !is String) return@forEach

            if (channel == Channel.X2 || channel == Channel.Y2) {
                // secondary channels in vega-lite don't affect axis type
                // Yet need to check sorting and other options - they may affect series_meta or mapping_meta
                return@forEach
            }

            val field = encoding.getString(Encoding.FIELD) ?: return@forEach
            if (encoding[Encoding.TYPE] == Encoding.Types.TEMPORAL
                || encoding.contains(Encoding.TIMEUNIT)
            ) {
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

    fun transformPositionAdjust(encodings: Properties, stat: StatOptions?): PositionOptions? {
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
            val xStackDefinition = encodings.getMap(Channel.X)?.getAny(Encoding.STACK)
            val yStackDefinition = encodings.getMap(Channel.Y)?.getAny(Encoding.STACK)

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

    fun transformCoordinateSystem(encoding: Properties, plotOptions: PlotOptions) {
        fun domain(channel: String): Pair<Double?, Double?> {
            val domainMin = encoding.getMap(channel)?.getMap(Encoding.SCALE)?.getNumber(Scale.DOMAIN_MIN)
            val domainMax = encoding.getMap(channel)?.getMap(Encoding.SCALE)?.getNumber(Scale.DOMAIN_MAX)
            val domain = encoding.getMap(channel)?.getMap(Encoding.SCALE)?.getList(Scale.DOMAIN)

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
