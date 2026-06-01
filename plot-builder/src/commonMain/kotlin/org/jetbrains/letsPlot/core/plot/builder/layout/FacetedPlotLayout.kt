/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleInsets
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

internal class FacetedPlotLayout(
    private val facets: PlotFacets,
    private val layoutProviderByTile: List<TileLayoutProvider>,
    private val hAxisTheme: AxisTheme,
    private val vAxisTheme: AxisTheme,
    private val plotTheme: PlotTheme,
    private val facetsTheme: FacetsTheme
) : PlotLayout {
    private val totalAddedHSize: Double = facetsTheme.panelSpacing().x * (facets.colCount - 1)
    private val totalAddedVSize: Double = facetsTheme.panelSpacing().y * (facets.rowCount - 1)

    private val insets: DoubleInsets = plotInsets(plotTheme.plotInset())

    init {
        require(facets.isDefined) { "Undefined facets." }
    }

    override fun layoutByGeomSize(
        geomContentSize: DoubleVector,
        coordProvider: CoordProvider,
        axisSpacer: Thickness
    ): PlotLayoutInfo {
        // In the context of faceted plot, geom content space equal to the plot inner space.
        return layoutByPlotSize(
            plotInnerSize = geomContentSize,
            coordProvider = coordProvider
        )
    }

    override fun layoutByPlotSize(
        plotInnerSize: DoubleVector,
        coordProvider: CoordProvider,
    ): PlotLayoutInfo {
        var tilesAreaSize = DoubleVector(
            plotInnerSize.x - (insets.left + insets.right),
            plotInnerSize.y - (insets.top + insets.bottom)
        )

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
                                FacetStripOrientation.HORIZONTAL
                            )
                            subColLabels[index] = max(labHeight, subColLabels[index] ?: 0.0)
                        }
                    }
                    subColLabels.values.toList()
                }
            totalAddedHeight = facetColTabHeights.values.sumOf { heights ->
                facetColHeadTotalHeight(heights, facetsTheme.horizontalFacetStrip().stripSpacing().y)
            }
        } else {
            facetColTabHeights = emptyMap()
            totalAddedHeight = 0.0
        }

        // max width of row labels
        if (facetsTheme.verticalFacetStrip().showStrip()) {
            facetRowTabWidth = facetTiles
                .mapNotNull { it.rowLab }
                .maxOfOrNull { facetLabelSize(it, facetsTheme.verticalFacetStrip(), FacetStripOrientation.VERTICAL) }
                ?: 0.0
            totalAddedWidth = facetRowTabWidth + facetsTheme.verticalFacetStrip().stripSpacing().x
        } else {
            facetRowTabWidth = 0.0
            totalAddedWidth = 0.0
        }
        val facetColHeadHeightGetter = { facetTile: PlotFacets.FacetTileInfo ->
            facetColTabHeights[facetTile.row]?.let { heights ->
                facetColHeadTotalHeight(heights, facetsTheme.horizontalFacetStrip().stripSpacing().y)
            } ?: facetColHeadTotalHeight(facetTile.colLabs, facetsTheme.horizontalFacetStrip())
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

        // absolute offsets of tile geom areas.
        val geomOffsetByCol = geomOffsetsByCol(
            layoutInfos, facetTiles,
            facetsTheme.panelSpacing().x, facets.colCount
        )

        val geomOffsetByRow = geomOffsetsByRow(
            layoutInfos, facetTiles,
            facetsTheme.horizontalFacetStrip().showStrip(),
            facetsTheme.panelSpacing().y, facets.rowCount,
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
                facetRowTabWidth + facetsTheme.verticalFacetStrip().stripSpacing().x // one label on the left side
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

        // Normalize the origin of tilesAreaBounds.
        val originDelta = tilesAreaOrigin.negate()
        val tilesPaddingLeftTop = insets.leftTop

        val finalLayoutInfos = ArrayList<TileLayoutInfo>()
        for ((index, facetTile) in facetTiles.withIndex()) {
            val layoutInfo = layoutInfos[index]
            val geomInnerBoundsOffset = layoutInfo.geomInnerBounds.origin
                .subtract(layoutInfo.geomOuterBounds.origin)
            val geomContentBoundsOffset = layoutInfo.geomContentBounds.origin
                .subtract(layoutInfo.geomOuterBounds.origin)

            val tileBounds = tileBoundsList[index]
            val geomOuterBounds = geomOuterBoundsList[index]
            val geomInnerBounds = DoubleRectangle(
                geomOuterBounds.origin.add(geomInnerBoundsOffset),
                layoutInfo.geomInnerBounds.dimension
            )
            val geomContentBounds = DoubleRectangle(
                geomOuterBounds.origin.add(geomContentBoundsOffset),
                layoutInfo.geomContentBounds.dimension
            )

            val xLabels = when (facetsTheme.horizontalFacetStrip().showStrip()) {
                true -> {
                    val colHeights = facetColTabHeights[facetTile.row] ?: facetTile.colLabs.map {
                        facetLabelSize(
                            it,
                            facetsTheme.horizontalFacetStrip(),
                            FacetStripOrientation.HORIZONTAL
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
                geomContentBounds = geomContentBounds.add(originDelta),
                layoutInfo.axisInfos,
                hAxisShown = facetTile.hasHAxis,
                vAxisShown = facetTile.hasVAxis,
                trueIndex = facetTile.trueIndex,
                facetXLabels = xLabels,
                facetYLabel = yLabel
            )
            finalLayoutInfos.add(newLayoutInfo)
        }

        val plotInsets = DoubleInsets(tilesPaddingLeftTop, insets.rightBottom)
        return PlotLayoutInfo(finalLayoutInfos, plotInsets)
    }


    companion object {
        // label height + margins
        private fun facetLabelSize(title: String, theme: FacetStripTheme, orientation: FacetStripOrientation): Double {
            val textSize = rotatedTitleSize(title, theme, orientation)
            return when (orientation) {
                FacetStripOrientation.HORIZONTAL -> textSize.y + theme.stripMargins().height
                FacetStripOrientation.VERTICAL -> textSize.x + theme.stripMargins().width
            }
        }

        // Total head sizes: tab size with additional padding

        private fun facetColHeadTotalHeight(colLabs: List<String>, facetsTheme: FacetStripTheme): Double {
            return if (colLabs.isNotEmpty()) {
                colLabs.sumOf { facetLabelSize(it, facetsTheme, FacetStripOrientation.HORIZONTAL) } + facetsTheme.stripSpacing().y
            } else {
                0.0
            }
        }

        fun facetColHeadTotalHeight(
            colLabHeights: List<Double>,
            spacingY: Double
        ): Double = colLabHeights.sum().let { labHeight ->
            if (labHeight > 0) labHeight + spacingY else labHeight
        }

        fun titleSize(title: String, theme: FacetStripTheme) =
            PlotLayoutUtil.textDimensions(title, PlotLabelSpecFactory.facetText(theme))

        private fun rotatedTitleSize(
            title: String,
            theme: FacetStripTheme,
            orientation: FacetStripOrientation
        ): DoubleVector {
            val textSize = titleSize(title, theme)
            val angle = theme.stripTextAngle().takeIf { !it.isNaN() } ?: when (orientation) {
                FacetStripOrientation.HORIZONTAL -> 0.0
                FacetStripOrientation.VERTICAL -> 90.0
            }
            return rotatedRectangleDimensions(textSize, angle)
        }

        private fun rotatedRectangleDimensions(size: DoubleVector, degreeAngle: Double): DoubleVector {
            val angle = toRadians(degreeAngle)
            val sin = abs(sin(angle))
            val cos = abs(cos(angle))

            val w = size.x * cos + size.y * sin
            val h = size.y * cos + size.x * sin

            return DoubleVector(w, h)
        }

        private enum class FacetStripOrientation {
            HORIZONTAL,
            VERTICAL
        }
    }
}
