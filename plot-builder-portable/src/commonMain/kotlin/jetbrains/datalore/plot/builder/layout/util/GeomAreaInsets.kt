/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.guide.Orientation.*
import jetbrains.datalore.plot.builder.layout.AxisLayout
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.LayoutConstants.GEOM_MIN_SIZE
import kotlin.math.max

internal class GeomAreaInsets private constructor(
    left: Double,
    top: Double,
    right: Double,
    bottom: Double,
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val _hAxisInfo: AxisLayoutInfo?,
    private val _vAxisInfo: AxisLayoutInfo?
) : Insets(
    left = left,
    top = top,
    right = right,
    bottom = bottom,
) {

    val hAxisThickness: Double
        get() = when (hAxisLayout.orientation == TOP) {
            true -> top
            false -> bottom
        }

    val vAxisThickness: Double
        get() = when (vAxisLayout.orientation == LEFT) {
            true -> left
            false -> right
        }

    val hAxisInfo: AxisLayoutInfo
        get() {
            return _hAxisInfo ?: throw IllegalStateException("Horizontal axis is not layouted")
        }

    val vAxisInfo: AxisLayoutInfo
        get() {
            return _vAxisInfo ?: throw IllegalStateException("Vertical axis is not layouted")
        }

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
        val hAxisInfo = hAxisLayout.doLayout(axisDomain, axisLength, this)
        return GeomAreaInsets(
            left = left,
            top = if (hAxisLayout.orientation == TOP) hAxisInfo.axisBounds().height else top,
            right = right,
            bottom = if (hAxisLayout.orientation == BOTTOM) hAxisInfo.axisBounds().height else bottom,
            hAxisLayout, vAxisLayout,
            _hAxisInfo = hAxisInfo,
            _vAxisInfo = _vAxisInfo
        )
    }

    fun layoutVAxis(axisDomain: DoubleSpan, axisLength: Double): GeomAreaInsets {
        val vAxisInfo = vAxisLayout.doLayout(axisDomain, axisLength, this)
        return GeomAreaInsets(
            left = if (vAxisLayout.orientation == LEFT) vAxisInfo.axisBounds().width else left,
            top = top,
            right = if (vAxisLayout.orientation == RIGHT) vAxisInfo.axisBounds().width else right,
            bottom = bottom,
            hAxisLayout, vAxisLayout,
            _hAxisInfo = _hAxisInfo,
            _vAxisInfo = vAxisInfo
        )
    }

    companion object {
        fun init(hAxisLayout: AxisLayout, vAxisLayout: AxisLayout): GeomAreaInsets {
            return GeomAreaInsets(
                left = if (vAxisLayout.orientation == LEFT) vAxisLayout.initialThickness() else 0.0,
                top = if (hAxisLayout.orientation == TOP) hAxisLayout.initialThickness() else 0.0,
                right = if (vAxisLayout.orientation == RIGHT) vAxisLayout.initialThickness() else 0.0,
                bottom = if (hAxisLayout.orientation == BOTTOM) hAxisLayout.initialThickness() else 0.0,
                hAxisLayout, vAxisLayout,
                null, null
            )
        }
    }
}