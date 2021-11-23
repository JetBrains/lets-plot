/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.GEOM_MARGIN
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.GEOM_MIN_SIZE
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.clipBounds
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.geomBounds
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.maxTickLabelsBounds

internal class XYPlotTileLayout(
    private val xAxisLayout: AxisLayout,
    private val yAxisLayout: AxisLayout
) : TileLayout {

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        var (xAxisInfo, yAxisInfo) = computeAxisInfos(
            xAxisLayout,
            yAxisLayout,
            preferredSize,
            coordProvider
        )

        var geomBounds = geomBounds(
            xAxisThickness = xAxisInfo.axisBounds().dimension.y,
            yAxisThickness = yAxisInfo.axisBounds().dimension.x,
            preferredSize
        )

        // X-axis labels bounds may exceed axis length - adjust
        run {
            val maxTickLabelsBounds = maxTickLabelsBounds(
                Orientation.BOTTOM,
                0.0,
                geomBounds,
                preferredSize
            )
            val tickLabelsBounds = xAxisInfo.tickLabelsBounds
            val leftOverflow = maxTickLabelsBounds.left - tickLabelsBounds!!.origin.x
            val rightOverflow = tickLabelsBounds.origin.x + tickLabelsBounds.dimension.x - maxTickLabelsBounds.right
            if (leftOverflow > 0) {
                geomBounds = DoubleRectangle(
                    geomBounds.origin.x + leftOverflow,
                    geomBounds.origin.y,
                    geomBounds.dimension.x - leftOverflow,
                    geomBounds.dimension.y
                )
            }
            if (rightOverflow > 0) {
                geomBounds = DoubleRectangle(
                    geomBounds.origin.x,
                    geomBounds.origin.y,
                    geomBounds.dimension.x - rightOverflow,
                    geomBounds.dimension.y
                )
            }
        }

        geomBounds = geomBounds.union(
            DoubleRectangle(geomBounds.origin, GEOM_MIN_SIZE)
        )

        // Combine geom area and x/y axis
        val geomWithAxisBounds =
            tileBounds(
                xAxisInfo.axisBounds(),
                yAxisInfo.axisBounds(),
                geomBounds
            )

        // sync axis info with new (may be) geom area size
        xAxisInfo = xAxisInfo.withAxisLength(geomBounds.width).build()
        yAxisInfo = yAxisInfo.withAxisLength(geomBounds.height).build()

        return TileLayoutInfo(
            geomWithAxisBounds,
            geomBounds,
            clipBounds(geomBounds),
            xAxisInfo,
            yAxisInfo,
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
            coordProvider: CoordProvider
        ): Pair<AxisLayoutInfo, AxisLayoutInfo> {
            val xAxisThickness = xAxisLayout.initialThickness()
            var yAxisInfo = computeYAxisInfo(
                yAxisLayout,
                geomBounds(
                    xAxisThickness,
                    yAxisLayout.initialThickness(),
                    plotSize
                ),
                coordProvider
            )

            val yAxisThickness = yAxisInfo.axisBounds().dimension.x
            var xAxisInfo = computeXAxisInfo(
                xAxisLayout,
                plotSize, geomBounds(
                    xAxisThickness,
                    yAxisThickness,
                    plotSize
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
                        plotSize
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
