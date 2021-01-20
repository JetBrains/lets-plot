/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.abs

internal class FacetGridPlotLayout(
    private val colLabels: List<String>,
    private val rowLabels: List<String>,
    private val tileLayout: TileLayout
) : PlotLayoutBase() {
    private val colCount: Int = if (colLabels.isEmpty()) 1 else colLabels.size
    private val myRowCount: Int
    private val myFaceting: Faceting
    private val myTotalPanelHorizontalPadding: Double
    private val myTotalPanelVerticalPadding: Double

    init {
        setPadding(10.0, 10.0, 0.0, 0.0)

        checkArgument(!(colLabels.isEmpty() && rowLabels.isEmpty()), "No col/row labels")

        myFaceting = when {
            colLabels.isEmpty() -> Faceting.ROW
            rowLabels.isEmpty() -> Faceting.COL
            else -> Faceting.BOTH
        }

//        colCount = if (colLabels.isEmpty()) 1 else colLabels.size
        myRowCount = if (rowLabels.isEmpty()) 1 else rowLabels.size

        myTotalPanelHorizontalPadding = PANEL_PADDING * (colCount - 1)
        myTotalPanelVerticalPadding = PANEL_PADDING * (myRowCount - 1)
    }

    override fun doLayout(preferredSize: DoubleVector): PlotLayoutInfo {
        var tilesAreaSize = DoubleVector(
            preferredSize.x - (paddingLeft + paddingRight),
            preferredSize.y - (paddingTop + paddingBottom)
        )

        val facetTabs = when (myFaceting) {
            Faceting.COL -> DoubleVector(0.0, FACET_TAB_HEIGHT)
            Faceting.ROW -> DoubleVector(FACET_TAB_HEIGHT, 0.0)
            Faceting.BOTH -> DoubleVector(FACET_TAB_HEIGHT, FACET_TAB_HEIGHT)
        }

        tilesAreaSize = tilesAreaSize.subtract(facetTabs)

        // rough estimate (without axis. The final size will be smaller)
        val tileWidth = (tilesAreaSize.x - myTotalPanelHorizontalPadding) / colCount
        val tileHeight = (tilesAreaSize.y - myTotalPanelVerticalPadding) / myRowCount

        // initial layout
        var tileInfo = layoutTile(tileWidth, tileHeight)

        // do 1 or 2 times
        for (i in 0..1) {
            // adjust geom size
            val tilesAreaSizeNew = tilesAreaSize(tileInfo)
            val widthDiff = tilesAreaSize.x - tilesAreaSizeNew.x
            val heightDiff = tilesAreaSize.y - tilesAreaSizeNew.y

            // error 1 px per tile is ok
            if (abs(widthDiff) <= colCount && abs(heightDiff) <= myRowCount) {
                break
            }

            val geomWidth = tileInfo.geomWidth() + widthDiff / colCount
            val newPanelWidth = geomWidth + tileInfo.axisThicknessY()
            val geomHeight = tileInfo.geomHeight() + heightDiff / myRowCount
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
        var offsetY = 0.0
        for (row in 0 until myRowCount) {
            var height = geomHeight
            var geomY = 0.0
            if (row == 0) {
                height += facetTabs.y
                geomY = facetTabs.y
            }
            if (row == myRowCount - 1) {
                height += axisThicknessX
            }

            var offsetX = 0.0
            for (col in 0 until colCount) {
                val xFacetLabel = if (row == 0 && colLabels.size > col)
                    colLabels[col]
                else
                    ""

                val yFacetLabel = if (col == colCount - 1 && rowLabels.size > row)
                    rowLabels[row]
                else
                    ""

                var width = geomWidth
                var geomX = 0.0
                if (col == 0) {
                    width += axisThicknessY
                    geomX = axisThicknessY
                }
                if (col == colCount - 1) {
                    width += facetTabs.x
                }

                val bounds = DoubleRectangle(0.0, 0.0, width, height)
                val geomBounds = DoubleRectangle(geomX, geomY, geomWidth, geomHeight)
                val offset = DoubleVector(offsetX, offsetY)

                val info = TileLayoutInfo(
                    bounds,
                    geomBounds,
                    TileLayoutBase.clipBounds(geomBounds),
                    tileInfo.layoutInfo.xAxisInfo,
                    tileInfo.layoutInfo.yAxisInfo,
                    xAxisShown = row == myRowCount - 1, // show X-axis for bottom row tiles
                    yAxisShown = col == 0                 // show Y-axis for leftmost tiles
                )
                    .withOffset(tilesAreaOffset.add(offset))
                    .withFacetLabels(xFacetLabel, yFacetLabel)

                tileInfos.add(info)

                tilesAreaBounds = tilesAreaBounds.union(info.getAbsoluteBounds(tilesAreaOffset))
                offsetX += width + PANEL_PADDING
            }
            offsetY += height + PANEL_PADDING
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
        val w = tileInfo.geomWidth() * colCount + myTotalPanelHorizontalPadding + tileInfo.axisThicknessY()
        val h = tileInfo.geomHeight() * myRowCount + myTotalPanelVerticalPadding + tileInfo.axisThicknessX()
        return DoubleVector(w, h)
    }

    enum class Faceting {
        COL, ROW, BOTH
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
