/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.figure.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.FrameOfReferenceProvider
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.assemble.PlotAssemblerUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.tile.LiveMapAxisTheme
import jetbrains.datalore.plot.builder.layout.tile.LiveMapTileLayoutProvider
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.Theme

internal class PlotFigureLayouter constructor(
    private val coreLayersByTile: List<List<GeomLayer>>,
    private val marginalLayersByTile: List<List<GeomLayer>>,
    private val frameProviderByTile: List<FrameOfReferenceProvider>,
    private val facets: PlotFacets,
    private val coordProvider: CoordProvider,
    private val hAxisPosition: AxisPosition,
    private val vAxisPosition: AxisPosition,
    private val theme: Theme,
    private val legendBoxInfos: List<LegendBoxInfo>,
    private var title: String?,
    private var subtitle: String?,
    private var caption: String?,
) {
    private val flipAxis = coordProvider.flipped
    private val containsLiveMap: Boolean = coreLayersByTile.flatten().any(GeomLayer::isLiveMap)

    private val plotLayout: PlotLayout

    init {
        if (containsLiveMap) {
            plotLayout = createLiveMapPlotLayout()
        } else {
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

    fun doLayout(outerSize: DoubleVector): PlotFigureLayoutInfo {
        val overallRect = DoubleRectangle(DoubleVector.ZERO, outerSize)

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
        return PlotFigureLayoutInfo(
            outerSize = outerSize,
            plotLayoutInfo = layoutInfo
        )
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
}