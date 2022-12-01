/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.FrameOfReferenceProvider
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.MarginalLayerUtil
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.frame.BogusFrameOfReferenceProvider
import jetbrains.datalore.plot.builder.frame.SquareFrameOfReferenceProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.GeomMarginsLayout
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider
import jetbrains.datalore.plot.builder.layout.tile.LiveMapAxisTheme
import jetbrains.datalore.plot.builder.layout.tile.LiveMapTileLayoutProvider
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.StyleSheet

class PlotAssembler private constructor(
    private val layersByTile: List<List<GeomLayer>>,
    private val scaleMap: TypedScaleMap,
    private val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
    private val coordProvider: CoordProvider,
    private val xAxisOrientation: Orientation,
    private val yAxisOrientation: Orientation,
    private val theme: Theme
) {

    private val scaleXProto: Scale<Double> = scaleMap.get(Aes.X)
    private val scaleYProto: Scale<Double> = scaleMap.get(Aes.Y)

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

    fun createPlot(): PlotSvgComponent {
        require(hasLayers()) { "No layers in plot" }

        val styleSheet: StyleSheet = Style.fromTheme(theme, coordProvider.flipped)

        // ToDo: transformed ranges by aes
        val plotContext: PlotContext = PlotAssemblerPlotContext(layersByTile, scaleMap)

        val legendsBoxInfos = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                plotContext,
                scaleMappersNP,
                guideOptionsMap,
                theme.legend()
            )

            else -> emptyList()
        }

        return if (containsLiveMap) {
            // build 'live map' plot:
            //  - skip X/Y scale training
            //  - ignore coord provider
            //  - plot layout without axes
            val layoutProviderByTile = coreLayersByTile.map {
                LiveMapTileLayoutProvider()
            }
            val plotLayout = PlotAssemblerUtil.createPlotLayout(
                layoutProviderByTile,
                facets,
                theme.facets(),
                hAxisTheme = LiveMapAxisTheme(),
                vAxisTheme = LiveMapAxisTheme(),
            )
            val frameProviderByTile = coreLayersByTile.map {
                BogusFrameOfReferenceProvider()
            }
            createPlot(frameProviderByTile, plotLayout, legendsBoxInfos, styleSheet, plotContext)
        } else {
            val flipAxis = coordProvider.flipped
            val domainsXYByTile = PositionalScalesUtil.computePlotXYTransformedDomains(
                coreLayersByTile,
                scaleXProto,
                scaleYProto,
                facets
            )
            val (hScaleProto, vScaleProto) = when (flipAxis) {
                true -> scaleYProto to scaleXProto
                else -> scaleXProto to scaleYProto
            }

            val (hAxisOrientation, vAxisOrientation) = when (flipAxis) {
                true -> yAxisOrientation.flip() to xAxisOrientation.flip()
                else -> xAxisOrientation to yAxisOrientation
            }

            // Marginal layers.
            // Marginal layers share "marginal domain" and layout across all tiles.
            val marginalLayers = marginalLayersByTile.flatten()
            val domainByMargin = MarginalLayerUtil.marginalDomainByMargin(marginalLayers, scaleXProto, scaleYProto)
            val marginsLayout: GeomMarginsLayout = GeomMarginsLayout.create(marginalLayers)

            // Create frame of reference provider for each tile.
            val frameProviderByTile: List<FrameOfReferenceProvider> =
                domainsXYByTile.map { (xDomain, yDomain) ->
                    val adjustedDomain = coordProvider.adjustDomain(DoubleRectangle(xDomain, yDomain))
                    SquareFrameOfReferenceProvider(
                        hScaleProto, vScaleProto,
                        adjustedDomain,
                        flipAxis,
                        hAxisOrientation, vAxisOrientation,
                        theme,
                        marginsLayout,
                        domainByMargin
                    )
                }

            val layoutProviderByTile: List<TileLayoutProvider> = frameProviderByTile.map {
                it.createTileLayoutProvider()
            }
            val plotLayout = PlotAssemblerUtil.createPlotLayout(
                layoutProviderByTile,
                facets,
                theme.facets(),
                hAxisTheme = theme.horizontalAxis(flipAxis),
                vAxisTheme = theme.verticalAxis(flipAxis),
            )

            createPlot(frameProviderByTile, plotLayout, legendsBoxInfos, styleSheet, plotContext)
        }
    }

    private fun createPlot(
        frameProviderByTile: List<FrameOfReferenceProvider>,
        plotLayout: PlotLayout,
        legendBoxInfos: List<LegendBoxInfo>,
        styleSheet: StyleSheet,
        plotContext: PlotContext
    ): PlotSvgComponent {

        return PlotSvgComponent(
            title = title,
            subtitle = subtitle,
            caption = caption,
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            plotLayout = plotLayout,
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
            scaleMap: TypedScaleMap,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme,
            xAxisOrientation: Orientation = Orientation.BOTTOM,
            yAxisOrientation: Orientation = Orientation.LEFT,
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                layersByTile,
                scaleMap,
                scaleMappersNP,
                coordProvider,
                xAxisOrientation,
                yAxisOrientation,
                theme
            )
        }

        fun multiTile(
            layersByTile: List<List<GeomLayer>>,
            scaleMap: TypedScaleMap,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            xAxisOrientation: Orientation,
            yAxisOrientation: Orientation,
            theme: Theme
        ): PlotAssembler {
            return PlotAssembler(
                layersByTile,
                scaleMap,
                scaleMappersNP,
                coordProvider,
                xAxisOrientation,
                yAxisOrientation,
                theme
            )
        }
    }
}
