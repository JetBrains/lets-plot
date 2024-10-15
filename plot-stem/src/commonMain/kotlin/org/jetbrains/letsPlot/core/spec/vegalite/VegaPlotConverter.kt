/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.spec.asMapOfMaps
import org.jetbrains.letsPlot.core.spec.getDouble
import org.jetbrains.letsPlot.core.spec.getMap
import org.jetbrains.letsPlot.core.spec.getMaps
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.COLOR
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.SIZE
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.X
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.X2
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.Y
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Encoding.Channel.Y2
import org.jetbrains.letsPlot.core.spec.vegalite.Option.Mark
import org.jetbrains.letsPlot.core.spec.vegalite.Util.applyConstants
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
        val (markType, markVegaSpec) = Util.readMark(layerSpec[Option.MARK] ?: error("Mark is not specified"))
        val encoding = (plotEncoding + (layerSpec.getMap(Option.ENCODING) ?: emptyMap())).asMapOfMaps()

        fun appendLayer(
            geom: GeomKind,
            channelMapping: List<Pair<String, Aes<*>>> = emptyList(),
            block: LayerOptions.() -> Unit = {}
        ) {
            val layerOptions = LayerOptions()
                .apply(block)
                .apply {
                    this.geom = geom
                    if (data == null) {
                        data = when {
                            Option.DATA !in layerSpec -> plotData
                            layerSpec[Option.DATA] == null -> emptyMap() // explicit null - no data, even from the parent plot
                            layerSpec[Option.DATA] != null -> Util.transformData(layerSpec.getMap(Option.DATA)!!) // data is specified
                            else -> error("Unsupported data specification")
                        }
                    }

                    if (mapping == null) {
                        mapping = Util.transformMappings(encoding, channelMapping)
                    }

                    if (stat == null) {
                        transformStat(encoding)
                    }

                    if (position == null) {
                        position = Util.transformPositionAdjust(encoding)
                    }

                    if (dataMeta == null) {
                        dataMeta = Util.transformDataMeta(data, encoding, channelMapping)
                    }

                    Util.transformCoordinateSystem(encoding, plotOptions)

                    applyConstants(markVegaSpec, channelMapping)
                }

            plotOptions.appendLayer(layerOptions)
        }

        when (markType) {
            Mark.Types.BAR ->
                if (encoding.values.any { Encoding.Property.BIN in it }) {
                    appendLayer(
                        geom = GeomKind.HISTOGRAM
                    ) {
                        binStat()
                    }
                } else if (encoding.any { (channel, _) -> channel == X2 }) {
                    appendLayer(
                        geom = GeomKind.CROSS_BAR,
                        channelMapping = listOf(
                            X to Aes.XMIN,
                            X2 to Aes.XMAX,
                            Y to Aes.Y,
                            COLOR to Aes.FILL,
                            COLOR to Aes.COLOR
                        )
                    )
                } else if (encoding.any { (channel, _) -> channel == Y2 }) {
                    appendLayer(
                        geom = GeomKind.CROSS_BAR,
                        channelMapping = listOf(
                            X to Aes.X,
                            Y to Aes.YMIN,
                            Y2 to Aes.YMAX,
                            COLOR to Aes.FILL,
                            COLOR to Aes.COLOR
                        )
                    )
                } else {
                    appendLayer(
                        geom = GeomKind.BAR,
                        channelMapping = listOf(COLOR to Aes.FILL, COLOR to Aes.COLOR)
                    ) {
                        if (!transformStat(encoding)) {
                            identityStat()
                        }
                        width = markVegaSpec.getDouble(Mark.WIDTH, Mark.Width.BAND)
                    }
                }


            Mark.Types.LINE, Mark.Types.TRAIL -> appendLayer(GeomKind.LINE)
            Mark.Types.POINT -> appendLayer(GeomKind.POINT)
            Mark.Types.AREA -> appendLayer(GeomKind.AREA)

            Mark.Types.BOXPLOT -> {
                val layerOrientation = when (Util.isContinuous(X, encoding)) {
                    true -> "y"
                    else -> null
                }

                appendLayer(GeomKind.BOX_PLOT) {
                    orientation = layerOrientation
                }

                appendLayer(GeomKind.POINT) {
                    orientation = layerOrientation
                    boxplotOutlierStat()
                }
            }

            Mark.Types.TEXT -> appendLayer(GeomKind.TEXT)

            Mark.Types.RECT ->
                if (listOf(X2, Y2).any { it in encoding }) {
                    appendLayer(
                        geom = GeomKind.RECT,
                        channelMapping = listOf(X to Aes.XMIN, Y to Aes.YMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                    )
                } else {
                    appendLayer(
                        geom = GeomKind.RASTER,
                        channelMapping = listOf(COLOR to Aes.FILL)
                    )
                }

            Mark.Types.RULE -> {
                val isVLine = X in encoding && listOf(X2, Y, Y2).none(encoding::contains)
                val isHLine = Y in encoding && listOf(X, X2, Y2).none(encoding::contains)
                val isVSegment = listOf(X, Y, Y2).all(encoding::contains) && X2 !in encoding
                val isHSegment = listOf(X, X2, Y).all(encoding::contains) && Y2 !in encoding
                when {
                    isVLine -> appendLayer(GeomKind.V_LINE, listOf(X to Aes.XINTERCEPT))
                    isHLine -> appendLayer(GeomKind.H_LINE, listOf(Y to Aes.YINTERCEPT))
                    isVSegment -> appendLayer(GeomKind.SEGMENT, listOf(X to Aes.X, X to Aes.XEND, Y2 to Aes.YEND))
                    isHSegment -> appendLayer(GeomKind.SEGMENT, listOf(Y to Aes.Y, Y to Aes.YEND, X2 to Aes.XEND))
                    else -> error("Rule markType can be used only for vertical or horizontal lines or segments.\nEncoding: $encoding")
                }
            }

            Mark.Types.CIRCLE, Mark.Types.SQUARE -> appendLayer(
                geom = GeomKind.POINT
            ) {
                shape = when (markType) {
                    Mark.Types.CIRCLE -> NamedShape.SOLID_CIRCLE
                    Mark.Types.SQUARE -> NamedShape.SOLID_SQUARE
                    else -> error("Unsupported markType type: $markType")
                }
            }


            Mark.Types.ERROR_BAR -> appendLayer(
                geom = GeomKind.ERROR_BAR,
                channelMapping = when (Util.iHorizontal(encoding)) {
                    true -> listOf(X to Aes.XMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                    false -> listOf(Y to Aes.YMIN, Y2 to Aes.YMAX, X2 to Aes.XMAX)
                }
            )

            Mark.Types.ERROR_BAND -> appendLayer(
                geom = GeomKind.RIBBON,
                channelMapping = when (Util.iHorizontal(encoding)) {
                    true -> listOf(X to Aes.XMIN, X2 to Aes.XMAX, Y2 to Aes.YMAX)
                    false -> listOf(Y to Aes.YMIN, Y2 to Aes.YMAX, X2 to Aes.XMAX)
                }
            ) {
                linetype = NamedLineType.BLANK
            }

            Mark.Types.TICK -> appendLayer(
                geom = GeomKind.CROSS_BAR,
                channelMapping = when (Util.isContinuous(X, encoding) == Util.isContinuous(Y, encoding)
                        || Util.isContinuous(X, encoding)
                ) {
                    true -> listOf(X to Aes.XMIN, X to Aes.XMAX)
                    false -> listOf(Y to Aes.YMIN, Y to Aes.YMAX)
                } + listOf(COLOR to Aes.FILL, COLOR to Aes.COLOR, SIZE to Aes.WIDTH)
            ) {
                prop[CrossbarLayer.FATTEN] = 0.0
            }

            else -> error("Unsupported markType type: $markType")
        }
    }
}
