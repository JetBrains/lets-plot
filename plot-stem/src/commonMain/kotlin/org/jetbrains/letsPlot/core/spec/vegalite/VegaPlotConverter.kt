/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.spec.asMapOfMaps
import org.jetbrains.letsPlot.core.spec.getDouble
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.COLOR
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.X
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.X2
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.Y
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.Y2
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Mark
import org.jetbrains.letsPlot.core.spec.vegalite.Util.readMark
import org.jetbrains.letsPlot.core.spec.vegalite.Util.transformStat

internal class VegaPlotConverter private constructor(
    private val vegaPlotSpec: MutableMap<String, Any>
) {
    companion object {
        fun convert(vegaPlotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            return VegaPlotConverter(vegaPlotSpec).convert()
        }
    }

    private val plotData: Map<String, List<Any?>> = Util.transformData(vegaPlotSpec.getMap(Option.DATA) ?: emptyMap())
    private val plotEncoding = (vegaPlotSpec.getMap(Option.ENCODING) ?: emptyMap()).asMapOfMaps()
    private val plotOptions = PlotOptions()

    private fun convert(): MutableMap<String, Any> {
        when (VegaConfig.getPlotKind(vegaPlotSpec)) {
            VegaPlotKind.SINGLE_LAYER -> processLayerSpec(vegaPlotSpec)
            VegaPlotKind.MULTI_LAYER -> vegaPlotSpec.getMaps(Option.LAYER)!!.forEach(::processLayerSpec)
            VegaPlotKind.FACETED -> error("Not implemented - faceted plot")
        }

        return plotOptions.toJson()
    }

    private fun processLayerSpec(layerSpec: Map<*, *>) {
        val (markType, markVegaSpec) = readMark(layerSpec[Option.MARK] ?: error("Mark is not specified"))
        val encoding = (plotEncoding + (layerSpec.getMap(Option.ENCODING) ?: emptyMap())).asMapOfMaps()

        fun LayerOptions.initDataAndMappings(vararg customChannelMapping: Pair<String, Aes<*>>) {
            data = when {
                Option.DATA !in layerSpec -> plotData
                layerSpec[Option.DATA] == null -> emptyMap() // explicit null - no data, even from the parent plot
                layerSpec[Option.DATA] != null -> Util.transformData(layerSpec.getMap(Option.DATA)!!) // data is specified
                else -> error("Unsupported data specification")
            }
            mapping = Util.transformMappings(encoding, customChannelMapping.toList())
            dataMeta = Util.transformDataMeta(data, plotData, encoding, customChannelMapping.toList())
        }

        when (markType) {
            Mark.Types.BAR -> plotOptions.appendLayer {
                if (encoding.values.any { Encoding.Property.BIN in it }) {
                    initDataAndMappings()
                    geom = GeomKind.HISTOGRAM
                } else if (encoding.any { (channel, _) -> channel == X2 || channel == Y2 }) {
                    initDataAndMappings(X to Aes.XMIN, Y to Aes.YMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                    geom = GeomKind.RECT
                } else {
                    initDataAndMappings(COLOR to Aes.FILL, COLOR to Aes.COLOR)
                    geom = GeomKind.BAR
                    width = markVegaSpec.getDouble(Mark.WIDTH, Mark.Width.BAND)
                    stat = transformStat(encoding) ?: identityStat()
                    position = Util.transformPositionAdjust(encoding)
                }
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
                    stat = boxplotOutlierStat()
                    initDataAndMappings()
                }
            }

            Mark.Types.TEXT -> plotOptions.appendLayer {
                geom = GeomKind.TEXT
                stat = transformStat(encoding)
                position = Util.transformPositionAdjust(encoding)

                initDataAndMappings()
            }

            Mark.Types.RECT -> plotOptions.appendLayer {
                if (listOf(X2, Y2).any { it in encoding }) {
                    geom = GeomKind.RECT
                    initDataAndMappings(X to Aes.XMIN, Y to Aes.YMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                } else {
                    geom = GeomKind.RASTER
                    initDataAndMappings(COLOR to Aes.FILL)
                }
            }

            Mark.Types.RULE -> {
                val isVLine = X in encoding && listOf(X2, Y, Y2).none(encoding::contains)
                val isHLine = Y in encoding && listOf(X, X2, Y2).none(encoding::contains)
                val isVSegment = listOf(X, Y, Y2).all(encoding::contains) && X2 !in encoding
                val isHSegment = listOf(X, X2, Y).all(encoding::contains) && Y2 !in encoding

                plotOptions.appendLayer {
                    geom = when {
                        isVLine -> GeomKind.V_LINE
                        isHLine -> GeomKind.H_LINE
                        isVSegment -> GeomKind.SEGMENT
                        isHSegment -> GeomKind.SEGMENT
                        else -> error("Rule markType can be used only for vertical or horizontal lines or segments.\nEncoding: $encoding")
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

                when (Util.iHorizontal(encoding)) {
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
