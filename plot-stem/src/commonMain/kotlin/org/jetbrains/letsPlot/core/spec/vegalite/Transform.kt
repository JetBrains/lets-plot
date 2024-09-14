/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.LayerOptions
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.OptionsUtil.toSpec
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.PlotOptions
import org.jetbrains.letsPlot.core.spec.getDouble
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings

internal object Transform {
    fun transform(spec: MutableMap<String, Any>): MutableMap<String, Any> {
        val plotOptions = PlotOptions()

        when (VegaConfig.getPlotKind(spec)) {
            VegaPlotKind.SINGLE_LAYER -> processLayerSpec(spec, plotOptions)
            VegaPlotKind.MULTI_LAYER -> {
                spec.getMap(Option.DATA)?.let { plotOptions.data = transformData(it) }
                spec.getMap(Encodings.ENCODING)?.let { processPlotEncoding(it, plotOptions) }
                spec.getMaps(Option.LAYER)!!.forEach { layerSpec -> processLayerSpec(layerSpec, plotOptions) }
            }
            VegaPlotKind.FACETED -> error("Not implemented - faceted plot")
        }

        return toSpec(plotOptions)
    }

    private fun processLayerSpec(layerSpec: Map<*, *>, plotOptions: PlotOptions) {
        val layer = LayerOptions()
        runCatching {
            layerSpec.getMap(Option.DATA)?.let { layer.data = transformData(it) }
            layerSpec[Option.MARK]?.let { processMark(it, layer) }
            layerSpec.getMap(Encodings.ENCODING)?.let { processLayerEncoding(it, layer) }

            plotOptions.layerOptions = (plotOptions.layerOptions ?: emptyList()) + layer
        }.onFailure { e -> println("Failed to process layer spec: $layerSpec\n$e") }
    }

    private fun processPlotEncoding(
        encodingSpec: Map<*, *>,
        options: PlotOptions
    ) {
        val mappings: Map<Aes<*>, String> = encodingSpec
            .mapValues { (_, encoding) -> (encoding as Map<*, *>).getString(Encodings.FIELD) }
            .filterNotNullValues()
            .mapKeys { (channel, _) -> channelToAes(channel) }
        options.mappings = mappings
    }

    private fun processLayerEncoding(
        encodingSpec: Map<*, *>,
        options: LayerOptions
    ) {
        val mappings: Map<Aes<*>, String> = encodingSpec
            .mapValues { (_, encoding) -> (encoding as Map<*, *>).getString(Encodings.FIELD) }
            .filterNotNullValues()
            .mapKeys { (channel, _) -> channelToAes(channel) }
        options.mappings = mappings
    }

    private fun processMark(
        spec: Any,
        options: LayerOptions
    ) {
        val spec = when (spec) {
            is String -> mapOf(Option.Mark.TYPE to spec)
            is Map<*, *> -> spec
            else -> error("Unsupported mark spec: $spec")
        }

        val mark = spec.getString(Option.Mark.TYPE) ?: error("Mark type is not specified")
        options.geom = transformGeomKind(mark)
        options.width = spec.getDouble(Option.Mark.WIDTH, Option.Mark.Width.BAND)
    }

    private fun transformGeomKind(markerType: String): GeomKind {
        return when (markerType) {
            Option.Mark.Types.POINT -> GeomKind.POINT
            Option.Mark.Types.LINE -> GeomKind.LINE
            Option.Mark.Types.BAR -> GeomKind.BAR
            else -> error("Unsupported mark type: $markerType")
        }
    }

    private fun transformData(data: Map<*, *>): Map<String, List<Any?>> {
        val rows = data.getMaps(Option.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        val data = columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
        return data
    }

    private fun transformConstants(markerType: String, encoding: Map<*, Map<*, *>>): Map<Aes<*>, Any?> {
        val values = encoding.filter { (_, encoding) -> Encodings.VALUE in encoding }
        return values.mapValues { (channel, encoding) ->
            when (markerType) {
                // Customize constant values for different mark types
                Option.Mark.Types.BAR -> when (channel) {
                    Encodings.Channels.SIZE -> encoding.getDouble(Encodings.VALUE)
                    else -> encoding[Encodings.VALUE]
                }

                else -> encoding[Encodings.VALUE]
            }
        }.mapKeys { (channel, _) -> channelToAes(channel) }
    }

    private fun channelToAes(channel: Any?) = when (channel) {
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
