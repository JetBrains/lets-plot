/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.guide.Orientation.*
import jetbrains.datalore.plot.builder.layout.Margins
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.max
import kotlin.math.min

internal object BreakLabelsLayoutUtil {

    fun getFlexBreaks(breaksProvider: AxisBreaksProvider, maxCount: Int, axisLength: Double): ScaleBreaks {
        require(!breaksProvider.isFixedBreaks) { "fixed breaks not expected" }
        require(maxCount > 0) { "maxCount=$maxCount" }
        var breaks = breaksProvider.getBreaks(maxCount, axisLength)

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
                breaks = breaksProvider.getBreaks(1, axisLength)
                break
            }
            breaks = breaksProvider.getBreaks(count, axisLength)
        }
        return breaks
    }

    fun longestLabelWidth(
        labels: List<String>,
        labelToWidth: (String) -> Double = { it.length.toDouble() }
    ): Double {
        val longestLabel = labels.maxByOrNull(labelToWidth)
        return longestLabel?.let(labelToWidth) ?: 0.0
    }

    fun horizontalCenteredLabelBounds(labelSize: DoubleVector): DoubleRectangle {
        return DoubleRectangle(-labelSize.x / 2.0, 0.0, labelSize.x, labelSize.y)
    }

    fun doLayoutVerticalAxisLabels(
        orientation: Orientation,
        breaks: ScaleBreaks,
        axisDomain: DoubleSpan,
        axisMapper: (Double?) -> Double?,
        theme: AxisTheme
    ): AxisLabelsLayoutInfo {
        check(!orientation.isHorizontal)

        val tickLength = if (theme.showTickMarks()) theme.tickMarkLength() else 0.0
        val axisBounds = when {
            theme.showLabels() -> {
                val labelsBounds = verticalAxisLabelsBounds(
                    breaks,
                    axisDomain,
                    axisMapper,
                    PlotLabelSpecFactory.axisTick(theme)
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

    fun mapToAxis(
        breaks: List<Double>,
        axisDomain: DoubleSpan,
        axisMapper: (Double?) -> Double?
    ): List<Double> {
        val axisMin = axisDomain.lowerEnd
        val axisBreaks = ArrayList<Double>()
        for (v in breaks) {
            val mapped = axisMapper(v - axisMin)
            axisBreaks.add(mapped!!)
        }
        return axisBreaks
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
            RIGHT-> margins.left
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

    fun textBounds(elementRect: DoubleRectangle, margins: Margins, orientation: Orientation): DoubleRectangle {
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

    private fun verticalAxisLabelsBounds(
        breaks: ScaleBreaks,
        axisDomain: DoubleSpan,
        axisMapper: (Double?) -> Double?,
        tickLabelSpec: LabelSpec
    ): DoubleRectangle {
        val maxLabelWidth = longestLabelWidth(breaks.labels) { tickLabelSpec.width(it) }
        var y1 = 0.0
        var y2 = 0.0
        if (!breaks.isEmpty) {
            val axisBreaks = mapToAxis(
                breaks.transformedValues,
                axisDomain,
                axisMapper
            )

            y1 = min(axisBreaks[0], axisBreaks.last())
            y2 = max(axisBreaks[0], axisBreaks.last())
            y1 -= tickLabelSpec.height() / 2
            y2 += tickLabelSpec.height() / 2
        }

        val origin = DoubleVector(0.0, y1)
        val dimensions = DoubleVector(maxLabelWidth, y2 - y1)
        return DoubleRectangle(origin, dimensions)
    }
}
