/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.math.toRadians
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.guide.Orientation.BOTTOM
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class HorizontalTiltedLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: PlotLabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme
) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    private val labelHorizontalAnchor: Text.HorizontalAnchor
        get() {
            if (orientation === BOTTOM) {
                return Text.HorizontalAnchor.RIGHT
            }
            throw RuntimeException("Not implemented")
        }

    private val labelVerticalAnchor: Text.VerticalAnchor
        get() = Text.VerticalAnchor.TOP

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
        maxLabelsBounds: DoubleRectangle?
    ): AxisLabelsLayoutInfo {

        val height = labelSpec.height()
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)
        var overlap = false
        if (breaks.size >= 2) {
            val minTickDistance = abs((height + MIN_DISTANCE) / SIN)
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
        // only works for RIGHT-TOP anchor ang angle 0...-90
        if (!(ROTATION_DEGREE >= -90 && ROTATION_DEGREE <= 0
                    && labelHorizontalAnchor === Text.HorizontalAnchor.RIGHT
                    && labelVerticalAnchor === Text.VerticalAnchor.TOP)
        ) {
            throw RuntimeException("Not implemented")
        }


        val w = abs(labelNormalSize.x * COS) + 2 * abs(labelNormalSize.y * SIN)
        val h = abs(labelNormalSize.x * SIN) + abs(labelNormalSize.y * COS)
        val x = -(abs(labelNormalSize.x * COS) + abs(labelNormalSize.y * SIN))
        val y = 0.0

        return DoubleRectangle(x, y, w, h)
    }

    companion object {
        private const val MIN_DISTANCE = 5.0
        private const val ROTATION_DEGREE = -30.0

        private val SIN = sin(toRadians(ROTATION_DEGREE))
        private val COS = cos(toRadians(ROTATION_DEGREE))
    }
}
