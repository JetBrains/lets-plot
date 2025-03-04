/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation.BOTTOM
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation.TOP
import kotlin.math.abs

internal class HorizontalVerticalLabelsLayout(
    orientation: Orientation,
    breaks: ScaleBreaks,
    theme: AxisTheme
) : AbstractFixedBreaksLabelsLayout(
    orientation,
    breaks,
    theme
) {

    val labelHorizontalAnchor: Text.HorizontalAnchor
        get() = when (orientation) {
            TOP -> Text.HorizontalAnchor.LEFT
            BOTTOM -> Text.HorizontalAnchor.RIGHT
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }

    val labelVerticalAnchor: Text.VerticalAnchor = Text.VerticalAnchor.CENTER

    override fun doLayout(
        axisDomain: DoubleSpan,
        axisLength: Double,
    ): AxisLabelsLayoutInfo {

        val height = labelSpec.height()
        val ticks = breaks.projectOnAxis(axisDomain, axisLength, isHorizontal = true)
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
            .labelRotationAngle(-ROTATION_DEGREE)
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        check(labelVerticalAnchor === Text.VerticalAnchor.CENTER)

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
