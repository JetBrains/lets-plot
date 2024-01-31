/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.util.GeomAreaInsets

internal class PolarTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {
    override val insideOut: Boolean = false

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        val geomAreaInsets = computeAxisInfos(
            axisLayoutQuad,
            preferredSize,
            hDomain, vDomain,
            marginsLayout,
            coordProvider
        )

        val geomBoundsAfterLayout = TileLayoutUtil.geomOuterBounds(
            geomAreaInsets,
            preferredSize,
            hDomain,
            vDomain,
            marginsLayout,
            coordProvider
        )

        val axisInfos = geomAreaInsets.axisInfoQuad

        val geomWithAxisBounds: DoubleRectangle = DoubleRectangle.LTRB(
            left = axisInfos.left?.axisBoundsAbsolute(geomBoundsAfterLayout)?.left ?: geomBoundsAfterLayout.left,
            top = geomBoundsAfterLayout.top, // polar coord never has top axis
            right = geomBoundsAfterLayout.right, // polar coord never has right axis
            bottom = geomBoundsAfterLayout.bottom, // with polar coord bottom axis is as a part of geom area
        )

        val geomInnerBounds = marginsLayout.toInnerBounds(geomBoundsAfterLayout)

        // sync axis info with new (maybe) geom area size
        val axisInfosNew = axisInfos
            .withHAxisLength(geomInnerBounds.width)
            .withVAxisLength(geomInnerBounds.height)

        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            geomWithAxisBounds = geomWithAxisBounds,
            geomOuterBounds = geomBoundsAfterLayout,
            geomInnerBounds = geomInnerBounds,
            axisInfos = axisInfosNew,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    companion object {
        private fun computeAxisInfos(
            axisLayoutQuad: AxisLayoutQuad,
            plotSize: DoubleVector,
            hDomain: DoubleSpan,
            vDomain: DoubleSpan,
            marginsLayout: GeomMarginsLayout,
            coordProvider: CoordProvider
        ): GeomAreaInsets {
            val insetsInitial = GeomAreaInsets.init(axisLayoutQuad)
            val axisHeightEstim =
                TileLayoutUtil.geomOuterBounds(insetsInitial, plotSize, hDomain, vDomain, marginsLayout, coordProvider)
                    .dimension
                    .let(marginsLayout::toInnerSize)
                    .y / 2 // For polar coord axis height is half of geom height - it starts from the center.

            val insetsVAxis = insetsInitial.layoutVAxis(vDomain, axisHeightEstim)
            val plottingArea = TileLayoutUtil.geomOuterBounds(
                insetsVAxis,
                plotSize,
                hDomain,
                vDomain,
                marginsLayout,
                coordProvider
            )

            val hAxisLength = marginsLayout
                .toInnerBounds(plottingArea)
                .width * 1.5 // h axis in polar coord is rendered as a circle - increase its length to make more tick.

            val insetsHVAxis = insetsVAxis.layoutHAxis(
                hDomain,
                hAxisLength
            )

            // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
            val insetsFinal =
                if ((insetsHVAxis.top + insetsHVAxis.bottom) > (insetsInitial.top + insetsInitial.bottom)) {
                    val geomHeight = TileLayoutUtil.geomOuterBounds(
                        insetsHVAxis,
                        plotSize,
                        hDomain,
                        vDomain,
                        marginsLayout,
                        coordProvider
                    )
                        .dimension
                        .let(marginsLayout::toInnerSize)
                        .y / 2 // For polar coord axis height is half of geom height - it starts from the center.

                    insetsHVAxis.layoutVAxis(vDomain, geomHeight)
                } else {
                    insetsHVAxis
                }

            return insetsFinal
        }
    }
}