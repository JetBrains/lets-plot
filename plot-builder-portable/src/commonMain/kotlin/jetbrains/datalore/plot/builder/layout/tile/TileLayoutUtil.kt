/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.GeomMarginsLayout
import jetbrains.datalore.plot.builder.layout.util.GeomAreaInsets
import kotlin.math.max

internal object TileLayoutUtil {
    const val GEOM_MARGIN = 0.0          // min space around geom area
    val GEOM_MIN_SIZE = DoubleVector(50.0, 50.0)

    fun liveMapGeomBounds(plotSize: DoubleVector): DoubleRectangle {
        return subtractMargins(0.0, 0.0, plotSize)
    }

    /**
     * ToDo: remove
     */
    private fun subtractMargins(
        hAxisThickness: Double,
        vAxisThickness: Double,
        plotSize: DoubleVector
    ): DoubleRectangle {
        val marginLeftTop = DoubleVector(vAxisThickness, GEOM_MARGIN)
        val marginRightBottom = DoubleVector(GEOM_MARGIN, hAxisThickness)

        val geomSize = plotSize
            .subtract(marginLeftTop)
            .subtract(marginRightBottom)

        return DoubleRectangle(
            marginLeftTop,
            DoubleVector(
                max(geomSize.x, GEOM_MIN_SIZE.x),
                max(geomSize.y, GEOM_MIN_SIZE.y)
            )
        )
    }

    fun geomOuterBounds(
        geomInsets: GeomAreaInsets,
        plotSize: DoubleVector,
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        marginsLayout: GeomMarginsLayout,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val plottingArea = geomInsets.subtractFrom(DoubleRectangle(ZERO, plotSize))
        val geomInnerSize = marginsLayout.toInnerSize(plottingArea.dimension)

        val geomOuterSizeAdjusted = coordProvider.adjustGeomSize(hDomain, vDomain, geomInnerSize).let {
            marginsLayout.toOuterSize(it)
        }
        return DoubleRectangle(plottingArea.origin, geomOuterSizeAdjusted)
    }

    fun maxHAxisTickLabelsBounds(
        axisOrientation: Orientation,
        stretch: Double,
        axisSpan: DoubleSpan,
        plotSize: DoubleVector
    ): DoubleRectangle {
        val geomPadding = 10.0          // min space around geom area (labels should not touch geom area).

        val maxHorizontalSpan = DoubleSpan(geomPadding, plotSize.x - 2 * geomPadding)
        return maxHAxisTickLabelsBounds(axisOrientation, stretch, axisSpan, maxHorizontalSpan)
    }

    fun maxHAxisTickLabelsBounds(
        axisOrientation: Orientation,
        stretch: Double,
        axisSpan: DoubleSpan,
        maxHorizontalSpan: DoubleSpan
    ): DoubleRectangle {
        when (axisOrientation) {
            Orientation.TOP,
            Orientation.BOTTOM -> {
                val leftSpace = axisSpan.lowerEnd - maxHorizontalSpan.lowerEnd + stretch
                val rightSpace = maxHorizontalSpan.upperEnd - axisSpan.upperEnd + stretch

                val height = 1E42   // just very large number
                val top = when (axisOrientation) {
                    Orientation.TOP -> -height
                    else -> 0.0
                }

                val left = -leftSpace
                val width = leftSpace + rightSpace + axisSpan.length
                return DoubleRectangle(left, top, width, height)
            }

            else -> throw IllegalArgumentException("Orientation not supported: $axisOrientation")
        }
    }
}
