/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.DoubleVector.Companion.ZERO
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.util.GeomAreaInsets
import kotlin.math.min

internal class PolarTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
    private val panelPadding: Thickness,
) : TileLayout {
    override val insideOut: Boolean = false

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {
        val geomAreaInsets = computeAxisInfos(preferredSize)

        val geomContentBounds = computeGeomContentBounds(geomAreaInsets, preferredSize)
        val geomInnerBounds = panelPadding.inflateRect(geomContentBounds)
        val geomOuterBounds = marginsLayout.toOuterBounds(geomInnerBounds)

        // sync axis info with new (maybe) geom area size
        val axisInfos = geomAreaInsets.axisInfoQuad
        val axisInfosNew = axisInfos
            .withHAxisLength(geomContentBounds.width)
            .withVAxisLength(geomContentBounds.height)

        val geomWithAxisBounds = DoubleRectangle.LTRB(
            left = axisInfos.left?.axisBoundsAbsolute(geomOuterBounds)?.left ?: geomOuterBounds.left,
            top = geomOuterBounds.top, // polar coord never has top axis
            right = geomOuterBounds.right, // polar coord never has right axis
            bottom = geomOuterBounds.bottom, // with polar coord bottom axis is as a part of geom area
        )

        return TileLayoutInfo(
            offset = ZERO,
            geomWithAxisBounds = geomWithAxisBounds,
            geomOuterBounds = geomOuterBounds,
            geomInnerBounds = geomInnerBounds,
            geomPlottingBounds = geomContentBounds,
            axisInfos = axisInfosNew,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    // square area excluding axis, marginal layers and paddings
    private fun computeGeomContentBounds(geomInsets: GeomAreaInsets, plotSize: DoubleVector): DoubleRectangle {
        val expectedGeomOuterBounds = geomInsets.subtractFrom(DoubleRectangle(ZERO, plotSize))
        val expectedGeomInnerBounds = marginsLayout.toInnerBounds(expectedGeomOuterBounds)
        val expectedGeomContentBounds = panelPadding.shrinkRect(expectedGeomInnerBounds)

        val actualContentSize = min(expectedGeomContentBounds.dimension.x, expectedGeomContentBounds.dimension.y)
            .coerceAtLeast(5.0)
            .let { DoubleVector(it, it) }

        return DoubleRectangle(expectedGeomContentBounds.origin, actualContentSize)
    }

    private fun computeAxisInfos(plotSize: DoubleVector): GeomAreaInsets {
        val insetsInitial = GeomAreaInsets.init(axisLayoutQuad)

        // For polar coord axis height is half of geom height - it starts from the center.
        val axisHeightEstim = computeGeomContentBounds(insetsInitial, plotSize).dimension.y / 2

        val insetsVAxis = insetsInitial.layoutVAxis(vDomain, axisHeightEstim)
        val contentBounds = computeGeomContentBounds(insetsVAxis, plotSize)

        // h axis in polar coord is rendered as a circle - increase its length to make more tick.
        val hAxisLength = contentBounds.width * 1.5

        val insetsHVAxis = insetsVAxis.layoutHAxis(hDomain, hAxisLength)

        // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
        val insetsFinal =
            if ((insetsHVAxis.top + insetsHVAxis.bottom) > (insetsInitial.top + insetsInitial.bottom)) {
                // For polar coord axis height is half of geom height - it starts from the center.
                val axisHeight = computeGeomContentBounds(insetsHVAxis, plotSize).dimension.y / 2
                insetsHVAxis.layoutVAxis(vDomain, axisHeight)
            } else {
                insetsHVAxis
            }

        return insetsFinal
    }

}