/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.GEOM_MARGIN
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.geomOuterBounds
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.maxHAxisTickLabelsBounds
import kotlin.math.max

internal class TopDownTileLayout(
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        var (hAxisInfo, vAxisInfo) = computeAxisInfos(
            hAxisLayout,
            vAxisLayout,
            preferredSize,
            hDomain, vDomain,
            marginsLayout,
            coordProvider
        )

        val hAxisThickness = hAxisInfo.axisBounds().dimension.y
        val vAxisThickness = vAxisInfo.axisBounds().dimension.x

        val geomBoundsAfterLayout = geomOuterBounds(
            hAxisThickness,
            vAxisThickness,
            preferredSize,
            hDomain,
            vDomain,
            marginsLayout,
            coordProvider
        )

        // X-axis labels bounds may exceed axis length - adjust
        val geomOuterBounds = geomBoundsAfterLayout.let {
            val hAxisSpan = marginsLayout.toInnerBounds(it).xRange()

            val maxTickLabelsBounds = maxHAxisTickLabelsBounds(
                Orientation.BOTTOM,
                0.0,
                hAxisSpan,
                preferredSize
            )
            val tickLabelsBounds = hAxisInfo.tickLabelsBounds
            val leftOverflow = maxTickLabelsBounds.left - tickLabelsBounds.origin.x
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


            // Fix for (Batik)
            //            org.apache.batik.bridge.BridgeException: null:-1
            //            The attribute "width" of the element <rect> cannot be negative
            newW = max(0.0, newW)

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

        // Combine geom area and x/y-axis
        val geomWithAxisBounds = tileBounds(
            hAxisInfo.axisBounds(),
            vAxisInfo.axisBounds(),
            geomOuterBounds
        )

        val geomInnerBounds = marginsLayout.toInnerBounds(geomOuterBounds)

        // sync axis info with new (maybe) geom area size
        hAxisInfo = hAxisInfo.withAxisLength(geomInnerBounds.width)
        vAxisInfo = vAxisInfo.withAxisLength(geomInnerBounds.height)

        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            bounds = geomWithAxisBounds,
            geomOuterBounds = geomOuterBounds,
            geomInnerBounds = geomInnerBounds,
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
            marginsLayout: GeomMarginsLayout,
            coordProvider: CoordProvider
        ): Pair<AxisLayoutInfo, AxisLayoutInfo> {
            val hAxisThickness = hAxisLayout.initialThickness()
            val geomHeightEstim = geomOuterBounds(
                hAxisThickness,
                vAxisLayout.initialThickness(),
                plotSize,
                hDomain,
                vDomain,
                marginsLayout,
                coordProvider
            ).dimension.let {
                marginsLayout.toInnerSize(it).y
            }

            val vAxisInfoEstim = computeVAxisInfo(vAxisLayout, vDomain, geomHeightEstim)

            val vAxisThickness = vAxisInfoEstim.axisBounds().dimension.x
            val plottingArea = geomOuterBounds(
                hAxisThickness,
                vAxisThickness,
                plotSize,
                hDomain,
                vDomain,
                marginsLayout,
                coordProvider
            )
            val hAxisSpan = marginsLayout.toInnerBounds(plottingArea).xRange()
            val hAxisInfo = computeHAxisInfo(
                hAxisLayout,
                hDomain,
                plotSize,
                hAxisSpan
            )

            // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
            val vAxisInfo = if (hAxisInfo.axisBounds().dimension.y > hAxisThickness) {
                val geomHeight = geomOuterBounds(
                    hAxisInfo.axisBounds().dimension.y,
                    vAxisThickness,
                    plotSize,
                    hDomain,
                    vDomain,
                    marginsLayout,
                    coordProvider
                ).dimension.let {
                    marginsLayout.toInnerSize(it).y
                }

                computeVAxisInfo(vAxisLayout, vDomain, geomHeight)
            } else {
                vAxisInfoEstim
            }

            return Pair(hAxisInfo, vAxisInfo)
        }

        private fun computeHAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            plotSize: DoubleVector,
            axisSpan: DoubleSpan
        ): AxisLayoutInfo {
            val axisLength = axisSpan.length
            val stretch = axisLength * AXIS_STRETCH_RATIO

            val maxTickLabelsBounds = maxHAxisTickLabelsBounds(
                Orientation.BOTTOM,
                stretch,
                axisSpan,
                plotSize
            )
            return axisLayout.doLayout(axisDomain, axisLength, maxTickLabelsBounds)
        }

        private fun computeVAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            axisLength: Double
        ): AxisLayoutInfo {
            return axisLayout.doLayout(axisDomain, axisLength, null)
        }
    }
}
