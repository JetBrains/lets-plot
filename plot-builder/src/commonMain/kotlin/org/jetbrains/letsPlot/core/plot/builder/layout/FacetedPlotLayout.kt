/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayoutUtil.geomOffsetsByCol
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayoutUtil.geomOffsetsByRow
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.plotInsets
import org.jetbrains.letsPlot.core.plot.builder.layout.facet.FixedScalesTilesLayouter
import org.jetbrains.letsPlot.core.plot.builder.layout.facet.FreeScalesTilesLayouter
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition

internal class FacetedPlotLayout(
    private val facets: PlotFacets,
    private val layoutProviderByTile: List<TileLayoutProvider>,
    private val showFacetStrip: Boolean,
    hAxisOrientation: AxisPosition,
    vAxisOrientation: AxisPosition,
    private val hAxisTheme: AxisTheme,
    private val vAxisTheme: AxisTheme,
    private val plotTheme: PlotTheme,
    private val facetsTheme: FacetsTheme
) : PlotLayout {
    private val totalAddedHSize: Double = PANEL_PADDING * (facets.colCount - 1)
    private val totalAddedVSize: Double = PANEL_PADDING * (facets.rowCount - 1)

    private val insets: Insets = plotInsets(
        hAxisOrientation, vAxisOrientation,
        hAxisTheme, vAxisTheme
    )

    init {
        require(facets.isDefined) { "Undefined facets." }
    }

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): PlotLayoutInfo {
        val plotMargins = plotTheme.plotMargins()
        var tilesAreaSize = DoubleVector(
            preferredSize.x - (insets.left + insets.right),
            preferredSize.y - (insets.top + insets.bottom)
        )
            .subtract(DoubleVector(plotMargins.width, plotMargins.height))

        val facetTiles = facets.tileInfos()

        if (showFacetStrip) {
            val totalAddedHeight = facetTiles
                .groupBy ( { it.row }, { facetColHeadHeight(it.colLabs, facetsTheme) } )
                .mapValues { it.value.max() }
                .values.sum()

            val width = facetTiles.mapNotNull { it.rowLab }.maxOfOrNull { facetTabHeight(it, facetsTheme) } ?: 0.0
            val labsTotalDim = DoubleVector(width, totalAddedHeight)

            tilesAreaSize = tilesAreaSize.subtract(labsTotalDim)
        }

        val layoutInfos: List<TileLayoutInfo> = if (facets.freeHScale || facets.freeVScale) {
            FreeScalesTilesLayouter.createTileLayoutInfos(
                tilesAreaSize,
                facets,
                layoutProviderByTile,
                totalAddedHSize,
                totalAddedVSize,
                coordProvider,
                hAxisTheme = hAxisTheme,
                vAxisTheme = vAxisTheme,
            )
        } else {
            FixedScalesTilesLayouter.createTileLayoutInfos(
                tilesAreaSize,
                facets,
                layoutProviderByTile,
                totalAddedHSize,
                totalAddedVSize,
                coordProvider,
                hAxisTheme = hAxisTheme,
                vAxisTheme = vAxisTheme,
            )
        }

        // Create final plot tiles layout infos.


        // Align geom areas of tiles.

        // absolute offsets of tile gom areas.
        val geomOffsetByCol = geomOffsetsByCol(layoutInfos, facetTiles, PANEL_PADDING, facets.colCount)
        val geomOffsetByRow = geomOffsetsByRow(
            layoutInfos, facetTiles, showFacetStrip,
            PANEL_PADDING, facets.rowCount,
            facetsTheme
        )

        val tileBoundsList = ArrayList<DoubleRectangle>()
        val geomOuterBoundsList = ArrayList<DoubleRectangle>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]

            val col = facetTile.col
            val row = facetTile.row
            val geomX = geomOffsetByCol[col]
            val geomY = geomOffsetByRow[row]
            val outerGeomSize = layoutInfo.geomOuterBounds.dimension

            // Tile width
            val tileLabelWidth = if (facetTile.rowLab != null && showFacetStrip) {
                facetTabHeight(facetTile.rowLab, facetsTheme)   // one label on the left side.
            } else {
                0.0
            }

            val axisWidth = if (facetTile.hasVAxis) {
                layoutInfo.axisThicknessY()
            } else {
                0.0
            }

            val tileX = geomX - axisWidth
            val tileWidth = outerGeomSize.x + axisWidth + tileLabelWidth

            // Tile height
            val tileLabelHeight = if (showFacetStrip) {
                facetColHeadHeight(facetTile.colLabs, facetsTheme)
            } else {
                0.0
            }

            val axisHeight = if (facetTile.hasHAxis) {
                layoutInfo.axisThicknessX()
            } else {
                0.0
            }

            val tileY = geomY - tileLabelHeight
            val tileHeight = outerGeomSize.y + tileLabelHeight + axisHeight

//            if (col == 0) {
//                println("[$row][$tileY] $tileHeight = ${geomSize.y} + $tileLabelHeight + $axisHeight")
//            }

            // Absolute bounds...
            val tileBounds = DoubleRectangle(
                DoubleVector(tileX, tileY),
                DoubleVector(tileWidth, tileHeight)
            )
            val geomOuterBounds = DoubleRectangle(
                DoubleVector(geomX, geomY),
                outerGeomSize
            )

            tileBoundsList.add(tileBounds)
            geomOuterBoundsList.add(geomOuterBounds)
        }

        val tilesAreaOrigin = tileBoundsList
            .reduce { b0, b1 -> b0.union(b1) }
            .origin

        // Normalize origin of tilesAreaBounds.
        val originDelta = tilesAreaOrigin.negate()
        val tilesPaddingLeftTop = insets.leftTop

        val finalLayoutInfos = ArrayList<TileLayoutInfo>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]
            val geomInnerBoundsOffset = layoutInfo.geomInnerBounds.origin
                .subtract(layoutInfo.geomOuterBounds.origin)

            val tileBounds = tileBoundsList[index]
            val geomOuterBounds = geomOuterBoundsList[index]
            val geomInnerBounds = DoubleRectangle(
                geomOuterBounds.origin.add(geomInnerBoundsOffset),
                layoutInfo.geomInnerBounds.dimension
            )

            val newLayoutInfo = TileLayoutInfo(
                tilesPaddingLeftTop,
                geomWithAxisBounds = tileBounds.add(originDelta),
                geomOuterBounds = geomOuterBounds.add(originDelta),
                geomInnerBounds = geomInnerBounds.add(originDelta),
                geomContentBounds = geomInnerBounds.add(originDelta),
                layoutInfo.axisInfos,
                hAxisShown = facetTile.hasHAxis,
                vAxisShown = facetTile.hasVAxis,
                trueIndex = facetTile.trueIndex
            )

            finalLayoutInfos.add(
                if (showFacetStrip) {
                    newLayoutInfo.withFacetLabels(facetTile.colLabs, facetTile.rowLab)
                } else {
                    newLayoutInfo
                }
            )
        }

        val plotInsets = Insets(tilesPaddingLeftTop, insets.rightBottom)
        return PlotLayoutInfo(finalLayoutInfos, plotInsets)
    }


    companion object {
        // todo use theme margins
        const val FACET_H_PADDING = 0
        const val FACET_V_PADDING = 3

        private const val PANEL_PADDING = 10.0

        fun facetTabHeight(title: String, theme: FacetsTheme) = titleSize(title, theme).y + 2 * FACET_V_PADDING

        fun facetColHeadHeight(colLabs: List<String>, facetsTheme: FacetsTheme): Double {
            return if (colLabs.isNotEmpty()) {
                colLabs.sumOf { facetTabHeight(it, facetsTheme) }
            } else {
                0.0
            }
        }

        fun titleSize(title: String, theme: FacetsTheme) =
            PlotLayoutUtil.textDimensions(title, PlotLabelSpecFactory.facetText(theme))
    }
}
