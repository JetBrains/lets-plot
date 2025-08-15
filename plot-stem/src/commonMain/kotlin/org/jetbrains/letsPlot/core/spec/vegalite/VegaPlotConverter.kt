/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.vegalite

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.livemap.SUPPORTED_LAYERS
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.plotson.*
import org.jetbrains.letsPlot.core.spec.plotson.LayerOptions.SizeUnit
import org.jetbrains.letsPlot.core.spec.plotson.LiveMapLayer.Companion.liveMap
import org.jetbrains.letsPlot.core.spec.plotson.LiveMapLayer.Companion.vectorTiles
import org.jetbrains.letsPlot.core.spec.vegalite.Util.applyConstants
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.COLOR
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.SIZE
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.THETA
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.X
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.X2
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.Y
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channel.Y2
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Encoding.Channels
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.LetsPlotExt
import org.jetbrains.letsPlot.core.spec.vegalite.VegaOption.Mark
import kotlin.math.sqrt

internal class VegaPlotConverter private constructor(
    private val vegaPlotSpecMap: MutableMap<String, Any?>
) {
    private val accessLogger = TraceableMapWrapper.AccessLogger()
    private val vegaPlotSpec = TraceableMapWrapper(vegaPlotSpecMap, accessLogger)

    companion object {
        fun convert(vegaPlotSpec: MutableMap<String, Any?>): PlotOptions {
            val vegaPlotConverter = VegaPlotConverter(vegaPlotSpec)
            return vegaPlotConverter.convert()
        }
    }

    private val plotOptions = PlotOptions()
    private var useLiveMap = false

    private fun convert(): PlotOptions {
        plotOptions.title = Util.transformTitle(vegaPlotSpec[VegaOption.TITLE])

        when (VegaConfig.getPlotKind(vegaPlotSpecMap)) {
            VegaPlotKind.SINGLE_LAYER -> {
                val encodingSpecMap = vegaPlotSpecMap.getMap(VegaOption.ENCODING) ?: emptyMap<Any, Any>()
                processLayerSpec(vegaPlotSpecMap, encodingSpecMap, accessLogger)
            }

            VegaPlotKind.MULTI_LAYER -> {
                vegaPlotSpec.getMaps(VegaOption.LAYER)!!.forEachIndexed { i, it ->
                    val combinedEncoding = mutableMapOf<String, Any>()
                    vegaPlotSpec.getMap(VegaOption.ENCODING)?.let { encodings ->
                        Channels.forEach { channel ->
                            val encoding = encodings.getMap(channel) ?: return@forEach
                            combinedEncoding.write(channel) { encoding }

                            // Encoding spec was moved from the plot to the layer, where it’s used.
                            // Visit all plot encoding options to prevent them from appearing as unused in the summary
                            (encoding as Map<*, *>).getPaths()
                        }
                    }

                    it.getMap(VegaOption.ENCODING)?.let { encodings ->
                        Channels.forEach { channel ->
                            val encoding = encodings.getMap(channel) ?: return@forEach
                            combinedEncoding.write(channel) { encoding }
                        }
                    }

                    processLayerSpec(it, combinedEncoding, accessLogger.nested(listOf(VegaOption.LAYER, i)))
                }
            }
        }

        if (vegaPlotSpec.getBool(VegaOption.LETS_PLOT_EXT, LetsPlotExt.REPORT_CONVERTER_SUMMARY) == true) {
            val summary = accessLogger
                .findUnusedProperties(vegaPlotSpec - VegaOption.SCHEMA - VegaOption.DESCRIPTION - VegaOption.DATA)
                .map { path -> path.joinToString(prefix = "Unknown parameter: ", separator = ".") }

            plotOptions.computationMessages = summary
        }

        if (vegaPlotSpec.getBool(VegaOption.LETS_PLOT_EXT, LetsPlotExt.DARK_MODE) == true) {
            plotOptions.themeOptions = (plotOptions.themeOptions ?: ThemeOptions()).apply {
                name = null
                flavor = ThemeOptions.Flavor.DARCULA
            }
        }

        if (useLiveMap
            && (plotOptions.layerOptions?.size ?: 0) > 0
            && plotOptions.layerOptions!!.all { it.geom in SUPPORTED_LAYERS } == true
        ) {
            val liveMapLayer = liveMap {
                tiles = vectorTiles()

                // never scale points/lines etc
                constSizeZoomin = 0
                dataSizeZoomin = 0
            }

            plotOptions.layerOptions = listOf(liveMapLayer) + plotOptions.layerOptions!!
        }

        return plotOptions
    }

    private fun processLayerSpec(
        layerSpecMap: Map<*, *>,
        combinedEncodingSpecMap: Map<*, *>,
        accessLogger: TraceableMapWrapper.AccessLogger
    ) {
        val layerSpec: Map<*, *> = TraceableMapWrapper(layerSpecMap, accessLogger)
        val encoding = TraceableMapWrapper(combinedEncodingSpecMap, accessLogger.nested(listOf(VegaOption.ENCODING)))

        if (Channel.LATITUDE in encoding || Channel.LONGITUDE in encoding) {
            useLiveMap = true
        }

        val (markType, markVegaSpecMap) = Util.readMark(layerSpec[VegaOption.MARK] ?: error("Mark is not specified"))
        val markVegaSpec = TraceableMapWrapper(markVegaSpecMap, accessLogger.nested(listOf(VegaOption.MARK)))
        val transformResult = VegaTransformHelper.applyTransform(layerSpec, encoding)

        // Updating map that already tracked by accessLogger. It's ok as it adds only LP specific props,
        // and usage report is built based on the original map.
        transformResult?.encodingAdjustment?.forEach { (path, value) ->
            combinedEncodingSpecMap.write(path, value)
        }

        fun appendLayer(
            geom: GeomKind? = null,
            channelMapping: List<Pair<String, Aes<*>>> = emptyList(),
            block: LayerOptions.() -> Unit = {}
        ) {
            val layerOptions = LayerOptions()
                .apply {
                    this.geom = geom

                    val vegaData = when (VegaOption.DATA in layerSpec) {
                        true -> layerSpec.getMap(VegaOption.DATA)
                            ?: emptyMap() // if explicitly null - don't use plot data
                        false -> vegaPlotSpecMap.getMap(VegaOption.DATA) ?: emptyMap()
                    }

                    data = Util.transformData(vegaData)
                        .let { Util.applyTimeUnit(it, encoding) }

                    stat = transformResult?.stat
                    orientation = transformResult?.orientation
                    plotOptions.guides = Util.transformPlotGuides(plotOptions.guides, encoding, channelMapping)

                    mapping = Util.transformMappings(encoding, channelMapping)
                    position = Util.transformPositionAdjust(encoding, stat)
                    dataMeta = Util.transformDataMeta(data, encoding, channelMapping)

                    Util.transformCoordinateSystem(encoding, plotOptions)

                    applyConstants(layerSpec, channelMapping, mapping!!)
                }
                .apply(block)

            plotOptions.appendLayer(layerOptions)
        }

        when (markType) {
            Mark.Types.ARC -> appendLayer(
                geom = GeomKind.PIE,
                channelMapping = listOf(
                    THETA to Aes.SLICE,
                    COLOR to Aes.FILL,
                    COLOR to Aes.COLOR
                )
            ) {
                size = 0.9
                prop[PieLayer.SIZE_UNIT] = SizeUnit.MIN
                prop[PieLayer.DIRECTION] = -1
                plotOptions.themeOptions = (plotOptions.themeOptions ?: ThemeOptions()).setVoid()
            }

            Mark.Types.BAR ->
                if (transformResult?.stat?.kind == StatKind.BIN) {
                    appendLayer(
                        geom = GeomKind.HISTOGRAM,
                        channelMapping = listOf(
                            X to Aes.X,
                            Y to Aes.Y,
                            COLOR to Aes.FILL,
                            COLOR to Aes.COLOR
                        )
                    )
                } else if (X2 in encoding) {
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
                } else if (Y2 in encoding) {
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
            Mark.Types.POINT -> appendLayer(GeomKind.POINT) {
                // In Vega-Lite constant size represents the pixel area of the marks, while in Lets-Plot it's the diameter
                // So, we need to convert the area to the diameter
                size = size?.let(::sqrt)
            }

            Mark.Types.AREA -> appendLayer(
                channelMapping = listOf(COLOR to Aes.FILL, COLOR to Aes.COLOR)
            ) {
                geom = when (stat?.kind) {
                    StatKind.DENSITY -> GeomKind.DENSITY
                    else -> GeomKind.AREA
                }
            }

            Mark.Types.BOXPLOT -> {
                appendLayer(GeomKind.BOX_PLOT)
                appendLayer(GeomKind.POINT) {
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

            else -> println("Unsupported markType type: $markType")
        }
    }
}
