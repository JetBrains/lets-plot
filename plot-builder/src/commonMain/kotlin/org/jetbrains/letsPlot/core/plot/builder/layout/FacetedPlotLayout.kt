/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetStripTheme
import org.jetbrains.letsPlot.core.plot.base.theme.FacetsTheme
import org.jetbrains.letsPlot.core.plot.base.theme.PlotTheme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayoutUtil.geomOffsetsByCol
import org.jetbrains.letsPlot.core.plot.builder.layout.FacetedPlotLayoutUtil.geomOffsetsByRow
import org.jetbrains.letsPlot.core.plot.builder.layout.PlotLayoutUtil.plotInsets
import org.jetbrains.letsPlot.core.plot.builder.layout.facet.FixedScalesTilesLayouter
import org.jetbrains.letsPlot.core.plot.builder.layout.facet.FreeScalesTilesLayouter
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets
import kotlin.math.max

internal class FacetedPlotLayout(
    private val facets: PlotFacets,
    private val layoutProviderByTile: List<TileLayoutProvider>,
    private val hAxisTheme: AxisTheme,
    private val vAxisTheme: AxisTheme,
    private val plotTheme: PlotTheme,
    private val facetsTheme: FacetsTheme
) : PlotLayout {
    private val totalAddedHSize: Double = PANEL_PADDING * (facets.colCount - 1)
    private val totalAddedVSize: Double = PANEL_PADDING * (facets.rowCount - 1)

    private val insets: Insets = plotInsets(plotTheme.plotInset())

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

        // Calc sizes of facet tabs
        val facetColTabHeights: Map<Int, List<Double>> // facet tab heights for column labels
        val facetRowTabWidth: Double                   // max width of row labels
        val totalAddedHeight: Double                   // summary height of horizontal facet tabs
        val totalAddedWidth: Double                    // width with padding of facet vertical tabs

        // facet tab heights for column labels
        if (facetsTheme.horizontalFacetStrip().showStrip()) {
            facetColTabHeights = facetTiles
                .groupBy({ it.row }, { it.colLabs })
                .mapValues {
                    val subColLabels = HashMap<Int, Double>()
                    it.value.forEach { colLabs ->
                        colLabs.forEachIndexed { index, colLab ->
                            val labHeight = facetLabelSize(
                                colLab,
                                facetsTheme.horizontalFacetStrip(),
                                marginSize = Thickness::height
                            )
                            subColLabels[index] = max(labHeight, subColLabels[index] ?: 0.0)
                        }
                    }
                    subColLabels.values.toList()
                }
            totalAddedHeight = facetColTabHeights.values.sumOf(::facetColHeadTotalHeight)
        } else {
            facetColTabHeights = emptyMap()
            totalAddedHeight = 0.0
        }

        // max width of row labels
        if (facetsTheme.verticalFacetStrip().showStrip()) {
            facetRowTabWidth = facetTiles
                .mapNotNull { it.rowLab }
                .maxOfOrNull { facetLabelSize(it, facetsTheme.verticalFacetStrip(), marginSize = Thickness::width) }
                ?: 0.0
            totalAddedWidth = facetRowTabWidth + FACET_PADDING
        } else {
            facetRowTabWidth = 0.0
            totalAddedWidth = 0.0
        }
        val facetColHeadHeightGetter = { facetTile: PlotFacets.FacetTileInfo ->
            facetColTabHeights[facetTile.row]?.let(::facetColHeadTotalHeight)
                ?: facetColHeadTotalHeight(facetTile.colLabs, facetsTheme.horizontalFacetStrip())
        }

        val labsTotalDim = DoubleVector(totalAddedWidth, totalAddedHeight)

        tilesAreaSize = tilesAreaSize.subtract(labsTotalDim)

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
            layoutInfos, facetTiles,
            facetsTheme.horizontalFacetStrip().showStrip(),
            PANEL_PADDING, facets.rowCount,
            facetColHeadHeightGetter
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
            val tileLabelWidth = if (facetTile.rowLab != null && facetsTheme.verticalFacetStrip().showStrip()) {
                facetRowTabWidth + FACET_PADDING // one label on the left side
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
            val tileLabelHeight = if (facetsTheme.horizontalFacetStrip().showStrip()) {
                facetColHeadHeightGetter(facetTile)
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

            val xLabels = when (facetsTheme.horizontalFacetStrip().showStrip()) {
                 true -> {
                    val colHeights = facetColTabHeights[facetTile.row] ?: facetTile.colLabs.map {
                        facetLabelSize(
                            it,
                            facetsTheme.horizontalFacetStrip(),
                            marginSize = Thickness::height
                        )
                    }
                    facetTile.colLabs.zip(colHeights)
                }
                false -> emptyList()
            }
            val yLabel = when (facetsTheme.verticalFacetStrip().showStrip()) {
                true -> facetTile.rowLab?.let { it to facetRowTabWidth }
                false -> null
            }
            val newLayoutInfo = TileLayoutInfo(
                tilesPaddingLeftTop,
                geomWithAxisBounds = tileBounds.add(originDelta),
                geomOuterBounds = geomOuterBounds.add(originDelta),
                geomInnerBounds = geomInnerBounds.add(originDelta),
                geomContentBounds = geomInnerBounds.add(originDelta),
                layoutInfo.axisInfos,
                hAxisShown = facetTile.hasHAxis,
                vAxisShown = facetTile.hasVAxis,
                trueIndex = facetTile.trueIndex,
                facetXLabels = xLabels,
                facetYLabel = yLabel
            )
            finalLayoutInfos.add(newLayoutInfo)
        }

        val plotInsets = Insets(tilesPaddingLeftTop, insets.rightBottom)
        return PlotLayoutInfo(finalLayoutInfos, plotInsets)
    }


    companion object {
        const val FACET_PADDING = 3  // space between panel and facet title
        private const val PANEL_PADDING = 10.0

        // label height + margins
        fun facetLabelSize(title: String, theme: FacetStripTheme, marginSize: (Thickness) -> Double) =
            titleSize(title, theme).y + marginSize(theme.stripMargins())

        // Total head sizes: tab size with additional padding

        private fun facetColHeadTotalHeight(colLabs: List<String>, facetsTheme: FacetStripTheme): Double {
            return if (colLabs.isNotEmpty()) {
                colLabs.sumOf { facetLabelSize(it, facetsTheme, Thickness::height) } + FACET_PADDING
            } else {
                0.0
            }
        }

        fun facetColHeadTotalHeight(colLabHeights: List<Double>) = colLabHeights.sum().let { labHeight ->
            if (labHeight > 0) labHeight + FACET_PADDING else labHeight
        }

        fun titleSize(title: String, theme: FacetStripTheme) =
            PlotLayoutUtil.textDimensions(title, PlotLabelSpecFactory.facetText(theme))
    }
}
