/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.OptionsUtil
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.layer
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.plot
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.getString
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Marks
import kotlin.collections.get

internal object Transform {
    fun transform(spec: MutableMap<String, Any>): MutableMap<String, Any> {
        return when (VegaConfig.getPlotKind(spec)) {
            VegaPlotKind.SINGLE_LAYER -> fromSingleLayerSpec(spec)
            VegaPlotKind.MULTI_LAYER -> error("Not implemented - multi-layer plot")
            VegaPlotKind.FACETED -> error("Not implemented - faceted plot")
        }
    }

    private fun fromSingleLayerSpec(spec: MutableMap<String, Any>): MutableMap<String, Any> {
        val lpSpec = plot {
            data = spec.getMap(Option.DATA)?.let(::transformData)

            layerOptions = listOf(
                layer {
                    geom = spec.getString(Option.MARK)?.let(::transformGeomKind)
                    mappings = spec.getMap(Encodings.ENCODING)?.let(::transformMappings)
                }
            )
        }

        return OptionsUtil.toSpec(lpSpec)
    }

    private fun transformGeomKind(markerType: String): GeomKind {
        return when (markerType) {
            Marks.POINT -> GeomKind.POINT
            Marks.LINE -> GeomKind.LINE
            Marks.BAR -> GeomKind.BAR
            else -> error("Unsupported mark type: $markerType")
        }
    }

    private fun transformData(data: Map<*, *>): Map<String, List<Any?>> {
        val rows = data.getMaps(Option.VALUES) ?: return emptyMap()
        val columnKeys = rows.flatMap { it.keys.filterNotNull() }.distinct().map(Any::toString)
        val data = columnKeys.associateWith { columnKey -> rows.map { row -> row[columnKey] } }
        return data
    }

    private fun transformMappings(encoding: Map<*, *>): Map<Aes<*>, String> {
        return encoding
            .mapKeys { (key, _) ->
                when (key) {
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
                    else -> error("Unsupported encoding channel: $key")
                }
            }.mapValues { (_, value) ->
                when (value) {
                    is Map<*, *> -> when {
                        Encodings.FIELD in value -> value.getString(Encodings.FIELD)!!
                        else -> error("Unsupported encoding value: $value")
                    }
                    else -> error("Unsupported encoding value: $value")
                }
            }
    }
}
