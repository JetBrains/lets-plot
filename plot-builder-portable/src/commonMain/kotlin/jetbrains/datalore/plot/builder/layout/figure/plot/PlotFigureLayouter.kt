/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.FrameOfReferenceProvider
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.MarginalLayerUtil
import jetbrains.datalore.plot.builder.assemble.PlotAssemblerUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.PositionalScalesUtil
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.frame.BogusFrameOfReferenceProvider
import jetbrains.datalore.plot.builder.frame.SquareFrameOfReferenceProvider
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.tile.LiveMapAxisTheme
import jetbrains.datalore.plot.builder.layout.tile.LiveMapTileLayoutProvider
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.Theme

internal class PlotFigureLayouter(
    private val coreLayersByTile: List<List<GeomLayer>>,
    private val marginalLayersByTile: List<List<GeomLayer>>,
    private val facets: PlotFacets,
    private val coordProvider: CoordProvider,
    private val scaleXProto: Scale,
    private val scaleYProto: Scale,
    private val xAxisPosition: AxisPosition,
    private val yAxisPosition: AxisPosition,
    private val theme: Theme,
    private val legendBoxInfos: List<LegendBoxInfo>,
    private var title: String?,
    private var subtitle: String?,
    private var caption: String?,
) {
    private val flipAxis = coordProvider.flipped
    private val containsLiveMap: Boolean = coreLayersByTile.flatten().any(GeomLayer::isLiveMap)

    private val frameProviderByTile: List<FrameOfReferenceProvider>
    private val plotLayout: PlotLayout

    init {
        if (containsLiveMap) {
            frameProviderByTile = coreLayersByTile.map { BogusFrameOfReferenceProvider() }
            plotLayout = createLiveMapPlotLayout()
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

            val (hAxisPosition, vAxisPosition) = when (flipAxis) {
                true -> yAxisPosition.flip() to xAxisPosition.flip()
                else -> xAxisPosition to yAxisPosition
            }

            // Marginal layers.
            // Marginal layers share "marginal domain" and layout across all tiles.
            val marginalLayers = marginalLayersByTile.flatten()
            val domainByMargin = MarginalLayerUtil.marginalDomainByMargin(marginalLayers, scaleXProto, scaleYProto)
            val marginsLayout: GeomMarginsLayout = GeomMarginsLayout.create(marginalLayers)

            // Create frame of reference provider for each tile.
            frameProviderByTile =
                domainsXYByTile.map { (xDomain, yDomain) ->
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

            val layoutProviderByTile: List<TileLayoutProvider> = frameProviderByTile.map {
                it.createTileLayoutProvider()
            }
            plotLayout = PlotAssemblerUtil.createPlotLayout(
                layoutProviderByTile,
                facets,
                theme.facets(),
                hAxisPosition, vAxisPosition,
                hAxisTheme = theme.horizontalAxis(flipAxis),
                vAxisTheme = theme.verticalAxis(flipAxis),
            )
        }
    }

    fun doLayout(plotSize: DoubleVector): Result {
        val overallRect = DoubleRectangle(DoubleVector.ZERO, plotSize)

        val hAxisTitle: String? = frameProviderByTile[0].hAxisLabel
        val vAxisTitle: String? = frameProviderByTile[0].vAxisLabel

        // compute geom bounds
        val entirePlot = if (containsLiveMap) {
            PlotLayoutUtil.liveMapBounds(overallRect)
        } else {
            overallRect
        }

        val legendTheme = theme.legend()
        val legendsBlockInfo = LegendBoxesLayoutUtil.arrangeLegendBoxes(
            legendBoxInfos,
            legendTheme
        )

        // -------------
        val axisEnabled = !containsLiveMap
        val plotInnerSizeAvailable = PlotLayoutUtil.subtractTitlesAndLegends(
            baseSize = entirePlot.dimension,
            title,
            subtitle,
            caption,
            hAxisTitle,
            vAxisTitle,
            axisEnabled,
            legendsBlockInfo,
            theme,
            flipAxis
        )

        // Layout plot inners
        val layoutInfo = plotLayout.doLayout(plotInnerSizeAvailable, coordProvider)
        return Result(layoutInfo, frameProviderByTile)
    }

    private fun createLiveMapPlotLayout(): PlotLayout {
        // build 'live map' plot:
        //  - skip X/Y scale training
        //  - ignore coord provider
        //  - plot layout without axes
        val layoutProviderByTile = coreLayersByTile.map {
            LiveMapTileLayoutProvider()
        }
        return PlotAssemblerUtil.createPlotLayout(
            layoutProviderByTile,
            facets,
            theme.facets(),
            hAxisPosition = AxisPosition.BOTTOM,  // Not used with Live Map
            vAxisPosition = AxisPosition.LEFT,    // Not used with Live Map
            hAxisTheme = LiveMapAxisTheme(),
            vAxisTheme = LiveMapAxisTheme(),
        )
    }

    data class Result(
        val layoutInfo: PlotLayoutInfo,
        val frameProviderByTile: List<FrameOfReferenceProvider>
    )
}