/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.guide.Orientation.BOTTOM
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.abs

internal class HorizontalVerticalLabelsLayout(
    orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    axisDomain: ClosedRange<Double>,
    labelSpec: PlotLabelSpec,
    breaks: GuideBreaks,
    theme: AxisTheme) :
        AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    val labelHorizontalAnchor: TextLabel.HorizontalAnchor
        get() {
            if (orientation === BOTTOM) {
                return TextLabel.HorizontalAnchor.LEFT
            }
            throw RuntimeException("Not implemented")
        }

    val labelVerticalAnchor: TextLabel.VerticalAnchor
        get() = TextLabel.VerticalAnchor.CENTER

    override fun doLayout(
            axisLength: Double,
            axisMapper: (Double?) -> Double?,
            maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        val height = labelSpec.height()
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        var overlap = false
        if (breaks.size() >= 2) {
            val minTickDistance = height + MIN_DISTANCE
            val tickDistance = abs(ticks[0] - ticks[1])
            overlap = tickDistance < minTickDistance
        }

        val bounds = labelsBounds(ticks, breaks.labels,
            HORIZONTAL_TICK_LOCATION
        )
        return createAxisLabelsLayoutInfoBuilder(bounds!!, overlap)
                .labelHorizontalAnchor(labelHorizontalAnchor)
                .labelVerticalAnchor(labelVerticalAnchor)
                .labelRotationAngle(ROTATION_DEGREE)
                .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        if (!(ROTATION_DEGREE == 90.0
                        && labelHorizontalAnchor === TextLabel.HorizontalAnchor.LEFT
                        && labelVerticalAnchor === TextLabel.VerticalAnchor.CENTER)) {
            throw RuntimeException("Not implemented")
        }
        val w = labelNormalSize.y
        val h = labelNormalSize.x
        val x = -w / 2
        val y = 0.0
        return DoubleRectangle(x, y, w, h)
    }

    companion object {
        private const val MIN_DISTANCE = 5.0
        private const val ROTATION_DEGREE = 90.0
    }
}
