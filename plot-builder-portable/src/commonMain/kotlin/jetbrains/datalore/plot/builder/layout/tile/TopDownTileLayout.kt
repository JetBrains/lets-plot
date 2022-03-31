/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.AxisLayout
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.GEOM_MARGIN
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.clipBounds
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.geomBounds
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.maxTickLabelsBounds

internal class TopDownTileLayout(
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
) : TileLayout {

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        var (hAxisInfo, vAxisInfo) = computeAxisInfos(
            hAxisLayout,
            vAxisLayout,
            preferredSize,
            hDomain, vDomain,
            coordProvider
        )

        val hAxisThickness = hAxisInfo.axisBounds().dimension.y
        val vAxisThickness = vAxisInfo.axisBounds().dimension.x

        val geomBoundsAfterLayout = geomBounds(
            hAxisThickness,
            vAxisThickness,
            preferredSize,
            hDomain,
            vDomain,
            coordProvider
        )

        // X-axis labels bounds may exceed axis length - adjust
        val geomBounds = geomBoundsAfterLayout.let {
            val maxTickLabelsBounds = maxTickLabelsBounds(
                Orientation.BOTTOM,
                0.0,
                it,
                preferredSize
            )
            val tickLabelsBounds = hAxisInfo.tickLabelsBounds
            val leftOverflow = maxTickLabelsBounds.left - tickLabelsBounds!!.origin.x
            val rightOverflow = tickLabelsBounds.origin.x + tickLabelsBounds.dimension.x - maxTickLabelsBounds.right
            var newX = it.origin.x
            var newW = it.dimension.x
            if (leftOverflow > 0) {
                newX = it.origin.x + leftOverflow
                newW = it.dimension.x - leftOverflow
            }

            if (rightOverflow > 0) {
                newW = newW - rightOverflow
            }

            val boundsNew = DoubleRectangle(
                newX, it.origin.y,
                newW, it.dimension.y
            )

            if (boundsNew != geomBoundsAfterLayout) {
                val sizeNew = coordProvider.adjustGeomSize(hDomain, vDomain, boundsNew.dimension)
                DoubleRectangle(boundsNew.origin, sizeNew)
            } else {
                boundsNew
            }
        }

        // Combine geom area and x/y axis
        val geomWithAxisBounds = tileBounds(
            hAxisInfo.axisBounds(),
            vAxisInfo.axisBounds(),
            geomBounds
        )

        // sync axis info with new (may be) geom area size
        hAxisInfo = hAxisInfo.withAxisLength(geomBounds.width).build()
        vAxisInfo = vAxisInfo.withAxisLength(geomBounds.height).build()

        return TileLayoutInfo(
            geomWithAxisBounds,
            geomBounds,
            clipBounds(geomBounds),
            hAxisInfo,
            vAxisInfo,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    companion object {
        private const val AXIS_STRETCH_RATIO = 0.1  // allow 10% axis flexibility (on each end)

        private fun tileBounds(
            xAxisBounds: DoubleRectangle,
            yAxisBounds: DoubleRectangle,
            geomBounds: DoubleRectangle
        ): DoubleRectangle {
            // Can't just union bounds because
            // x-axis has zero origin
            // y-axis has negative origin
            val leftTop = DoubleVector(
                geomBounds.left - yAxisBounds.width,
                geomBounds.top - GEOM_MARGIN
            )
            val rightBottom = DoubleVector(
                geomBounds.right + GEOM_MARGIN,
                geomBounds.bottom + xAxisBounds.height
            )
            return DoubleRectangle(leftTop, rightBottom.subtract(leftTop))
        }

        private fun computeAxisInfos(
            hAxisLayout: AxisLayout,
            vAxisLayout: AxisLayout,
            plotSize: DoubleVector,
            hDomain: DoubleSpan,
            vDomain: DoubleSpan,
            coordProvider: CoordProvider
        ): Pair<AxisLayoutInfo, AxisLayoutInfo> {
            val hAxisThickness = hAxisLayout.initialThickness()
            var vAxisInfo = computeVAxisInfo(
                vAxisLayout,
                vDomain,
                geomBounds(
                    hAxisThickness,
                    vAxisLayout.initialThickness(),
                    plotSize,
                    hDomain,
                    vDomain,
                    coordProvider
                )
            )

            val vAxisThickness = vAxisInfo.axisBounds().dimension.x
            var hAxisInfo = computeHAxisInfo(
                hAxisLayout,
                hDomain,
                plotSize,
                geomBounds(
                    hAxisThickness,
                    vAxisThickness,
                    plotSize,
                    hDomain,
                    vDomain,
                    coordProvider
                )
            )

            if (hAxisInfo.axisBounds().dimension.y > hAxisThickness) {
                // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
                vAxisInfo = computeVAxisInfo(
                    vAxisLayout,
                    vDomain,
                    geomBounds(
                        hAxisInfo.axisBounds().dimension.y,
                        vAxisThickness,
                        plotSize,
                        hDomain,
                        vDomain,
                        coordProvider
                    )
                )
            }

            return Pair(hAxisInfo, vAxisInfo)
        }

        private fun computeHAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            plotSize: DoubleVector,
            geomBounds: DoubleRectangle
        ): AxisLayoutInfo {
            val axisLength = geomBounds.dimension.x
            val stretch = axisLength * AXIS_STRETCH_RATIO
            val maxTickLabelsBounds = maxTickLabelsBounds(
                Orientation.BOTTOM,
                stretch,
                geomBounds,
                plotSize
            )
            return axisLayout.doLayout(axisDomain, axisLength, maxTickLabelsBounds)
        }

        private fun computeVAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            geomBounds: DoubleRectangle
        ): AxisLayoutInfo {
            return axisLayout.doLayout(axisDomain, geomBounds.dimension.y, null)
        }
    }
}
