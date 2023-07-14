/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil
import org.jetbrains.letsPlot.core.plot.builder.PlotSvgComponent
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.frame.BogusFrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.frame.SquareFrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.LegendBoxInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot.PlotFigureLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot.PlotFigureLayouter
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

class PlotAssembler constructor(
    private val layersByTile: List<List<GeomLayer>>,
    private val scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>,
    private val scaleMappersNP: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>,
    private val facets: PlotFacets = PlotFacets.undefined(),
    private val coordProvider: CoordProvider,
    private val xAxisPosition: AxisPosition,
    private val yAxisPosition: AxisPosition,
    private val theme: Theme,
    private val title: String? = null,
    private val subtitle: String? = null,
    private val caption: String? = null,
    private val guideOptionsMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, GuideOptions> = HashMap(),
) {

    private val scaleXProto: Scale = scaleMap.getValue(org.jetbrains.letsPlot.core.plot.base.Aes.X)
    private val scaleYProto: Scale = scaleMap.getValue(org.jetbrains.letsPlot.core.plot.base.Aes.Y)

    val coreLayersByTile: List<List<GeomLayer>> = layersByTile.map { layers ->
        layers.filterNot { it.isMarginal }
    }
    private val marginalLayersByTile: List<List<GeomLayer>> = layersByTile.map { layers ->
        layers.filter { it.isMarginal }.filterNot { it.isLiveMap }
    }

    val containsLiveMap: Boolean = coreLayersByTile.flatten().any(GeomLayer::isLiveMap)

    private var legendsEnabled = true
    private var interactionsEnabled = true

    private val frameProviderByTile: List<FrameOfReferenceProvider>

    private val plotContext: PlotContext
    private val layouter: PlotFigureLayouter

    init {
        // ToDo: transformed ranges by aes
        plotContext = PlotAssemblerPlotContext(layersByTile, scaleMap)

        val legendBoxInfos: List<LegendBoxInfo> = when {
            legendsEnabled -> PlotAssemblerUtil.createLegends(
                plotContext,
                scaleMappersNP,
                guideOptionsMap,
                theme.legend()
            )

            else -> emptyList()
        }

        val flipAxis = coordProvider.flipped

        val (hAxisPosition, vAxisPosition) = when (flipAxis) {
            true -> yAxisPosition.flip() to xAxisPosition.flip()
            else -> xAxisPosition to yAxisPosition
        }

        frameProviderByTile = frameProviderByTile(
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            facets = facets,
            coordProvider = coordProvider,
            scaleXProto = scaleXProto,
            scaleYProto = scaleYProto,
            containsLiveMap = containsLiveMap,
            hAxisPosition = hAxisPosition,
            vAxisPosition = vAxisPosition,
            theme
        )

        layouter = PlotFigureLayouter(
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            frameProviderByTile = frameProviderByTile,
            facets = facets,
            coordProvider = coordProvider,
            hAxisPosition = hAxisPosition,
            vAxisPosition = vAxisPosition,
            theme = theme,
            legendBoxInfos = legendBoxInfos,
            title = title,
            subtitle = subtitle,
            caption = caption
        )
    }

    fun layoutByOuterSize(size: DoubleVector): PlotFigureLayoutInfo {
        return layouter.layoutByOuterSize(size)
    }

    fun layoutByGeomSize(size: DoubleVector): PlotFigureLayoutInfo {
        return layouter.layoutByGeomSize(size)
    }

    private fun hasLayers(): Boolean {
        return coreLayersByTile.any { it.isNotEmpty() }
    }

    fun createPlot(figureLayoutInfo: PlotFigureLayoutInfo): PlotSvgComponent {
        require(hasLayers()) { "No layers in plot" }
        return createPlot(
            frameProviderByTile = frameProviderByTile,
            figureLayoutInfo = figureLayoutInfo,
            styleSheet = Style.fromTheme(theme, coordProvider.flipped),
            plotContext = plotContext
        )
    }

    private fun createPlot(
        frameProviderByTile: List<FrameOfReferenceProvider>,
        figureLayoutInfo: PlotFigureLayoutInfo,
        styleSheet: StyleSheet,
        plotContext: PlotContext
    ): PlotSvgComponent {
        return PlotSvgComponent(
            title = title,
            subtitle = subtitle,
            caption = caption,
            coreLayersByTile = coreLayersByTile,
            marginalLayersByTile = marginalLayersByTile,
            figureLayoutInfo = figureLayoutInfo,
            frameProviderByTile = frameProviderByTile,
            coordProvider = coordProvider,
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
            scaleMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>,
            scaleMappersNP: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>>,
            coordProvider: CoordProvider,
            theme: Theme,
            xAxisPosition: AxisPosition = AxisPosition.BOTTOM,
            yAxisPosition: AxisPosition = AxisPosition.LEFT,
        ): PlotAssembler {
            val layersByTile = ArrayList<List<GeomLayer>>()
            layersByTile.add(plotLayers)
            return PlotAssembler(
                layersByTile,
                scaleMap,
                scaleMappersNP,
                coordProvider = coordProvider,
                xAxisPosition = xAxisPosition,
                yAxisPosition = yAxisPosition,
                theme = theme
            )
        }

        private fun frameProviderByTile(
            coreLayersByTile: List<List<GeomLayer>>,
            marginalLayersByTile: List<List<GeomLayer>>,
            facets: PlotFacets,
            coordProvider: CoordProvider,
            scaleXProto: Scale,
            scaleYProto: Scale,
            containsLiveMap: Boolean,
            hAxisPosition: AxisPosition,
            vAxisPosition: AxisPosition,
            theme: Theme,
        ): List<FrameOfReferenceProvider> {
            if (containsLiveMap) {
                return coreLayersByTile.map { BogusFrameOfReferenceProvider() }
            }

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

            // Marginal layers.
            // Marginal layers share "marginal domain" and layout across all tiles.
            val marginalLayers = marginalLayersByTile.flatten()
            val domainByMargin = MarginalLayerUtil.marginalDomainByMargin(marginalLayers, scaleXProto, scaleYProto)
            val marginsLayout: GeomMarginsLayout = GeomMarginsLayout.create(marginalLayers)

            // Create frame of reference provider for each tile.
            return domainsXYByTile.map { (xDomain, yDomain) ->
                val adjustedDomain = coordProvider.adjustDomain(DoubleRectangle(xDomain, yDomain))
                SquareFrameOfReferenceProvider(
                    hScaleProto, vScaleProto,
                    adjustedDomain,
                    flipAxis,
                    hAxisPosition, vAxisPosition,
                    theme,
                    marginsLayout,
                    domainByMargin
                )
            }
        }
    }
}
