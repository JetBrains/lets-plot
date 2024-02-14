/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.MarginSide
import org.jetbrains.letsPlot.core.plot.builder.MarginalLayerUtil
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation

/**
 * left, top, right, bottom : margin size relative to the overall geom area.
 */
internal class GeomMarginsLayout(
    private val left: Double,
    private val top: Double,
    private val right: Double,
    private val bottom: Double,
) {

    fun toInnerSize(outer: DoubleVector): DoubleVector {
        val lt = DoubleVector(
            outer.x * left,
            outer.y * top
        )
        val rb = DoubleVector(
            outer.x * right,
            outer.y * bottom
        )
        return outer.subtract(lt).subtract(rb)
    }

    fun toInnerBounds(outer: DoubleRectangle): DoubleRectangle {
        val lt = DoubleVector(
            outer.width * left,
            outer.height * top
        )
        return DoubleRectangle(
            outer.origin.add(lt),
            toInnerSize(outer.dimension)
        )
    }

    fun toOuterSize(inner: DoubleVector): DoubleVector {
        val outerWidth = inner.x / (1 - (left + right))
        val outerHeight = inner.y / (1 - (top + bottom))
        return DoubleVector(outerWidth, outerHeight)
    }

    fun toOuterBounds(inner: DoubleRectangle): DoubleRectangle {
        val outerSize = toOuterSize(inner.dimension)
        val lt = DoubleVector(
            outerSize.x * left,
            outerSize.y * top
        )

        return DoubleRectangle(
            inner.origin.subtract(lt),
            outerSize
        )
    }


    fun toAxisOrigin(
        geomInnerBounds: DoubleRectangle,
        axisOrientation: Orientation,
        isPolarCoordinateSystem: Boolean
    ): DoubleVector {

        val outerBounds = toOuterBounds(geomInnerBounds)
        return when (axisOrientation) {
            Orientation.LEFT -> DoubleVector(outerBounds.left, geomInnerBounds.top)
            Orientation.TOP -> geomInnerBounds.origin
            Orientation.RIGHT -> DoubleVector(geomInnerBounds.right, geomInnerBounds.top)
            Orientation.BOTTOM -> {
                if (isPolarCoordinateSystem) {
                    // Angle marks are placed from top to bottom. With a bottom alignment, they will go under a plot.
                    DoubleVector(geomInnerBounds.left, geomInnerBounds.top)
                } else {
                    DoubleVector(geomInnerBounds.left, outerBounds.bottom)
                }
            }
        }
    }


    companion object {
        fun create(marginalLayers: List<GeomLayer>): GeomMarginsLayout {
            val marginalLayersByMargin: Map<MarginSide, List<GeomLayer>> =
                MarginalLayerUtil.marginalLayersByMargin(marginalLayers)

            val left = marginalLayersByMargin[MarginSide.LEFT]?.maxOfOrNull { it.marginalSize }
            val top = marginalLayersByMargin[MarginSide.TOP]?.maxOfOrNull { it.marginalSize }
            val right = marginalLayersByMargin[MarginSide.RIGHT]?.maxOfOrNull { it.marginalSize }
            val bottom = marginalLayersByMargin[MarginSide.BOTTOM]?.maxOfOrNull { it.marginalSize }

            return GeomMarginsLayout(
                left = left ?: 0.0,
                top = top ?: 0.0,
                right = right ?: 0.0,
                bottom = bottom ?: 0.0,
            )
        }
    }
}