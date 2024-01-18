/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.layout.Margins
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation.*
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProvider
import org.jetbrains.letsPlot.core.plot.builder.presentation.LabelSpec
import kotlin.math.*

internal object BreakLabelsLayoutUtil {

    fun getFlexBreaks(breaksProvider: AxisBreaksProvider, maxCount: Int): ScaleBreaks {
        require(!breaksProvider.isFixedBreaks) { "fixed breaks not expected" }
        require(maxCount > 0) { "maxCount=$maxCount" }
        var breaks = breaksProvider.getBreaks(maxCount)

        if (maxCount == 1 && !breaks.isEmpty) {
            return ScaleBreaks(
                breaks.domainValues.subList(0, 1),
                breaks.transformedValues.subList(0, 1),
                breaks.labels.subList(0, 1)
            )
        }
        var count = maxCount
        while (breaks.size > maxCount) {
            val delta = max(1, (breaks.size - maxCount) / 2)
            count -= delta
            if (count <= 1) {
                breaks = breaksProvider.getBreaks(1)
                break
            }
            breaks = breaksProvider.getBreaks(count)
        }
        return breaks
    }

    fun horizontalCenteredLabelBounds(labelSize: DoubleVector): DoubleRectangle {
        return DoubleRectangle(-labelSize.x / 2.0, 0.0, labelSize.x, labelSize.y)
    }

    fun doLayoutVerticalAxisLabels(
        orientation: Orientation,
        labelSpec: LabelSpec,
        breaks: ScaleBreaks,
        theme: AxisTheme,
        axisDomain: DoubleSpan,
        axisLength: Double,
    ): AxisLabelsLayoutInfo {
        check(!orientation.isHorizontal)

        if (theme.showLabels() && theme.rotateLabels()) {
            return VerticalRotatedLabelsLayout(
                orientation,
                breaks,
                theme,
                theme.labelAngle()
            ).doLayout(axisDomain, axisLength)
        }

        val tickLength = if (theme.showTickMarks()) theme.tickMarkLength() else 0.0
        val axisBounds = when {
            theme.showLabels() -> {
                val labelsBounds = verticalAxisLabelsBounds(
                    breaks,
                    projectedBreaks = breaks.projectOnAxis(axisDomain, axisLength, isHorizontal = false),
                    tickLabelSpec = labelSpec
                )
                applyLabelMargins(
                    labelsBounds,
                    tickLength,
                    theme.tickLabelMargins(),
                    orientation
                )
            }

            theme.showTickMarks() -> {
                val labelsBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                applyLabelMargins(
                    labelsBounds,
                    tickLength,
                    theme.tickLabelMargins(),
                    orientation
                )
            }

            else -> DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        }

        return AxisLabelsLayoutInfo.Builder()
            .breaks(breaks)
            .bounds(axisBounds)     // label bounds actually
//            .labelHorizontalAnchor(), // Default anchors,
//            .labelVerticalAnchor()    // see: AxisComponent.TickLabelAdjustments
            .build()
    }

    // Expands to borders with margins
    fun applyLabelMargins(
        bounds: DoubleRectangle,
        tickLength: Double,
        margins: Margins,
        orientation: Orientation
    ): DoubleRectangle {
        val origin = alignToLabelMargin(bounds, tickLength, margins, orientation).let {
            val offset = when {
                orientation.isHorizontal -> DoubleVector(0.0, margins.top)
                else -> DoubleVector(margins.left, 0.0)
            }
            it.subtract(offset).origin
        }
        val dimension = bounds.dimension.add(
            when {
                orientation.isHorizontal -> DoubleVector(0.0, margins.height())
                else -> DoubleVector(margins.width(), 0.0)
            }
        )
        return DoubleRectangle(origin, dimension)
    }

    // Moves a rectangle on the border (aligns to the margin)
    fun alignToLabelMargin(
        bounds: DoubleRectangle,
        tickLength: Double,
        margins: Margins,
        orientation: Orientation
    ): DoubleRectangle {
        val offset = tickLength + when (orientation) {
            LEFT -> margins.right + bounds.width
            TOP -> margins.bottom + bounds.height
            RIGHT -> margins.left
            BOTTOM -> margins.top
        }
        val offsetVector = when (orientation) {
            LEFT -> DoubleVector(-offset, 0.0)
            RIGHT -> DoubleVector(offset, 0.0)
            TOP -> DoubleVector(0.0, -offset)
            BOTTOM -> DoubleVector(0.0, offset)
        }
        return bounds.add(offsetVector)
    }

    fun textBounds(elementRect: DoubleRectangle, margins: Margins, orientation: Orientation): DoubleRectangle? {
        if (elementRect.width == 0.0 || elementRect.height == 0.0) {
            return null
        }
        return when {
            orientation.isHorizontal -> {
                DoubleRectangle(
                    elementRect.left,
                    elementRect.top + margins.top,
                    elementRect.width,
                    elementRect.height - margins.height()
                )
            }

            else -> {
                DoubleRectangle(
                    elementRect.left + margins.left,
                    elementRect.top,
                    elementRect.width - margins.width(),
                    elementRect.height
                )
            }
        }
    }

    fun rotatedLabelBounds(labelNormalSize: DoubleVector, degreeAngle: Double): DoubleRectangle {
        val angle = toRadians(degreeAngle)
        val sin = sin(angle)
        val cos = cos(angle)
        val w = abs(labelNormalSize.x * cos) + abs(labelNormalSize.y * sin)
        val h = abs(labelNormalSize.x * sin) + abs(labelNormalSize.y * cos)
        val x = -(abs(labelNormalSize.x * cos) + abs(labelNormalSize.y * sin))
        val y = 0.0

        return DoubleRectangle(x, y, w, h)
    }

    private fun verticalAxisLabelsBounds(
        breaks: ScaleBreaks,
        projectedBreaks: List<Double>,  // coordinates on axis
        tickLabelSpec: LabelSpec
    ): DoubleRectangle {
        val maxLabelWidth = breaks.labels.maxOfOrNull(tickLabelSpec::width) ?: 0.0
        var y1 = 0.0
        var y2 = 0.0
        if (!breaks.isEmpty) {

            y1 = min(projectedBreaks[0], projectedBreaks.last())
            y2 = max(projectedBreaks[0], projectedBreaks.last())
            y1 -= tickLabelSpec.height() / 2
            y2 += tickLabelSpec.height() / 2
        }

        val origin = DoubleVector(0.0, y1)
        val dimensions = DoubleVector(maxLabelWidth, y2 - y1)
        return DoubleRectangle(origin, dimensions)
    }

    fun estimateBreakCountInitial(
        axisLength: Double,
        tickLabelSpec: LabelSpec,
        rotationAngle: Double?,
        side: (DoubleVector) -> Double
    ): Int {
        val initialDim = tickLabelSpec.dimensions(AxisLabelsLayout.INITIAL_TICK_LABEL)
        val dimension = if (rotationAngle != null) {
            rotatedLabelBounds(initialDim, rotationAngle).dimension
        } else {
            initialDim
        }
        return estimateBreakCount(side(dimension), axisLength)
    }

    fun estimateBreakCount(
        labels: List<String>,
        axisLength: Double,
        tickLabelSpec: LabelSpec,
        rotationAngle: Double?,
        side: (DoubleVector) -> Double
    ): Int {
        val dims = labels.map { label ->
            if (rotationAngle != null) {
                rotatedLabelBounds(tickLabelSpec.dimensions(label), rotationAngle).dimension
            } else {
                tickLabelSpec.dimensions(label)
            }
        }
        val longestSide = dims.maxOfOrNull(side) ?: 0.0
        return estimateBreakCount(longestSide, axisLength)
    }

    private fun estimateBreakCount(length: Double, axisLength: Double): Int {
        val tickDistance = length + AxisLabelsLayout.MIN_TICK_LABEL_DISTANCE
        return max(1.0, axisLength / tickDistance).toInt()
    }
}
