/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.plot

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotAssemblerUtil
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.axisTitlesOriginOffset
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLegendsLayoutUtil.legendsSpaceLeftTopDelta
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.LiveMapAxisTheme
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.LiveMapTileLayoutProvider
import kotlin.math.max

internal class PlotFigureLayouter(
    private val frameProviderByTile: List<FrameOfReferenceProvider>,
    private val facets: PlotFacets,
    private val coordProvider: CoordProvider,
    private val containsLiveMap: Boolean,
    private val theme: Theme,
    legendBoxInfos: List<LegendBoxInfo>,
    private var title: String?,
    private var subtitle: String?,
    private var caption: String?,
    private var tag: String?,
) {
    private val flipAxis = coordProvider.flipped

    private val hAxisTitle: String? = frameProviderByTile[0].hAxisLabel
    private val vAxisTitle: String? = frameProviderByTile[0].vAxisLabel

    private val axisEnabled = !containsLiveMap

    private val legendsBlockInfo: LegendsBlockInfo?

    init {
        val legendTheme = theme.legend()
        legendsBlockInfo = when {
            legendBoxInfos.isNotEmpty() -> LegendsBlockInfo.arrangeLegendBoxes(
                legendBoxInfos,
                legendTheme
            )

            else -> null
        }

    }

    fun layoutByOuterSize(outerSize: DoubleVector): PlotFigureLayoutInfo {

        val innerSize = PlotLayoutUtil.subtractTitlesLegendsTagsAndMargins(
            baseSize = outerSize,
            title,
            subtitle,
            caption,
            tag,
            hAxisTitle,
            vAxisTitle,
            axisEnabled,
            legendsBlockInfo,
            theme,
            flipAxis
        )

        // -------------

        // Layout plot inners
        val plotLayout = createPlotLayout()
        val layoutInfo = plotLayout.layoutByPlotSize(innerSize, coordProvider)
        val layoutedOuterSize = toOuterSize(layoutInfo)

        // Position the "entire" plot rect in the center of the "overall" rect.
        val figurePreferredBounds = DoubleRectangle(DoubleVector.ZERO, outerSize)
        val delta = figurePreferredBounds.center.subtract(
            DoubleRectangle(figurePreferredBounds.origin, layoutedOuterSize).center
        )
        val deltaApplied = DoubleVector(max(0.0, delta.x), max(0.0, delta.y))

        val figureLayoutedBounds = DoubleRectangle(
            figurePreferredBounds.origin.add(deltaApplied),
            layoutedOuterSize
        )

        return createFigureLayoutInfo(
            plotLayoutInfo = layoutInfo,
            figureLayoutedBounds = figureLayoutedBounds,
            figureSvgSize = outerSize
        )
    }

    fun layoutByGeomSize(
        geomSize: DoubleVector,
        axisSpacer: Thickness,
        figureSvgPadding: Thickness = Thickness.ZERO
    ): PlotFigureLayoutInfo {
        val plotLayout = createPlotLayout()
        val layoutInfo = plotLayout.layoutByGeomSize(geomSize, coordProvider, axisSpacer)
        val layoutedOuterSize = toOuterSize(layoutInfo)

        // The 'inside-out' mode. Offset figureLayoutedBounds by 'svg padding'
        // so the subplot content is positioned within the larger SVG viewport.
        val figureSvgSize = figureSvgPadding.inflateSize(layoutedOuterSize)
        val figureLayoutedBounds = DoubleRectangle(figureSvgPadding.leftTop, layoutedOuterSize)

        return createFigureLayoutInfo(
            plotLayoutInfo = layoutInfo,
            figureLayoutedBounds = figureLayoutedBounds,
            figureSvgSize = figureSvgSize
        )
    }

    private fun toOuterSize(layoutInfo: PlotLayoutInfo): DoubleVector {
        // PlotLayoutInfo size includes geoms, axis and facet labels (no titles, legends).
        val plotInnerSize = layoutInfo.size
        return PlotLayoutUtil.addTitlesLegendsTagsAndMargins(
            base = plotInnerSize,
            title,
            subtitle,
            caption,
            tag,
            hAxisTitle,
            vAxisTitle,
            axisEnabled,
            legendsBlockInfo,
            theme,
            flipAxis
        )
    }

    private fun createPlotLayout(): PlotLayout {
        return if (containsLiveMap) {
            createLiveMapPlotLayout()
        } else {
            val layoutProviderByTile: List<TileLayoutProvider> = frameProviderByTile.map {
                it.createTileLayoutProvider()
            }
            PlotAssemblerUtil.createPlotLayout(
                layoutProviderByTile,
                facets,
                theme.facets(),
                hAxisTheme = theme.horizontalAxis(flipAxis),
                vAxisTheme = theme.verticalAxis(flipAxis),
                plotTheme = theme.plot()
            )
        }
    }

    private fun createLiveMapPlotLayout(): PlotLayout {
        // build 'live map' plot:
        //  - skip X/Y scale training
        //  - ignore coord provider
        //  - plot layout without axes
        val layoutProviderByTile = frameProviderByTile.map {
            LiveMapTileLayoutProvider()
        }
        return PlotAssemblerUtil.createPlotLayout(
            layoutProviderByTile,
            facets,
            theme.facets(),
            hAxisTheme = LiveMapAxisTheme(),
            vAxisTheme = LiveMapAxisTheme(),
            plotTheme = theme.plot()
        )
    }

    private fun createFigureLayoutInfo(
        plotLayoutInfo: PlotLayoutInfo,
        figureLayoutedBounds: DoubleRectangle,
        figureSvgSize: DoubleVector,
    ): PlotFigureLayoutInfo {

        val figureBoundsWithoutTitlesTagsAndMargins = run {
            val plotMargins = theme.plot().layoutMargins()
            val titleDelta = PlotLayoutUtil.titleSizeDelta(title, subtitle, theme.plot())
            val captionDelta = PlotLayoutUtil.captionSizeDelta(caption, theme.plot())
            val tagThickness = PlotLayoutUtil.tagMarginThickness(tag, theme.plot())

            val origin = figureLayoutedBounds.origin
                .add(plotMargins.leftTop)
                .add(titleDelta)
                .add(tagThickness.leftTop)

            val dimension = figureLayoutedBounds.dimension
                .subtract(plotMargins.size)
                .subtract(titleDelta)
                .subtract(captionDelta)
                .subtract(tagThickness.size)

            DoubleRectangle(origin, dimension)
        }

        // Inner bounds - all without titles, tags, legends and margins.
        // Plot origin: the origin of the plot area: geoms, axis and facet labels (no titles, legends).
        val plotOrigin = figureBoundsWithoutTitlesTagsAndMargins.origin
            .add(legendsSpaceLeftTopDelta(listOfNotNull(legendsBlockInfo), theme.legend()))
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

        // Geom outer bounds: plot without titles, legends, axis and facet labels.
        val geomOuterBounds = PlotLayoutUtil.overallGeomOuterBounds(plotLayoutInfo)
            .add(plotOrigin)

        // Actual plotting area: geomOuterBounds excluding marginal layers (if any) and plot panel insets
        val geomContentBounds = PlotLayoutUtil.overallGeomContentBounds(plotLayoutInfo)
            .add(plotOrigin)

        return PlotFigureLayoutInfo(
            figureLayoutedBounds = figureLayoutedBounds,
            figureBoundsWithoutTitlesTagsAndMargins = figureBoundsWithoutTitlesTagsAndMargins,
            plotAreaOrigin = plotOrigin,
            geomOuterBounds = geomOuterBounds,
            geomContentBounds = geomContentBounds,
            figureSvgSize = figureSvgSize,
            plotLayoutInfo = plotLayoutInfo,
            legendsBlockInfo = legendsBlockInfo,
        )
    }
}