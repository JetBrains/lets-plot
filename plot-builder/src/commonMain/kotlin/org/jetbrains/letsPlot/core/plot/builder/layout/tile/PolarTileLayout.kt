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
        val axisInfos = geomAreaInsets.axisInfoQuad

        val preferredGeomOuterBounds = geomAreaInsets.subtractFrom(DoubleRectangle(ZERO, preferredSize))
        val preferredGeomInnerBounds = marginsLayout.toInnerBounds(preferredGeomOuterBounds)
        val preferredGeomPlottingSize = preferredGeomInnerBounds.dimension.subtract(panelPadding.size)

        val actualGeomInnerSize = min(preferredGeomPlottingSize.x, preferredGeomPlottingSize.y)
            .let { DoubleVector(it, it) }
            .add(panelPadding.size)

        // Reverse transformation to get actual geom outer bounds.
        val actualGeomOuterBounds = marginsLayout.toOuterBounds(DoubleRectangle(preferredGeomInnerBounds.origin, actualGeomInnerSize))
        val actualGeomInnerBounds = marginsLayout.toInnerBounds(actualGeomOuterBounds)
        val actualGeomPlottingBounds = panelPadding.subtractFrom(actualGeomInnerBounds)

        // sync axis info with new (maybe) geom area size
        val axisInfosNew = axisInfos
            .withHAxisLength(actualGeomPlottingBounds.width)
            .withVAxisLength(actualGeomPlottingBounds.height)

        val geomWithAxisBounds: DoubleRectangle = DoubleRectangle.LTRB(
            left = axisInfos.left?.axisBoundsAbsolute(actualGeomOuterBounds)?.left ?: actualGeomOuterBounds.left,
            top = actualGeomOuterBounds.top, // polar coord never has top axis
            right = actualGeomOuterBounds.right, // polar coord never has right axis
            bottom = actualGeomOuterBounds.bottom, // with polar coord bottom axis is as a part of geom area
        )

        return TileLayoutInfo(
            offset = ZERO,
            geomWithAxisBounds = geomWithAxisBounds,
            geomOuterBounds = actualGeomOuterBounds,
            geomInnerBounds = actualGeomInnerBounds,
            geomPlottingBounds = actualGeomPlottingBounds,
            axisInfos = axisInfosNew,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    private fun computeAxisInfos(plotSize: DoubleVector): GeomAreaInsets {
        val insetsInitial = GeomAreaInsets.init(axisLayoutQuad)
        val axisHeightEstim = geomPanelBounds(insetsInitial, plotSize)
            .dimension
            .let(marginsLayout::toInnerSize)
            .y / 2 // For polar coord axis height is half of geom height - it starts from the center.

        val insetsVAxis = insetsInitial.layoutVAxis(vDomain, axisHeightEstim)
        val plottingArea = geomPanelBounds(insetsVAxis, plotSize)

        val hAxisLength = marginsLayout
            .toInnerBounds(plottingArea)
            .width * 1.5 // h axis in polar coord is rendered as a circle - increase its length to make more tick.

        val insetsHVAxis = insetsVAxis.layoutHAxis(hDomain, hAxisLength)

        // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
        val insetsFinal =
            if ((insetsHVAxis.top + insetsHVAxis.bottom) > (insetsInitial.top + insetsInitial.bottom)) {
                val geomHeight = geomPanelBounds(insetsHVAxis, plotSize)
                    .dimension
                    .let(marginsLayout::toInnerSize)
                    .y / 2 // For polar coord axis height is half of geom height - it starts from the center.

                insetsHVAxis.layoutVAxis(vDomain, geomHeight)
            } else {
                insetsHVAxis
            }

        return insetsFinal
    }

    private fun geomPanelBounds(geomInsets: GeomAreaInsets, plotSize: DoubleVector): DoubleRectangle {
        val plottingArea = geomInsets.subtractFrom(DoubleRectangle(ZERO, plotSize))
        val geomInnerSize = marginsLayout.toInnerSize(plottingArea.dimension)
            .subtract(panelPadding.size)

        val size = min(geomInnerSize.x, geomInnerSize.y).let { DoubleVector(it, it) }
        val geomOuterSizeAdjusted = marginsLayout.toOuterSize(size)

        return DoubleRectangle(plottingArea.origin, geomOuterSizeAdjusted)
    }
}