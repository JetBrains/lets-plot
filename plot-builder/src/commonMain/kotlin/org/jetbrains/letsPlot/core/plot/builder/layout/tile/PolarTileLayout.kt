/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfoQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutQuad
import org.jetbrains.letsPlot.core.plot.builder.layout.GeomMarginsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.util.GeomAreaInsets

internal class PolarTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    marginsLayout: GeomMarginsLayout,
    panelInset: Thickness,
) : TopDownTileLayout(
    axisLayoutQuad = axisLayoutQuad,
    hDomain = hDomain,
    vDomain = vDomain,
    marginsLayout = marginsLayout,
    panelInset = panelInset
) {

    override fun computeGeomWithAxisBounds(
        geomOuterBounds: DoubleRectangle,
        axisInfos: AxisLayoutInfoQuad
    ): DoubleRectangle {
        return DoubleRectangle.LTRB(
            left = axisInfos.left?.axisBoundsAbsolute(geomOuterBounds)?.left ?: geomOuterBounds.left,
            top = geomOuterBounds.top, // polar coord never has top axis
            right = geomOuterBounds.right, // polar coord never has right axis
            bottom = geomOuterBounds.bottom, // with polar coord bottom axis is as a part of geom area
        )
    }

    override fun computeAxisInfos(plotSize: DoubleVector, coordProvider: CoordProvider): GeomAreaInsets {
        val insetsInitial = GeomAreaInsets.init(axisLayoutQuad)

        // For polar coord axis height is half of geom height - it starts from the center.
        val axisHeightEstim = computeGeomContentBounds(insetsInitial, plotSize, coordProvider).dimension.y / 2

        val insetsVAxis = insetsInitial.layoutVAxis(vDomain, axisHeightEstim)
        val contentBounds = computeGeomContentBounds(insetsVAxis, plotSize, coordProvider)

        // h axis in polar coord is rendered as a circle - increase its length to make more tick.
        val hAxisLength = contentBounds.width * 1.5

        return insetsVAxis.layoutHAxis(hDomain, hAxisLength)
    }
}