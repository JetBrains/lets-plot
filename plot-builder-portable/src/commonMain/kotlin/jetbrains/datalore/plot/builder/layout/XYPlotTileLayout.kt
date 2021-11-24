/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.GEOM_MARGIN
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.clipBounds
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.geomBounds
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.maxTickLabelsBounds

internal class XYPlotTileLayout(
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val hDomain: ClosedRange<Double>, // transformed data ranges.
    private val vDomain: ClosedRange<Double>,
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
            xAxisLayout: AxisLayout,
            yAxisLayout: AxisLayout,
            plotSize: DoubleVector,
            hDomain: ClosedRange<Double>,
            vDomain: ClosedRange<Double>,
            coordProvider: CoordProvider
        ): Pair<AxisLayoutInfo, AxisLayoutInfo> {
            val xAxisThickness = xAxisLayout.initialThickness()
            var yAxisInfo = computeYAxisInfo(
                yAxisLayout,
                geomBounds(
                    xAxisThickness,
                    yAxisLayout.initialThickness(),
                    plotSize,
                    hDomain,
                    vDomain,
                    coordProvider
                ),
                coordProvider
            )

            val yAxisThickness = yAxisInfo.axisBounds().dimension.x
            var xAxisInfo = computeXAxisInfo(
                xAxisLayout,
                plotSize,
                geomBounds(
                    xAxisThickness,
                    yAxisThickness,
                    plotSize,
                    hDomain,
                    vDomain,
                    coordProvider
                ),
                coordProvider
            )

            if (xAxisInfo.axisBounds().dimension.y > xAxisThickness) {
                // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
                yAxisInfo = computeYAxisInfo(
                    yAxisLayout,
                    geomBounds(
                        xAxisInfo.axisBounds().dimension.y,
                        yAxisThickness,
                        plotSize,
                        hDomain,
                        vDomain,
                        coordProvider
                    ),
                    coordProvider
                )
            }

            return Pair(xAxisInfo, yAxisInfo)
        }

        private fun computeXAxisInfo(
            axisLayout: AxisLayout,
            plotSize: DoubleVector,
            geomBounds: DoubleRectangle,
            coordProvider: CoordProvider
        ): AxisLayoutInfo {
            val axisLength = geomBounds.dimension.x
            val stretch = axisLength * AXIS_STRETCH_RATIO
            val maxTickLabelsBounds = maxTickLabelsBounds(
                Orientation.BOTTOM,
                stretch,
                geomBounds,
                plotSize
            )
            return axisLayout.doLayout(geomBounds.dimension, maxTickLabelsBounds, coordProvider)
        }

        private fun computeYAxisInfo(
            axisLayout: AxisLayout,
            geomBounds: DoubleRectangle,
            coordProvider: CoordProvider
        ): AxisLayoutInfo {
            return axisLayout.doLayout(geomBounds.dimension, null, coordProvider)
        }
    }
}
