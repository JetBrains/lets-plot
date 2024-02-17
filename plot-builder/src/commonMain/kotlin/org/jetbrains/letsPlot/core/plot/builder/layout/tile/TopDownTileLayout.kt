/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.util.GeomAreaInsets

internal open class TopDownTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
    private val panelPadding: Thickness,
) : TileLayout {
    override val insideOut: Boolean = false

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        val geomAreaInsets = computeAxisInfos(preferredSize, coordProvider)

        val geomContentBounds = computeGeomContentBounds(geomAreaInsets, preferredSize, coordProvider)
        val geomInnerBounds = panelPadding.inflateRect(geomContentBounds)
        val geomOuterBounds = marginsLayout.toOuterBounds(geomInnerBounds)

        val axisInfos = geomAreaInsets.axisInfoQuad

        // Combine geom area and x/y-axis
        val geomWithAxisBounds = computeGeomWithAxisBounds(geomOuterBounds, axisInfos)

        // sync axis info with new (maybe) geom area size
        val axisInfosNew = axisInfos
            .withHAxisLength(geomContentBounds.width)
            .withVAxisLength(geomContentBounds.height)

        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            geomWithAxisBounds = geomWithAxisBounds,
            geomOuterBounds = geomOuterBounds,
            geomInnerBounds = geomInnerBounds,
            geomContentBounds = geomContentBounds,
            axisInfos = axisInfosNew,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    protected open fun computeGeomWithAxisBounds(geomOuterBounds: DoubleRectangle, axisInfos: AxisLayoutInfoQuad): DoubleRectangle {
        val (l, r, t, b) = axisInfos
        return DoubleRectangle.LTRB(
            left = l?.axisBoundsAbsolute(geomOuterBounds)?.left ?: geomOuterBounds.left,
            top = t?.axisBoundsAbsolute(geomOuterBounds)?.top ?: geomOuterBounds.top,
            right = r?.axisBoundsAbsolute(geomOuterBounds)?.right ?: geomOuterBounds.right,
            bottom = b?.axisBoundsAbsolute(geomOuterBounds)?.bottom ?: geomOuterBounds.bottom,
        )
    }

    protected open fun computeAxisInfos(
        plotSize: DoubleVector,
        coordProvider: CoordProvider
    ): GeomAreaInsets {
        val insetsInitial = GeomAreaInsets.init(axisLayoutQuad)
        val axisHeightEstim =
            computeGeomContentBounds(insetsInitial, plotSize, coordProvider)
                .dimension
                .let(marginsLayout::toInnerSize)
                .y

        val insetsVAxis = insetsInitial.layoutVAxis(vDomain, axisHeightEstim)
        val plottingArea = computeGeomContentBounds(insetsVAxis, plotSize, coordProvider)

        val hAxisLength = marginsLayout
            .toInnerBounds(plottingArea)
            .width

        val insetsHVAxis = insetsVAxis.layoutHAxis(
            hDomain,
            hAxisLength
        )

        // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
        val insetsFinal =
            if ((insetsHVAxis.top + insetsHVAxis.bottom) > (insetsInitial.top + insetsInitial.bottom)) {
                val geomHeight = computeGeomContentBounds(insetsHVAxis, plotSize, coordProvider)
                    .dimension
                    .let(marginsLayout::toInnerSize)
                    .y

                insetsHVAxis.layoutVAxis(vDomain, geomHeight)
            } else {
                insetsHVAxis
            }

        return insetsFinal
    }

    protected fun computeGeomContentBounds(
        geomInsets: GeomAreaInsets,
        plotSize: DoubleVector,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val plottingArea = geomInsets.subtractFrom(DoubleRectangle(DoubleVector.ZERO, plotSize))
        val panelBounds = marginsLayout.toInnerBounds(plottingArea)
        val contentBounds = panelPadding.shrinkRect(panelBounds)

        val geomOuterSizeAdjusted = coordProvider.adjustGeomSize(hDomain, vDomain, contentBounds.dimension)


        return DoubleRectangle(contentBounds.origin, geomOuterSizeAdjusted)
    }


}
