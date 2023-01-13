/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.LayoutConstants.FACET_PANEL_AXIS_EXPAND

internal class InsideOutTileLayout constructor(
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun doLayout(geomSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        val geomOuterBounds = DoubleRectangle(DoubleVector.ZERO, geomSize)
        val geomInnerBounds = marginsLayout.toInnerBounds(geomOuterBounds)

        var (hAxisInfo, vAxisInfo) = computeAxisInfos(
            hAxisLayout,
            vAxisLayout,
            geomSize = geomInnerBounds.dimension,
            hDomain, vDomain,
        )

        // Combine geom area and x/y-axis
        val geomWithAxisBounds = geomOuterBounds
            .union(hAxisInfo.axisBoundsAbsolute(geomOuterBounds))
            .union(vAxisInfo.axisBoundsAbsolute(geomOuterBounds))


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
        private fun computeAxisInfos(
            hAxisLayout: AxisLayout,
            vAxisLayout: AxisLayout,
            geomSize: DoubleVector,
            hDomain: DoubleSpan,
            vDomain: DoubleSpan,
        ): Pair<AxisLayoutInfo, AxisLayoutInfo> {
            val geomBounds = DoubleRectangle(DoubleVector.ZERO, geomSize)
            var hAxisInfo = computeHAxisInfo(
                hAxisLayout,
                hDomain,
                geomBounds,
            )

            var vAxisInfo = computeVAxisInfo(
                vAxisLayout,
                vDomain,
                geomBounds
            )

            return Pair(hAxisInfo, vAxisInfo)
        }

        private fun computeHAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            geomBounds: DoubleRectangle
        ): AxisLayoutInfo {
            val axisSpan = geomBounds.xRange()
            val axisLength = axisSpan.length
            return axisLayout.doLayout(axisDomain, axisLength, FACET_PANEL_AXIS_EXPAND)
        }

        private fun computeVAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            geomBounds: DoubleRectangle
        ): AxisLayoutInfo {
            return axisLayout.doLayout(axisDomain, geomBounds.dimension.y, FACET_PANEL_AXIS_EXPAND)
        }
    }
}