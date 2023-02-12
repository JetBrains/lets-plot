/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.FrameOfReferenceProvider
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.PlotLayoutInfo
import jetbrains.datalore.plot.builder.layout.figure.plot.PlotFigureLayouter
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.StyleSheet

class PlotAssembler private constructor(
    private val layersByTile: List<List<GeomLayer>>,
    private val scaleMap: Map<Aes<*>, Scale>,
    private val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
    private val coordProvider: CoordProvider,
    private val xAxisPosition: AxisPosition,
    private val yAxisPosition: AxisPosition,
    private val theme: Theme
) {

    private val scaleXProto: Scale = scaleMap.getValue(Aes.X)
    private val scaleYProto: Scale = scaleMap.getValue(Aes.Y)

    val coreLayersByTile: List<List<GeomLayer>> = layersByTile.map { layers ->
        layers.filterNot { it.isMarginal }
    }
    private val marginalLayersByTile: List<List<GeomLayer>> = layersByTile.map { layers ->
        layers.filter { it.isMarginal }.filterNot { it.isLiveMap }
    }

    val containsLiveMap: Boolean = coreLayersByTile.flatten().any(GeomLayer::isLiveMap)

    var facets: PlotFacets = PlotFacets.undefined()
    var title: String? = null
    var subtitle: String? = null
    var caption: String? = null
    var guideOptionsMap: Map<Aes<*>, GuideOptions> = HashMap()

    private var legendsEnabled = true
    private var interactionsEnabled = true


    private fun hasLayers(): Boolean {
        return coreLayersByTile.any { it.isNotEmpty() }
    }

    fun createPlot(size: DoubleVector): PlotSvgComponent {
        require(hasLayers()) { "No layers in plot" }

        val styleSheet: StyleSheet = Style.fromTheme(theme, coordProvider.flipped)

        // ToDo: transformed ranges by aes
        val plotContext: PlotContext = PlotAssemblerPlotContext(layersByTile, scaleMap)

        val legendBoxInfos: List<LegendBoxInfo> = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                plotContext,
                scaleMappersNP,
                guideOptionsMap,
                theme.legend()
            )

            else -> emptyList()
        }

        val layouter = PlotFigureLayouter(
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            facets = facets,
            coordProvider = coordProvider,
            scaleXProto = scaleXProto,
            scaleYProto = scaleYProto,
            xAxisPosition = xAxisPosition,
            yAxisPosition = yAxisPosition,
            theme = theme,
            legendBoxInfos = legendBoxInfos,
            title = title,
            subtitle = subtitle,
            caption = caption
        )

        val result = layouter.doLayout(size)
        return createPlot(
            size = size,
            frameProviderByTile = result.frameProviderByTile,
            plotLayoutInfo = result.layoutInfo,
            legendBoxInfos = legendBoxInfos,
            styleSheet = styleSheet,
            plotContext = plotContext
        )
    }

    private fun createPlot(
        size: DoubleVector,
        frameProviderByTile: List<FrameOfReferenceProvider>,
        plotLayoutInfo: PlotLayoutInfo,
        legendBoxInfos: List<LegendBoxInfo>,
        styleSheet: StyleSheet,
        plotContext: PlotContext
    ): PlotSvgComponent {
        return PlotSvgComponent(
            plotSize = size,
            title = title,
            subtitle = subtitle,
            caption = caption,
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            plotLayoutInfo = plotLayoutInfo,
            frameProviderByTile = frameProviderByTile,
            coordProvider = coordProvider,
            legendBoxInfos = legendBoxInfos,
            interactionsEnabled = interactionsEnabled,
            theme = theme,
            styleSheet = styleSheet,
            plotContext = plotContext
        )
    }

    fun disableLegends() {
        legendsEnabled = false
    }

    fun disableInteractions() {
        interactionsEnabled = false
    }

    companion object {
        fun demoAndTest(
            plotLayers: List<GeomLayer>,
            scaleMap: Map<Aes<*>, Scale>,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme,
            xAxisPosition: AxisPosition = AxisPosition.BOTTOM,
            yAxisPosition: AxisPosition = AxisPosition.LEFT,
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                layersByTile,
                scaleMap,
                scaleMappersNP,
                coordProvider,
                xAxisPosition,
                yAxisPosition,
                theme
            )
        }

        fun multiTile(
            layersByTile: List<List<GeomLayer>>,
            scaleMap: Map<Aes<*>, Scale>,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            xAxisPosition: AxisPosition,
            yAxisPosition: AxisPosition,
            theme: Theme
        ): PlotAssembler {
            return PlotAssembler(
                layersByTile,
                scaleMap,
                scaleMappersNP,
                coordProvider,
                xAxisPosition,
                yAxisPosition,
                theme
            )
        }
    }
}
