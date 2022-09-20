/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal abstract class AbstractFixedBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    protected val breaks: ScaleBreaks,
    theme: AxisTheme
) : AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {

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
            .bounds(applyLabelsMargins(bounds))
            .overlap(overlap)
    }

    fun noLabelsLayoutInfo(
        axisLength: Double,
        orientation: Orientation
    ): AxisLabelsLayoutInfo {
        if (orientation.isHorizontal) {
            var bounds = DoubleRectangle(axisLength / 2, 0.0, 0.0, 0.0) // empty bounds in the middle of the axis;
            bounds = applyLabelsMargins(bounds)
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
