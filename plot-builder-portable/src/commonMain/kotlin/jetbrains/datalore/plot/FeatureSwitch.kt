/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation

object FeatureSwitch {

    const val PLOT_VIEW_TOOLBOX = false

    const val PLOT_DEBUG_DRAWING = false
    const val LEGEND_DEBUG_DRAWING = false

    // Marginal layers

    const val MARGINAL_LAYERS = false
    private const val MARGIN_RATIO = 0.3

    fun marginToSub(dim: Double) = dim * MARGIN_RATIO

    private fun marginAddOneSide(dim: Double) = dim * (1 / (1 - MARGIN_RATIO) - 1)

    // This is when there are marginal layers on both ends.
    private fun marginToAdd(dim: Double) = dim / 2 * (1 / (1 - 2 * MARGIN_RATIO) - 1)

    fun subtactMarginalLayers(size: DoubleVector): DoubleVector {
        val margins = DoubleVector(
            marginToSub(size.x),
            marginToSub(size.y)
        )
        val delta = margins.add(margins)
        return size.subtract(delta)
    }

    fun addMarginalLayers(size: DoubleVector): DoubleVector {
        val margins = DoubleVector(
            marginToAdd(size.x),
            marginToAdd(size.y)
        )
        val delta = margins.add(margins)
        return size.add(delta)
    }

    fun subtactMarginalLayers(bounds: DoubleRectangle): DoubleRectangle {
        val offset = DoubleVector(
            marginToSub(bounds.width),
            marginToSub(bounds.height)
        )
        val delta = offset.add(offset)
        return DoubleRectangle(
            bounds.origin.add(offset),
            bounds.dimension.subtract(delta)
        )
    }

    fun addMarginalLayers(bounds: DoubleRectangle): DoubleRectangle {
        val offset = DoubleVector(
            marginToAdd(bounds.width),
            marginToAdd(bounds.height)
        )
        val delta = offset.add(offset)
        return DoubleRectangle(
            bounds.origin.subtract(offset),
            bounds.dimension.add(delta)
        )
    }

    fun toAxisOrigin(geomBounds: DoubleRectangle, axisOrientation: Orientation): DoubleVector {
        return when (axisOrientation) {
            Orientation.LEFT -> {
                geomBounds.origin.subtract(DoubleVector(marginToAdd(geomBounds.width), 0.0))
            }
            Orientation.BOTTOM -> {
                DoubleVector(geomBounds.left, geomBounds.bottom + marginToAdd(geomBounds.height))
            }
            else -> throw IllegalStateException("toAxisOrigin: axis orientation $axisOrientation is not yet supported.")
        }
    }
}