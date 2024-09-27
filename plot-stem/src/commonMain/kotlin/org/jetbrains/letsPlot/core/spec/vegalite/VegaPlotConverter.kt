/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.spec.StatKind
import org.jetbrains.letsPlot.core.spec.asMapOfMaps
import org.jetbrains.letsPlot.core.spec.getDouble
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.plotson.LayerOptions
import org.jetbrains.letsPlot.core.spec.plotson.PlotOptions
import org.jetbrains.letsPlot.core.spec.plotson.toJson
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels.COLOR
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels.X
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels.X2
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels.Y
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encodings.Channels.Y2
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Mark
import org.jetbrains.letsPlot.core.spec.vegalite.Util.readMark
import kotlin.Pair
import kotlin.String
import kotlin.collections.get

internal class VegaPlotConverter private constructor(
    val vegaPlotSpec: MutableMap<String, Any>
) {
    companion object {
        fun convert(vegaPlotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            return VegaPlotConverter(vegaPlotSpec).convert()
        }
    }

    private val plotOptions = PlotOptions()


    private fun convert(): MutableMap<String, Any> {
        when (VegaConfig.getPlotKind(vegaPlotSpec)) {
            VegaPlotKind.SINGLE_LAYER -> processLayerSpec(vegaPlotSpec)
            VegaPlotKind.MULTI_LAYER -> {
                val plotVegaData = vegaPlotSpec.getMap(Option.DATA) ?: emptyMap()
                val plotVegaEncoding = (vegaPlotSpec.getMap(Encodings.ENCODING) ?: emptyMap()).asMapOfMaps()

                plotOptions.data = Util.transformData(plotVegaData)
                plotOptions.mappings = Util.transformMappings(plotVegaEncoding)
                plotOptions.dataMeta = Util.transformDataMeta(plotOptions.data, emptyMap(), plotVegaEncoding, emptyList())
                vegaPlotSpec.getMaps(Option.LAYER)!!
                    .forEach { layerSpec -> processLayerSpec(layerSpec) }
            }

            VegaPlotKind.FACETED -> error("Not implemented - faceted plot")
        }

        return plotOptions.toJson()
    }

    private fun processLayerSpec(layerSpec: Map<*, *>) {
        val (markType, markVegaSpec) = readMark(layerSpec[Option.MARK] ?: error("Mark is not specified"))
        val vegaEncoding = (layerSpec.getMap(Encodings.ENCODING) ?: emptyMap()).asMapOfMaps()

        fun LayerOptions.initDataAndMappings(vararg customChannelMapping: Pair<String, Aes<*>>) {
            data = Util.transformData(layerSpec.getMap(Option.DATA) ?: emptyMap<String, Any>())
            mappings = Util.transformMappings(vegaEncoding, customChannelMapping.toList())
            dataMeta = Util.transformDataMeta(data, plotOptions.data, vegaEncoding, customChannelMapping.toList())
        }

        when (markType) {
            Mark.Types.BAR -> plotOptions.appendLayer {
                if (vegaEncoding.values.any { Encodings.BIN in it }) {
                    geom = GeomKind.HISTOGRAM
                } else {
                    geom = GeomKind.BAR
                    width = markVegaSpec.getDouble(Mark.WIDTH, Mark.Width.BAND)
                }

                initDataAndMappings()
            }

            Mark.Types.LINE, Mark.Types.TRAIL -> plotOptions.appendLayer {
                geom = GeomKind.LINE
                initDataAndMappings()
            }

            Mark.Types.POINT -> plotOptions.appendLayer {
                geom = GeomKind.POINT
                initDataAndMappings()
            }

            Mark.Types.AREA -> plotOptions.appendLayer {
                geom = GeomKind.AREA
                initDataAndMappings()
            }

            Mark.Types.BOXPLOT -> {
                plotOptions.appendLayer {
                    geom = GeomKind.BOX_PLOT
                    initDataAndMappings()
                }

                plotOptions.appendLayer {
                    geom = GeomKind.POINT
                    stat = StatKind.BOXPLOT_OUTLIER.name.lowercase()
                    initDataAndMappings()
                }
            }

            Mark.Types.TEXT -> plotOptions.appendLayer {
                geom = GeomKind.TEXT
                initDataAndMappings()
            }

            Mark.Types.RECT -> plotOptions.appendLayer {
                if (listOf(X2, Y2).any { it in vegaEncoding }) {
                    geom = GeomKind.RECT
                    initDataAndMappings(X to Aes.XMIN, Y to Aes.YMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                } else {
                    geom = GeomKind.RASTER
                    initDataAndMappings(COLOR to Aes.FILL)
                }
            }

            Mark.Types.RULE -> {
                val isVLine = X in vegaEncoding && listOf(X2, Y, Y2).none(vegaEncoding::contains)
                val isHLine = Y in vegaEncoding && listOf(X, X2, Y2).none(vegaEncoding::contains)
                val isVSegment = listOf(X, Y, Y2).all(vegaEncoding::contains) && X2 !in vegaEncoding
                val isHSegment = listOf(X, X2, Y).all(vegaEncoding::contains) && Y2 !in vegaEncoding

                plotOptions.appendLayer {
                    geom = when {
                        isVLine -> GeomKind.V_LINE
                        isHLine -> GeomKind.H_LINE
                        isVSegment -> GeomKind.SEGMENT
                        isHSegment -> GeomKind.SEGMENT
                        else -> error("Rule markType can be used only for vertical or horizontal lines or segments.\nEncoding: $vegaEncoding")
                    }

                    when {
                        isVLine -> initDataAndMappings(X to Aes.XINTERCEPT)
                        isHLine -> initDataAndMappings(Y to Aes.YINTERCEPT)
                        isHSegment -> initDataAndMappings(Y to Aes.Y, Y to Aes.YEND, X2 to Aes.XEND)
                        isVSegment -> initDataAndMappings(X to Aes.X, X to Aes.XEND, Y2 to Aes.YEND)
                        else -> error("Rule markType can be used only for vertical or horizontal lines or segments")
                    }
                }
            }

            Mark.Types.CIRCLE, Mark.Types.SQUARE -> plotOptions.appendLayer {
                geom = GeomKind.POINT
                shape = when (markType) {
                    Mark.Types.CIRCLE -> NamedShape.SOLID_CIRCLE
                    Mark.Types.SQUARE -> NamedShape.SOLID_SQUARE
                    else -> error("Unsupported markType type: $markType")
                }

                initDataAndMappings()
            }

            Mark.Types.ERROR_BAR, Mark.Types.ERROR_BAND -> plotOptions.appendLayer {
                geom = when (markType) {
                    Mark.Types.ERROR_BAR -> GeomKind.ERROR_BAR
                    Mark.Types.ERROR_BAND -> GeomKind.RIBBON
                    else -> error("Unsupported markType type: $markType")
                }

                when (Util.iHorizontal(vegaEncoding)) {
                    true -> initDataAndMappings(X to Aes.XMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                    false -> initDataAndMappings(Y to Aes.YMIN, Y2 to Aes.YMAX, X2 to Aes.XMAX)
                }
            }

            Mark.Types.TICK -> plotOptions.appendLayer {
                geom = GeomKind.CROSS_BAR
                size = 0.1 // thickness of the tick
                width = 0.6

                initDataAndMappings(X to Aes.XMIN, X to Aes.XMAX)
            }

            else -> error("Unsupported markType type: $markType")
        }
    }

}
