/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.LayoutConstants.FACET_PANEL_AXIS_EXPAND

internal class InsideOutTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {
    override val insideOut: Boolean = true

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
            geomWithAxisBounds = geomWithAxisBounds,
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