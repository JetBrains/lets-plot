/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.AxisLayout
import jetbrains.datalore.plot.builder.layout.GeomMarginsLayout
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.geomOuterBounds
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.maxHAxisTickLabelsBounds
import jetbrains.datalore.plot.builder.layout.util.GeomAreaInsets
import kotlin.math.max

internal class TopDownTileLayout(
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        var geomAreaInsets = computeAxisInfos(
            hAxisLayout,
            vAxisLayout,
            preferredSize,
            hDomain, vDomain,
            marginsLayout,
            coordProvider
        )

        val geomBoundsAfterLayout = geomOuterBounds(
            geomAreaInsets,
            preferredSize,
            hDomain,
            vDomain,
            marginsLayout,
            coordProvider
        )

        val (hAxisInfo, vAxisInfo) = geomAreaInsets.hAxisInfo to geomAreaInsets.vAxisInfo

        // X-axis labels bounds may exceed axis length - adjust
        val geomOuterBounds = geomBoundsAfterLayout.let {
            val hAxisSpan = marginsLayout.toInnerBounds(it).xRange()

            val maxTickLabelsBounds = maxHAxisTickLabelsBounds(
                hAxisLayout.orientation,
                0.0,
                hAxisSpan,
                preferredSize
            )
            val tickLabelsBounds = hAxisInfo.tickLabelsBounds
            val leftOverflow = maxTickLabelsBounds.left - tickLabelsBounds.left
            val rightOverflow = tickLabelsBounds.left + tickLabelsBounds.width - maxTickLabelsBounds.right
            var newX = it.left
            var newW = it.width
            if (leftOverflow > 0) {
                newX = it.left + leftOverflow
                newW = it.width - leftOverflow
            }

            if (rightOverflow > 0) {
                newW = newW - rightOverflow
            }


            // Fix for (Batik)
            //            org.apache.batik.bridge.BridgeException: null:-1
            //            The attribute "width" of the element <rect> cannot be negative
            newW = max(0.0, newW)

            val boundsNew = DoubleRectangle(
                newX, it.top,
                newW, it.height
            )

            if (boundsNew != geomBoundsAfterLayout) {
                val sizeNew = coordProvider.adjustGeomSize(hDomain, vDomain, boundsNew.dimension)
                DoubleRectangle(boundsNew.origin, sizeNew)
            } else {
                boundsNew
            }
        }

        // Combine geom area and x/y-axis
//        val geomWithAxisBounds = tileBounds(
//            hAxisInfo.axisBounds(),
//            vAxisInfo.axisBounds(),
//            geomOuterBounds
//        )
        val geomWithAxisBounds = geomOuterBounds
            .union(hAxisInfo.axisBoundsAbsolute(geomOuterBounds))
            .union(vAxisInfo.axisBoundsAbsolute(geomOuterBounds))

        val geomInnerBounds = marginsLayout.toInnerBounds(geomOuterBounds)

        // sync axis info with new (maybe) geom area size
        val hAxisInfoNew = hAxisInfo.withAxisLength(geomInnerBounds.width)
        val vAxisInfoNew = vAxisInfo.withAxisLength(geomInnerBounds.height)

        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            bounds = geomWithAxisBounds,
            geomOuterBounds = geomOuterBounds,
            geomInnerBounds = geomInnerBounds,
            hAxisInfoNew,
            vAxisInfoNew,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    companion object {
        private const val AXIS_STRETCH_RATIO = 0.1  // allow 10% axis flexibility (on each end)

//        private fun tileBounds(
//            xAxisBounds: DoubleRectangle,
//            yAxisBounds: DoubleRectangle,
//            geomBounds: DoubleRectangle
//        ): DoubleRectangle {
//            // Can't just union bounds because
//            // x-axis has zero origin
//            // y-axis has negative origin
//            val leftTop = DoubleVector(
//                geomBounds.left - yAxisBounds.width,
//                geomBounds.top - GEOM_MARGIN
//            )
//            val rightBottom = DoubleVector(
//                geomBounds.right + GEOM_MARGIN,
//                geomBounds.bottom + xAxisBounds.height
//            )
//            return DoubleRectangle(leftTop, rightBottom.subtract(leftTop))
//        }

        private fun computeAxisInfos(
            hAxisLayout: AxisLayout,
            vAxisLayout: AxisLayout,
            plotSize: DoubleVector,
            hDomain: DoubleSpan,
            vDomain: DoubleSpan,
            marginsLayout: GeomMarginsLayout,
            coordProvider: CoordProvider
        ): GeomAreaInsets {
            val insetsInitial = GeomAreaInsets.init(hAxisLayout, vAxisLayout)
            val geomHeightEstim = geomOuterBounds(
                insetsInitial,
                plotSize,
                hDomain,
                vDomain,
                marginsLayout,
                coordProvider
            ).dimension.let {
                marginsLayout.toInnerSize(it).y
            }

            val insetsVAxis = insetsInitial.layoutVAxis(vDomain, geomHeightEstim)
            val plottingArea = geomOuterBounds(
                insetsVAxis,
                plotSize,
                hDomain,
                vDomain,
                marginsLayout,
                coordProvider
            )
            val hAxisSpan = marginsLayout.toInnerBounds(plottingArea).xRange()
            val insetsHVAxis = insetsVAxis.layoutHAxis(
                hDomain,
                plotSize,
                hAxisSpan
            )

            // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
            val insetsFinal = if (insetsHVAxis.hAxisThickness > insetsInitial.hAxisThickness) {
                val geomHeight = geomOuterBounds(
                    insetsHVAxis,
                    plotSize,
                    hDomain,
                    vDomain,
                    marginsLayout,
                    coordProvider
                ).dimension.let {
                    marginsLayout.toInnerSize(it).y
                }

                insetsHVAxis.layoutVAxis(vDomain, geomHeight)
            } else {
                insetsHVAxis
            }

            return insetsFinal
        }
    }
}
