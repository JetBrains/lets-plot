/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.AxisLayoutQuad
import jetbrains.datalore.plot.builder.layout.GeomMarginsLayout
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.geomOuterBounds
import jetbrains.datalore.plot.builder.layout.util.GeomAreaInsets

internal class TopDownTileLayout(
    private val axisLayoutQuad: AxisLayoutQuad,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
    private val marginsLayout: GeomMarginsLayout,
) : TileLayout {
    override val insideOut: Boolean = false

    override fun doLayout(preferredSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        var geomAreaInsets = computeAxisInfos(
            axisLayoutQuad,
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

        val axisInfos = geomAreaInsets.axisInfoQuad

        // Combine geom area and x/y-axis
        val (l, r, t, b) = axisInfos
        val axisBounds = listOfNotNull(l, r, t, b)
            .map {
                it.axisBoundsAbsolute(geomBoundsAfterLayout)
            }

        val geomWithAxisBounds = axisBounds.fold(geomBoundsAfterLayout) { a, e ->
            a.union(e)
        }

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

            val hAxisLength = marginsLayout.toInnerBounds(plottingArea).width
            val insetsHVAxis = insetsVAxis.layoutHAxis(
                hDomain,
                hAxisLength
            )

            // Re-layout y-axis if x-axis became thicker than its 'original thickness'.
            val insetsFinal =
                if ((insetsHVAxis.top + insetsHVAxis.bottom) > (insetsInitial.top + insetsInitial.bottom)) {
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
