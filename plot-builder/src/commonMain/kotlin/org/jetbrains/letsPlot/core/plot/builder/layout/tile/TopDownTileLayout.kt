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
    private val panelInset: Thickness,
) : TileLayout {
    override val insideOut: Boolean = false

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        val geomAreaInsets = computeAxisInfos(preferredSize, coordProvider)

        val geomOuterBounds = geomOuterBounds(geomAreaInsets, preferredSize, coordProvider)
        val geomContentBounds = geomContentBounds(geomAreaInsets, preferredSize, coordProvider)
        val geomInnerBounds = getGeomInnerBounds(geomAreaInsets, preferredSize, coordProvider)

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

    protected open fun computeGeomWithAxisBounds(
        geomOuterBounds: DoubleRectangle,
        axisInfos: AxisLayoutInfoQuad
    ): DoubleRectangle {
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
            geomContentBounds(insetsInitial, plotSize, coordProvider)
                .dimension
                .y

        val insetsVAxis = insetsInitial.layoutVAxis(vDomain, axisHeightEstim)
        val plottingArea = geomContentBounds(insetsVAxis, plotSize, coordProvider)

        val hAxisLength = plottingArea.width

        val insetsHVAxis = insetsVAxis.layoutHAxis(
            hDomain,
            hAxisLength
        )

        // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
        val insetsFinal =
            if ((insetsHVAxis.top + insetsHVAxis.bottom) > (insetsInitial.top + insetsInitial.bottom)) {
                val geomHeight = geomContentBounds(insetsHVAxis, plotSize, coordProvider)
                    .dimension
                    .y

                insetsHVAxis.layoutVAxis(vDomain, geomHeight)
            } else {
                insetsHVAxis
            }

        return insetsFinal
    }

    protected fun geomContentBounds(
        geomInsets: GeomAreaInsets,
        plotSize: DoubleVector,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val geomInnerBounds = getGeomInnerBounds(geomInsets, plotSize, coordProvider)
        return panelInset.shrinkRect(geomInnerBounds)
    }

    private fun getGeomInnerBounds(
        geomAreaInsets: GeomAreaInsets,
        plotSize: DoubleVector,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val geomOuterBounds = geomOuterBounds(geomAreaInsets, plotSize, coordProvider)
        return marginsLayout.toInnerBounds(geomOuterBounds)
    }

    private fun geomOuterBounds(
        geomInsets: GeomAreaInsets,
        plotSize: DoubleVector,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val plottingArea = geomInsets.subtractFrom(DoubleRectangle(DoubleVector.ZERO, plotSize))
        val geomInnerSize = marginsLayout.toInnerSize(plottingArea.dimension)
        val geomContentSize = panelInset.shrinkSize(geomInnerSize)

        val geomOuterSizeAdjusted = coordProvider.adjustGeomSize(hDomain, vDomain, geomContentSize)
            .let(marginsLayout::toOuterSize)
            .let(panelInset::inflateSize)

        return DoubleRectangle(plottingArea.origin, geomOuterSizeAdjusted)
    }
}
