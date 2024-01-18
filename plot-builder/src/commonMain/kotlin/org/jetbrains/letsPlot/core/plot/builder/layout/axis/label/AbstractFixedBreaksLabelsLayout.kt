/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.GeometryUtil

internal abstract class AbstractFixedBreaksLabelsLayout(
    orientation: Orientation,
    protected val breaks: ScaleBreaks,
    theme: AxisTheme
) : AxisLabelsLayout(
    orientation,
    theme
) {

    private fun labelBounds(labelLocation: DoubleVector, labelText: String): DoubleRectangle {
        val dim = labelSpec.dimensions(labelText)
        val labelBounds = labelBounds(dim)
        return labelBounds.add(labelLocation)
    }

    protected abstract fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle

    fun labelsBounds(
        tickPositions: List<Double>,
        @Suppress("UNUSED_PARAMETER") tickLabels: List<String>,
        toTickLocation: (Double) -> DoubleVector
    ): DoubleRectangle? {
        val boundsList = labelBoundsList(tickPositions, breaks.labels, toTickLocation)
        var bounds: DoubleRectangle? = null
        for (labelBounds in boundsList) {
            bounds = GeometryUtil.union(labelBounds, bounds)
        }
        return bounds
    }

    fun labelBoundsList(
        tickPositions: List<Double>,
        tickLabels: List<String>,
        toTickLocation: (Double) -> DoubleVector
    ): List<DoubleRectangle> {
        val result = ArrayList<DoubleRectangle>()
        val labels = tickLabels.iterator()
        for (pos in tickPositions) {
            val label = labels.next()
            val bounds = labelBounds(toTickLocation(pos), label)
            result.add(bounds)
        }
        return result
    }


    fun createAxisLabelsLayoutInfoBuilder(bounds: DoubleRectangle, overlap: Boolean): AxisLabelsLayoutInfo.Builder {
        return AxisLabelsLayoutInfo.Builder()
            .breaks(breaks)
            .bounds(applyLabelMargins(bounds))
            .overlap(overlap)
    }

    fun noLabelsLayoutInfo(
        axisLength: Double,
        orientation: Orientation
    ): AxisLabelsLayoutInfo {
        if (orientation.isHorizontal) {
            var bounds = DoubleRectangle(axisLength / 2, 0.0, 0.0, 0.0) // empty bounds in the middle of the axis;
            if (theme.showTickMarks()) {
                bounds = applyLabelMargins(bounds)
            }
            return AxisLabelsLayoutInfo.Builder()
                .breaks(breaks)
                .bounds(bounds)
                .overlap(false)
                .labelAdditionalOffsets(null)
                .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
                .labelVerticalAnchor(Text.VerticalAnchor.TOP)
                .build()
        }

        throw IllegalStateException("Not implemented for $orientation")
    }

    companion object {
        val HORIZONTAL_TICK_LOCATION = { x: Double -> DoubleVector(x, 0.0) }
    }
}
