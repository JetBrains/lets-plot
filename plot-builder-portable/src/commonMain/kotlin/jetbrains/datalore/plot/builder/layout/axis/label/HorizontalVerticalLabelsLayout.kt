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
import jetbrains.datalore.plot.builder.guide.Orientation.BOTTOM
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.abs

internal class HorizontalVerticalLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme
) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    val labelHorizontalAnchor: Text.HorizontalAnchor
        get() {
            if (orientation === BOTTOM) {
                return Text.HorizontalAnchor.LEFT
            }
            throw RuntimeException("Not implemented")
        }

    val labelVerticalAnchor: Text.VerticalAnchor
        get() = Text.VerticalAnchor.CENTER

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        val height = labelSpec.height()
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        var overlap = false
        if (breaks.size >= 2) {
            val minTickDistance = height + MIN_DISTANCE
            val tickDistance = abs(ticks[0] - ticks[1])
            overlap = tickDistance < minTickDistance
        }

        val bounds = labelsBounds(
            ticks, breaks.labels,
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
                    && labelHorizontalAnchor === Text.HorizontalAnchor.LEFT
                    && labelVerticalAnchor === Text.VerticalAnchor.CENTER)
        ) {
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
