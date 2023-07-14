/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssemblerUtil
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.axisTitlesOriginOffset
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.legendBlockLeftTopDelta
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.LiveMapAxisTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.LiveMapTileLayoutProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
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
        val figureBaseSize = if (containsLiveMap) {
            val figBounds = DoubleRectangle(DoubleVector.ZERO, outerSize)
            PlotLayoutUtil.liveMapBounds(figBounds).dimension
        } else {
            outerSize
        }

        // -------------
        val plotPreferredSize = PlotLayoutUtil.subtractTitlesAndLegends(
            baseSize = figureBaseSize,
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
        val layoutInfo = plotLayout.doLayout(plotPreferredSize, coordProvider)

        return createFigureLayoutInfo(
            figurePreferredSize = outerSize,
            plotLayoutInfo = layoutInfo
        )
    }

    fun layoutByGeomSize(geomSize: DoubleVector): PlotFigureLayoutInfo {
        val plotLayout = createPlotLayout(insideOut = true)
        val layoutInfo = plotLayout.doLayout(geomSize, coordProvider)

        return createFigureLayoutInfo(
            figurePreferredSize = null,
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
        figurePreferredSize: DoubleVector?,
        plotLayoutInfo: PlotLayoutInfo
    ): PlotFigureLayoutInfo {
        // Plot size includes geoms, axis and facet labels (no titles, legends).
        val plotSize = plotLayoutInfo.size
        val figureLayoutedSize = PlotLayoutUtil.addTitlesAndLegends(
            base = plotSize,
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

        // Position the "entire" plot rect in the center of the "overall" rect.
        val figureLayoutedBounds = if (figurePreferredSize == null) {
            DoubleRectangle(DoubleVector.ZERO, figureLayoutedSize)
        } else {
            val figurePreferredBounds = DoubleRectangle(DoubleVector.ZERO, figurePreferredSize)
            val delta = figurePreferredBounds.center.subtract(
                DoubleRectangle(figurePreferredBounds.origin, figureLayoutedSize).center
            )
            val deltaApplied = DoubleVector(max(0.0, delta.x), max(0.0, delta.y))
            val plotOuterOrigin = figurePreferredBounds.origin.add(deltaApplied)
            DoubleRectangle(plotOuterOrigin, figureLayoutedSize)
        }

        val figureBoundsWithoutTitleAndCaption = let {
            val titleSizeDelta = PlotLayoutUtil.titleSizeDelta(title, subtitle, theme.plot())
            val captionSizeDelta = PlotLayoutUtil.captionSizeDelta(caption, theme.plot())
            DoubleRectangle(
                figureLayoutedBounds.origin.add(titleSizeDelta),
                figureLayoutedBounds.dimension.subtract(titleSizeDelta).subtract(captionSizeDelta)
            )
        }

        // Inner bounds - all without titles and legends.
        // Plot origin : the origin of the plot area: geoms, axis and facet labels (no titles, legends).
        val plotOrigin = figureBoundsWithoutTitleAndCaption.origin
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

        // Geom area: plot withot axis and facet labels.
        val geomAreaBounds = PlotLayoutUtil.overallGeomBounds(plotLayoutInfo)
            .add(plotOrigin)

        return PlotFigureLayoutInfo(
            figureLayoutedBounds = figureLayoutedBounds,
            figureBoundsWithoutTitleAndCaption = figureBoundsWithoutTitleAndCaption,
            plotAreaOrigin = plotOrigin,
            geomAreaBounds = geomAreaBounds,
            figurePreferredSize = figurePreferredSize ?: figureLayoutedBounds.dimension,
            plotLayoutInfo = plotLayoutInfo,
            legendsBlockInfo = legendsBlockInfo
        )
    }
}