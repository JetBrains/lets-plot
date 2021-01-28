/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import kotlin.math.abs

internal class FacetGridPlotLayout(
    private val facets: PlotFacets,
    private val tileLayout: TileLayout
) : PlotLayoutBase() {
    private val totalPanelHorizontalPadding: Double = PANEL_PADDING * (facets.colCount - 1)
    private val totalPanelVerticalPadding: Double = PANEL_PADDING * (facets.rowCount - 1)

    init {
        setPadding(10.0, 10.0, 0.0, 0.0)

        require(facets.isDefined) { "Undefined facets." }
    }

    override fun doLayout(preferredSize: DoubleVector): PlotLayoutInfo {
        var tilesAreaSize = DoubleVector(
            preferredSize.x - (paddingLeft + paddingRight),
            preferredSize.y - (paddingTop + paddingBottom)
        )

        val facetTiles = facets.tileInfos()
        val labsInCol = facetTiles.filter { it.colLabs.isNotEmpty() }
            .map { it.row }.distinct().count()
        val labsInRow = if (facetTiles.any { it.rowLab != null }) 1 else 0


        val labsTotalDim = DoubleVector(labsInRow * FACET_TAB_HEIGHT, labsInCol * FACET_TAB_HEIGHT)
        tilesAreaSize = tilesAreaSize.subtract(labsTotalDim)

        // rough estimate (without axis. The final size will be smaller)
        val tileWidth = (tilesAreaSize.x - totalPanelHorizontalPadding) / facets.colCount
        val tileHeight = (tilesAreaSize.y - totalPanelVerticalPadding) / facets.rowCount

        // initial layout
        var tileInfo = layoutTile(tileWidth, tileHeight)

        // do 1 or 2 times
        for (i in 0..1) {
            // adjust geom size
            val tilesAreaSizeNew = tilesAreaSize(tileInfo)
            val widthDiff = tilesAreaSize.x - tilesAreaSizeNew.x
            val heightDiff = tilesAreaSize.y - tilesAreaSizeNew.y

            // error 1 px per tile is ok
            if (abs(widthDiff) <= facets.colCount && abs(heightDiff) <= facets.rowCount) {
                break
            }

            val geomWidth = tileInfo.geomWidth() + widthDiff / facets.colCount
            val newPanelWidth = geomWidth + tileInfo.axisThicknessY()
            val geomHeight = tileInfo.geomHeight() + heightDiff / facets.rowCount
            val newPanelHeight = geomHeight + tileInfo.axisThicknessX()

            // re-layout
            tileInfo = layoutTile(newPanelWidth, newPanelHeight)
        }

        // create final plot tiles layout infos

        val axisThicknessX = tileInfo.axisThicknessX()
        val axisThicknessY = tileInfo.axisThicknessY()
        val geomWidth = tileInfo.geomWidth()
        val geomHeight = tileInfo.geomHeight()

        var tilesAreaBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        val tilesAreaOffset = DoubleVector(paddingLeft, paddingTop)
        val tileInfos = ArrayList<TileLayoutInfo>()

        var offsetX = 0.0
        var offsetY = 0.0
        var currRow = 0
        var prevHeight = 0.0

        for (facetTile in facetTiles) {
            var width = geomWidth
            var geomX = 0.0
            if (facetTile.yAxis) {
                width += axisThicknessY
                geomX = axisThicknessY
            }
            if (facetTile.rowLab != null) {
                width += FACET_TAB_HEIGHT
            }

            var height = geomHeight
            var geomY = 0.0
            if (facetTile.xAxis && facetTile.row == facets.rowCount - 1) {   // bottom row only
                height += axisThicknessX
            }
            val addedHeight = FACET_TAB_HEIGHT * facetTile.colLabs.size
            height += addedHeight
            geomY = addedHeight

            val bounds = DoubleRectangle(0.0, 0.0, width, height)
            val geomBounds = DoubleRectangle(geomX, geomY, geomWidth, geomHeight)

            val row = facetTile.row
            if (row > currRow) {
                currRow = row
                offsetY += prevHeight + PANEL_PADDING
            }
            prevHeight = height

            val col = facetTile.col
            if (col == 0) {
                offsetX = 0.0
            }

            val offset = DoubleVector(offsetX, offsetY)
            offsetX += width + PANEL_PADDING

            val info = TileLayoutInfo(
                bounds,
                geomBounds,
                TileLayoutBase.clipBounds(geomBounds),
                tileInfo.layoutInfo.xAxisInfo,
                tileInfo.layoutInfo.yAxisInfo,
                xAxisShown = facetTile.xAxis,
                yAxisShown = facetTile.yAxis
            )
                .withOffset(tilesAreaOffset.add(offset))
                .withFacetLabels(facetTile.colLabs, facetTile.rowLab)

            tileInfos.add(info)

            tilesAreaBounds = tilesAreaBounds.union(info.getAbsoluteBounds(tilesAreaOffset))
        }

        val plotSize = DoubleVector(
            tilesAreaBounds.right + paddingRight,
            tilesAreaBounds.height + paddingBottom
        )

        return PlotLayoutInfo(tileInfos, plotSize)
    }

    private fun layoutTile(tileWidth: Double, tileHeight: Double): MyTileInfo {
        val layoutInfo = tileLayout.doLayout(DoubleVector(tileWidth, tileHeight))
        return MyTileInfo(layoutInfo)
    }

    private fun tilesAreaSize(tileInfo: MyTileInfo): DoubleVector {
        val w = tileInfo.geomWidth() * facets.colCount + totalPanelHorizontalPadding + tileInfo.axisThicknessY()
        val h = tileInfo.geomHeight() * facets.rowCount + totalPanelVerticalPadding + tileInfo.axisThicknessX()
        return DoubleVector(w, h)
    }

    private class MyTileInfo internal constructor(internal val layoutInfo: TileLayoutInfo) {

        internal fun axisThicknessX(): Double {
            return layoutInfo.bounds.bottom - layoutInfo.geomBounds.bottom
        }

        internal fun axisThicknessY(): Double {
            return layoutInfo.geomBounds.left - layoutInfo.bounds.left
        }

        internal fun geomWidth(): Double {
            return layoutInfo.geomBounds.width
        }

        internal fun geomHeight(): Double {
            return layoutInfo.geomBounds.height
        }
    }

    companion object {
        private const val FACET_TAB_HEIGHT = 30.0
        private const val PANEL_PADDING = 10.0
    }
}
