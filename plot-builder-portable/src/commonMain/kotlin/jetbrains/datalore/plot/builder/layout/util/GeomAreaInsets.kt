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
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil
import jetbrains.datalore.plot.builder.layout.tile.TileLayoutUtil.GEOM_MARGIN
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
                max(r.width, TileLayoutUtil.GEOM_MIN_SIZE.x),
                max(r.height, TileLayoutUtil.GEOM_MIN_SIZE.y)
            )
        )
    }

    fun layoutHAxis(axisDomain: DoubleSpan, plotSize: DoubleVector, axisSpan: DoubleSpan): GeomAreaInsets {
        val axisLength = axisSpan.length
        val stretch = axisLength * AXIS_STRETCH_RATIO

        val maxTickLabelsBounds = TileLayoutUtil.maxHAxisTickLabelsBounds(
            hAxisLayout.orientation,
            stretch,
            axisSpan,
            plotSize
        )
        val info = hAxisLayout.doLayout(axisDomain, axisLength, maxTickLabelsBounds)
        return GeomAreaInsets(
            left = left,
            top = if (hAxisLayout.orientation == TOP) info.axisBounds().height else top,
            right = right,
            bottom = if (hAxisLayout.orientation == BOTTOM) info.axisBounds().height else bottom,
            hAxisLayout, vAxisLayout,
            _hAxisInfo = info,
            _vAxisInfo = _vAxisInfo
        )
    }

    fun layoutVAxis(axisDomain: DoubleSpan, axisLength: Double): GeomAreaInsets {
        val info = vAxisLayout.doLayout(axisDomain, axisLength, null)
        return GeomAreaInsets(
            left = if (vAxisLayout.orientation == LEFT) info.axisBounds().width else left,
            top = top,
            right = if (vAxisLayout.orientation == RIGHT) info.axisBounds().width else right,
            bottom = bottom,
            hAxisLayout, vAxisLayout,
            _hAxisInfo = _hAxisInfo,
            _vAxisInfo = info
        )
    }

    companion object {
        private const val AXIS_STRETCH_RATIO = 0.1  // allow 10% axis flexibility (on each end)

        fun init(hAxisLayout: AxisLayout, vAxisLayout: AxisLayout): GeomAreaInsets {
            return GeomAreaInsets(
                left = if (vAxisLayout.orientation == LEFT) vAxisLayout.initialThickness() else GEOM_MARGIN,
                top = if (hAxisLayout.orientation == TOP) hAxisLayout.initialThickness() else GEOM_MARGIN,
                right = if (vAxisLayout.orientation == RIGHT) vAxisLayout.initialThickness() else GEOM_MARGIN,
                bottom = if (hAxisLayout.orientation == BOTTOM) hAxisLayout.initialThickness() else GEOM_MARGIN,
                hAxisLayout, vAxisLayout,
                null, null
            )
        }
    }
}