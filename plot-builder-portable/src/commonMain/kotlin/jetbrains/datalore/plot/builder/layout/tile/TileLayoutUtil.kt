/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.FeatureSwitch.MARGINAL_LAYERS
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import kotlin.math.max

internal object TileLayoutUtil {
    const val GEOM_MARGIN = 0.0          // min space around geom area
    private const val CLIP_EXTEND = 5.0
    val GEOM_MIN_SIZE = DoubleVector(50.0, 50.0)

    fun liveMapGeomBounds(plotSize: DoubleVector): DoubleRectangle {
        return subtractMargins(0.0, 0.0, plotSize)
    }

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

    // ToDo: this is the entire tile plotting area - rename
    fun geomBounds(
        hAxisThickness: Double,
        vAxisThickness: Double,
        plotSize: DoubleVector,
        hDomain: DoubleSpan,
        vDomain: DoubleSpan,
        coordProvider: CoordProvider
    ): DoubleRectangle {
        val plottingArea = subtractMargins(hAxisThickness, vAxisThickness, plotSize)

        val geomSize = subtractMargins(hAxisThickness, vAxisThickness, plotSize).let {
            when {
                MARGINAL_LAYERS -> FeatureSwitch.subtactMarginalLayers(it.dimension)
                else -> it.dimension
            }
        }
        val geomSizeAdjusted = coordProvider.adjustGeomSize(hDomain, vDomain, geomSize).let {
            when {
                MARGINAL_LAYERS -> FeatureSwitch.addMarginalLayers(it)
                else -> it
            }
        }
        return DoubleRectangle(plottingArea.origin, geomSizeAdjusted)
    }

    fun clipBounds(geomBounds: DoubleRectangle): DoubleRectangle {
        return DoubleRectangle(
            geomBounds.origin.subtract(
                DoubleVector(
                    CLIP_EXTEND,
                    CLIP_EXTEND
                )
            ),
            DoubleVector(
                geomBounds.dimension.x + 2 * CLIP_EXTEND,
                geomBounds.dimension.y + 2 * CLIP_EXTEND
            )
        )
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
