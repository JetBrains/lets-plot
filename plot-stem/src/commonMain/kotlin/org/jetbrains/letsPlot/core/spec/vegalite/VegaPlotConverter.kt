/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.commons.intern.filterNotNullKeys
import org.jetbrains.letsPlot.commons.intern.filterNotNullValues
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.vegalite.Util.applyConstants
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.COLOR
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.SIZE
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.X
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.X2
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.Y
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.Y2
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Mark

internal class VegaPlotConverter private constructor(
    private val vegaPlotSpec: MutableMap<String, Any>
) {
    companion object {
        fun convert(vegaPlotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            return VegaPlotConverter(vegaPlotSpec).convert()
        }
    }

    private val plotData: Map<String, List<Any?>> =
        Util.transformData(vegaPlotSpec.getMap(VegaOption.DATA) ?: emptyMap())
    private val plotEncoding = (vegaPlotSpec.getMap(VegaOption.ENCODING) ?: emptyMap()).asMapOfMaps()
    private val plotOptions = PlotOptions()

    private fun convert(): MutableMap<String, Any> {
        when (VegaConfig.getPlotKind(vegaPlotSpec)) {
            VegaPlotKind.SINGLE_LAYER -> processLayerSpec(vegaPlotSpec)
            VegaPlotKind.MULTI_LAYER -> vegaPlotSpec.getMaps(VegaOption.LAYER)!!.forEach(::processLayerSpec)
            VegaPlotKind.FACETED -> error("Not implemented - faceted plot")
        }

        return plotOptions.toJson()
    }

    private fun processLayerSpec(layerSpec: Map<*, *>) {
        val (markType, markVegaSpec) = Util.readMark(layerSpec[VegaOption.MARK] ?: error("Mark is not specified"))
        val encoding = (plotEncoding + (layerSpec.getMap(VegaOption.ENCODING) ?: emptyMap())).asMapOfMaps()

        fun appendLayer(
            geom: GeomKind? = null,
            channelMapping: List<Pair<String, Aes<*>>> = emptyList(),
            block: LayerOptions.() -> Unit = {}
        ) {
            val layerOptions = LayerOptions()
                .apply {
                    this.geom = geom
                    data = when {
                        VegaOption.DATA !in layerSpec -> plotData
                        layerSpec[VegaOption.DATA] == null -> emptyMap() // explicit null - no data, even from the parent plot
                        layerSpec[VegaOption.DATA] != null -> Util.transformData(layerSpec.getMap(VegaOption.DATA)!!) // data is specified
                        else -> error("Unsupported data specification")
                    }

                    val vegaMapping = encoding
                        .mapKeys { (k, _) -> k as? String }.filterNotNullKeys()
                        .mapValues { (_, v) -> v[Encoding.FIELD] as? String }
                        .filterNotNullValues()

                    mapping = Util.transformMappings(vegaMapping, channelMapping)
                    position = Util.transformPositionAdjust(encoding)
                    dataMeta = Util.transformDataMeta(data, encoding, channelMapping)

                    Util.transformStat(encoding, layerSpec, mapping)?.let { (newStat, newMapping) ->
                        stat = newStat
                        mapping = newMapping
                    }

                    Util.transformCoordinateSystem(encoding, plotOptions)

                    applyConstants(markVegaSpec, channelMapping)
                }
                .apply(block)

            plotOptions.appendLayer(layerOptions)
        }

        when (markType) {
            Mark.Types.BAR ->
                if (encoding.values.any { Encoding.BIN in it }) {
                    appendLayer(
                        geom = GeomKind.HISTOGRAM
                    ) {
                        stat = binStat()
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
                        if (stat == null) {
                            stat = identityStat()
                        }
                        width = markVegaSpec.getDouble(Mark.WIDTH, Mark.Width.BAND)
                    }
                }


            Mark.Types.LINE, Mark.Types.TRAIL -> appendLayer(GeomKind.LINE)
            Mark.Types.POINT -> appendLayer(GeomKind.POINT)
            Mark.Types.AREA -> appendLayer() {
                geom = when (stat?.kind) {
                    StatKind.DENSITY -> GeomKind.DENSITY
                    else -> GeomKind.AREA
                }
            }

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
                    stat = boxplotOutlierStat()
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
                geom = GeomKind.POINT,
                channelMapping = listOf(
                    COLOR to Aes.FILL,
                    COLOR to Aes.COLOR
                )
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
