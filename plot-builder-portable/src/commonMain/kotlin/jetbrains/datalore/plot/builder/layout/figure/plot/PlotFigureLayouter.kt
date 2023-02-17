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
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.axisTitlesOriginOffset
import jetbrains.datalore.plot.builder.layout.PlotLayoutUtil.legendBlockLeftTopDelta
import jetbrains.datalore.plot.builder.layout.tile.LiveMapAxisTheme
import jetbrains.datalore.plot.builder.layout.tile.LiveMapTileLayoutProvider
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.Theme
import kotlin.math.max

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

    private val hAxisTitle: String? = frameProviderByTile[0].hAxisLabel
    private val vAxisTitle: String? = frameProviderByTile[0].vAxisLabel

    private val axisEnabled = !containsLiveMap

    private val legendsBlockInfo: LegendsBlockInfo

    init {
        val legendTheme = theme.legend()
        legendsBlockInfo = LegendBoxesLayoutUtil.arrangeLegendBoxes(
            legendBoxInfos,
            legendTheme
        )
    }

    fun layoutByOuterSize(outerSize: DoubleVector): PlotFigureLayoutInfo {
        val overallRect = DoubleRectangle(DoubleVector.ZERO, outerSize)

        // compute geom bounds
        val entirePlot = if (containsLiveMap) {
            PlotLayoutUtil.liveMapBounds(overallRect)
        } else {
            overallRect
        }

        // -------------
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

        // -------------

        // Layout plot inners
        val plotLayout = createPlotLayout(insideOut = false)
        val layoutInfo = plotLayout.doLayout(plotInnerSizeAvailable, coordProvider)

        // -------------

        // Inner size includes geoms, axis and facet labels.
        val plotInnerSize = layoutInfo.size
        val plotOuterSize = PlotLayoutUtil.addTitlesAndLegends(
            plotInnerSize,
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

        return createFigureLayoutInfo(overallRect, plotOuterSize, layoutInfo)
    }

    fun layoutByGeomSize(geomSize: DoubleVector): PlotFigureLayoutInfo {
        val plotLayout = createPlotLayout(insideOut = true)
        val layoutInfo = plotLayout.doLayout(geomSize, coordProvider)

        // -------------

        // Inner size includes geoms, axis and facet labels.
        val plotInnerSize = layoutInfo.size
        val figureOuterSize = PlotLayoutUtil.addTitlesAndLegends(
            base = plotInnerSize,
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

        return createFigureLayoutInfo(
            overallRect = DoubleRectangle(DoubleVector.ZERO, figureOuterSize),
            plotOuterSize = figureOuterSize,
            layoutInfo
        )
    }


    private fun createPlotLayout(insideOut: Boolean): PlotLayout {
        return if (containsLiveMap) {
            createLiveMapPlotLayout()
        } else {
            val layoutProviderByTile: List<TileLayoutProvider> = frameProviderByTile.map {
                it.createTileLayoutProvider()
            }
            PlotAssemblerUtil.createPlotLayout(
                layoutProviderByTile,
                insideOut,
                facets,
                theme.facets(),
                hAxisPosition, vAxisPosition,
                hAxisTheme = theme.horizontalAxis(flipAxis),
                vAxisTheme = theme.verticalAxis(flipAxis),
            )
        }
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
            insideOut = false,
            facets,
            theme.facets(),
            hAxisPosition = AxisPosition.BOTTOM,  // Not used with Live Map
            vAxisPosition = AxisPosition.LEFT,    // Not used with Live Map
            hAxisTheme = LiveMapAxisTheme(),
            vAxisTheme = LiveMapAxisTheme(),
        )
    }

    private fun createFigureLayoutInfo(
        overallRect: DoubleRectangle,
        plotOuterSize: DoubleVector,
        plotLayoutInfo: PlotLayoutInfo
    ): PlotFigureLayoutInfo {
        // Position the "entire" plot rect in the center of the "overall" rect.
        val plotOuterBounds = let {
            val delta = overallRect.center.subtract(
                DoubleRectangle(overallRect.origin, plotOuterSize).center
            )
            val deltaApplied = DoubleVector(max(0.0, delta.x), max(0.0, delta.y))
            val plotOuterOrigin = overallRect.origin.add(deltaApplied)
            DoubleRectangle(plotOuterOrigin, plotOuterSize)
        }

        val plotOuterBoundsWithoutTitleAndCaption = let {
            val titleSizeDelta = PlotLayoutUtil.titleSizeDelta(title, subtitle, theme.plot())
            val captionSizeDelta = PlotLayoutUtil.captionSizeDelta(caption, theme.plot())
            DoubleRectangle(
                plotOuterBounds.origin.add(titleSizeDelta),
                plotOuterBounds.dimension.subtract(titleSizeDelta).subtract(captionSizeDelta)
            )
        }

        // Inner bounds - all without titles and legends.
        val plotInnerOrigin = plotOuterBoundsWithoutTitleAndCaption.origin
            .add(legendBlockLeftTopDelta(legendsBlockInfo, theme.legend()))
            .add(
                axisTitlesOriginOffset(
                    hAxisTitleInfo = hAxisTitle to PlotLabelSpecFactory.axisTitle(theme.horizontalAxis(flipAxis)),
                    vAxisTitleInfo = vAxisTitle to PlotLabelSpecFactory.axisTitle(theme.verticalAxis(flipAxis)),
                    hasTopAxisTitle = plotLayoutInfo.hasTopAxisTitle,
                    hasLeftAxisTitle = plotLayoutInfo.hasLeftAxisTitle,
                    axisEnabled,
                    marginDimensions = PlotLayoutUtil.axisMarginDimensions(theme, flipAxis)
                )
            )

        val geomAreaBounds = PlotLayoutUtil.overallGeomBounds(plotLayoutInfo)
            .add(plotInnerOrigin)

        return PlotFigureLayoutInfo(
            plotOuterBounds = plotOuterBounds,
            plotOuterBoundsWithoutTitleAndCaption = plotOuterBoundsWithoutTitleAndCaption,
            plotInnerOrigin = plotInnerOrigin,
            geomAreaBounds = geomAreaBounds,
//            outerSize = outerSize,
            outerSize = overallRect.dimension,
            plotLayoutInfo = plotLayoutInfo,
            legendsBlockInfo = legendsBlockInfo
        )
    }
}