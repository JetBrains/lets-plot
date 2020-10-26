/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.XYPlotLayoutUtil.maxTickLabelsBounds

internal class XYPlotTileLayout(
    private val myXAxisLayout: AxisLayout,
    private val myYAxisLayout: AxisLayout
) : TileLayoutBase() {

    override fun doLayout(preferredSize: DoubleVector): TileLayoutInfo {
        var xAxisThickness = myXAxisLayout.initialThickness()
        var yAxisThickness = myYAxisLayout.initialThickness()

        var geomBounds = geomBounds(
            xAxisThickness,
            yAxisThickness,
            preferredSize
        )

        var xAxisInfo: AxisLayoutInfo? = null
        var yAxisInfo: AxisLayoutInfo? = null

        var doX = true
        while (doX) {
            var doY = false
            doX = false
            run {
                val axisLength = geomBounds.dimension.x
                val stretch = axisLength * AXIS_STRETCH_RATIO
                val maxTickLabelsBounds = maxTickLabelsBounds(
                    Orientation.BOTTOM,
                    stretch,
                    geomBounds,
                    preferredSize
                )
                xAxisInfo = myXAxisLayout.doLayout(geomBounds.dimension, maxTickLabelsBounds)
                val axisThicknessNew = xAxisInfo!!.axisBounds().dimension.y
                if (axisThicknessNew > xAxisThickness) {
                    doY = true // do Y if X got tallier
                    geomBounds =
                        geomBounds(
                            axisThicknessNew,
                            yAxisThickness,
                            preferredSize
                        )
                }
                xAxisThickness = axisThicknessNew
            }

            if (doY || yAxisInfo == null) {
                yAxisInfo = myYAxisLayout.doLayout(geomBounds.dimension, null)
                val axisThicknessNew = yAxisInfo.axisBounds().dimension.x
                if (axisThicknessNew > yAxisThickness) {
                    doX = true // do X again if Y got wider
                    geomBounds =
                        geomBounds(
                            xAxisThickness,
                            axisThicknessNew,
                            preferredSize
                        )
                }
                yAxisThickness = axisThicknessNew
            }
        }

        // X-axis labels bounds may exceed axis length - adjust
        run {
            val maxTickLabelsBounds = maxTickLabelsBounds(
                Orientation.BOTTOM,
                0.0,
                geomBounds,
                preferredSize
            )
            val tickLabelsBounds = xAxisInfo!!.tickLabelsBounds
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
                xAxisInfo!!.axisBounds(),
                yAxisInfo!!.axisBounds(),
                geomBounds
            )

        // sync axis info with new (may be) geom area size
        xAxisInfo = xAxisInfo!!.withAxisLength(geomBounds.width).build()
        yAxisInfo = yAxisInfo.withAxisLength(geomBounds.height).build()

        return TileLayoutInfo(
            geomWithAxisBounds,
            geomBounds,
            clipBounds(geomBounds),
            xAxisInfo!!,
            yAxisInfo
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
    }
}
