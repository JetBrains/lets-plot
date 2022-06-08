/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.builder.*
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.frame.BogusFrameOfReferenceProvider
import jetbrains.datalore.plot.builder.frame.SquareFrameOfReferenceProvider
import jetbrains.datalore.plot.builder.layout.GeomMarginsLayout
import jetbrains.datalore.plot.builder.layout.LegendBoxInfo
import jetbrains.datalore.plot.builder.layout.PlotLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider
import jetbrains.datalore.plot.builder.layout.tile.LiveMapAxisTheme
import jetbrains.datalore.plot.builder.layout.tile.LiveMapTileLayoutProvider
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.presentation.Style
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.vis.StyleSheet

class PlotAssembler private constructor(
    private val layersByTile: List<List<GeomLayer>>,
    private val scaleXProto: Scale<Double>,
    private val scaleYProto: Scale<Double>,
    private val scaleMappers: Map<Aes<*>, ScaleMapper<*>>,
    private val coordProvider: CoordProvider,
    private val theme: Theme
) {

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

        val styleSheet: StyleSheet = Style.fromTheme(theme, coordProvider.flipAxis)
        // ToDo Remove this global label metrics:
        PlotLabelSpec.initWithStyleSheet(styleSheet)

        val legendsBoxInfos = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                layersByTile,
                scaleMappers,
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
            createPlot(frameProviderByTile, plotLayout, legendsBoxInfos, styleSheet)
        } else {
            val flipAxis = coordProvider.flipAxis
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

            // Marginal layers.
            // Marginal layers share "marginal domain" and layout across all tiles.
            val marginalLayers = marginalLayersByTile.flatten()
            val domainByMargin = MarginalLayerUtil.marginalDomainByMargin(marginalLayers, scaleXProto, scaleYProto)
            val marginsLayout: GeomMarginsLayout = GeomMarginsLayout.create(marginalLayers)

            // Create frame of reference provider for each tile.
            val frameProviderByTile: List<FrameOfReferenceProvider> =
                domainsXYByTile.map { (xDomain, yDomain) ->
                    val (hDomain, vDomain) = coordProvider.adjustDomains(
                        hDomain = if (flipAxis) yDomain else xDomain,
                        vDomain = if (flipAxis) xDomain else yDomain
                    )
                    SquareFrameOfReferenceProvider(
                        hScaleProto, vScaleProto,
                        hDomain, vDomain,
                        flipAxis,
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

            createPlot(frameProviderByTile, plotLayout, legendsBoxInfos, styleSheet)
        }
    }

    private fun createPlot(
        frameProviderByTile: List<FrameOfReferenceProvider>,
        plotLayout: PlotLayout,
        legendBoxInfos: List<LegendBoxInfo>,
        styleSheet: StyleSheet
    ): PlotSvgComponent {

        return PlotSvgComponent(
            title = title,
            subtitle = subtitle,
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            plotLayout = plotLayout,
            frameProviderByTile = frameProviderByTile,
            coordProvider = coordProvider,
            legendBoxInfos = legendBoxInfos,
            interactionsEnabled = interactionsEnabled,
            theme = theme,
            caption = caption,
            styleSheet = styleSheet
        )
    }

    fun disableLegends() {
        legendsEnabled = false
    }

    fun disableInteractions() {
        interactionsEnabled = false
    }

    companion object {
        // Note: 'singleTile' is only used in demos.
        fun singleTile(
            plotLayers: List<GeomLayer>,
            scaleXProto: Scale<Double>,
            scaleYProto: Scale<Double>,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return multiTile(
                layersByTile,
                scaleXProto,
                scaleYProto,
                scaleMappersNP,
                coordProvider,
                theme
            )
        }

        fun multiTile(
            layersByTile: List<List<GeomLayer>>,
            scaleXProto: Scale<Double>,
            scaleYProto: Scale<Double>,
            scaleMappersNP: Map<Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme
        ): PlotAssembler {
            return PlotAssembler(
                layersByTile,
                scaleXProto,
                scaleYProto,
                scaleMappersNP,
                coordProvider,
                theme
            )
        }
    }
}
