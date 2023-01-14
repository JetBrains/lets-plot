/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.guide.Orientation.*
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.LayoutConstants.GEOM_MIN_SIZE
import kotlin.math.max

internal class GeomAreaInsets private constructor(
    left: Double,
    top: Double,
    right: Double,
    bottom: Double,
    private val axisLayoutQuad: AxisLayoutQuad,
    val axisInfoQuad: AxisLayoutInfoQuad,
) : Insets(
    left = left,
    top = top,
    right = right,
    bottom = bottom,
) {

    override fun subtractFrom(r: DoubleRectangle): DoubleRectangle {
        @Suppress("NAME_SHADOWING")
        val r = super.subtractFrom(r)
        return DoubleRectangle(
            r.origin,
            DoubleVector(
                max(r.width, GEOM_MIN_SIZE.x),
                max(r.height, GEOM_MIN_SIZE.y)
            )
        )
    }

    fun layoutHAxis(
        axisDomain: DoubleSpan,
        axisLength: Double,
    ): GeomAreaInsets {
        val axisInfosNew = AxisLayoutInfoQuad(
            left = axisInfoQuad.left,
            right = axisInfoQuad.right,
            top = axisLayoutQuad.top?.doLayout(axisDomain, axisLength, this),
            bottom = axisLayoutQuad.bottom?.doLayout(axisDomain, axisLength, this),
        )

        return GeomAreaInsets(
            left = left,
            top = axisInfosNew.top?.axisBounds()?.height ?: top,
            right = right,
            bottom = axisInfosNew.bottom?.axisBounds()?.height ?: bottom,
            axisLayoutQuad,
            axisInfosNew,
        )
    }

    fun layoutVAxis(axisDomain: DoubleSpan, axisLength: Double): GeomAreaInsets {
        val axisInfosNew = AxisLayoutInfoQuad(
            left = axisLayoutQuad.left?.doLayout(axisDomain, axisLength, this),
            right = axisLayoutQuad.right?.doLayout(axisDomain, axisLength, this),
            top = axisInfoQuad.top,
            bottom = axisInfoQuad.bottom,
        )

        return GeomAreaInsets(
            left = axisInfosNew.left?.axisBounds()?.width ?: left,
            top = top,
            right = axisInfosNew.right?.axisBounds()?.width ?: right,
            bottom = bottom,
            axisLayoutQuad,
            axisInfosNew,
        )
    }

    companion object {
        fun init(axisLayoutQuad: AxisLayoutQuad): GeomAreaInsets {
            return GeomAreaInsets(
                left = axisLayoutQuad.left?.let { initialThickness(it) } ?: 0.0,
                top = axisLayoutQuad.top?.let { initialThickness(it) } ?: 0.0,
                right = axisLayoutQuad.right?.let { initialThickness(it) } ?: 0.0,
                bottom = axisLayoutQuad.bottom?.let { initialThickness(it) } ?: 0.0,
                axisLayoutQuad,
                AxisLayoutInfoQuad.EMPTY
            )
        }

        private fun initialThickness(axisLayout: AxisLayout): Double {
            return PlotAxisLayoutUtil.initialThickness(axisLayout.orientation, axisLayout.theme)
        }
    }
}