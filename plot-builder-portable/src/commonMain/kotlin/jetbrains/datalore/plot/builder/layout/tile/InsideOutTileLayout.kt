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

internal class InsideOutTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun doLayout(geomSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        val geomOuterBounds = DoubleRectangle(DoubleVector.ZERO, geomSize)
        val geomInnerBounds = marginsLayout.toInnerBounds(geomOuterBounds)

        var axisInfos = computeAxisInfos(
            axisLayoutQuad,
            geomSize = geomInnerBounds.dimension,
            hDomain, vDomain,
        )

        // Combine geom area and x/y-axis
        val (l, r, t, b) = axisInfos
        val axisBounds = listOfNotNull(l, r, t, b)
            .map {
                it.axisBoundsAbsolute(geomOuterBounds)
            }

        val geomWithAxisBounds = axisBounds.fold(geomOuterBounds) { a, e ->
            a.union(e)
        }


        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            bounds = geomWithAxisBounds,
            geomOuterBounds = geomOuterBounds,
            geomInnerBounds = geomInnerBounds,
            axisInfos = axisInfos,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    companion object {
        private fun computeAxisInfos(
            axisLayoutQuad: AxisLayoutQuad,
            geomSize: DoubleVector,
            hDomain: DoubleSpan,
            vDomain: DoubleSpan,
        ): AxisLayoutInfoQuad {
            val geomBounds = DoubleRectangle(DoubleVector.ZERO, geomSize)
            return AxisLayoutInfoQuad(
                left = axisLayoutQuad.left?.let { axisLayout -> computeVAxisInfo(axisLayout, vDomain, geomBounds) },
                right = axisLayoutQuad.right?.let { axisLayout -> computeVAxisInfo(axisLayout, vDomain, geomBounds) },
                top = axisLayoutQuad.top?.let { axisLayout -> computeHAxisInfo(axisLayout, hDomain, geomBounds) },
                bottom = axisLayoutQuad.bottom?.let { axisLayout -> computeHAxisInfo(axisLayout, hDomain, geomBounds) }
            )
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