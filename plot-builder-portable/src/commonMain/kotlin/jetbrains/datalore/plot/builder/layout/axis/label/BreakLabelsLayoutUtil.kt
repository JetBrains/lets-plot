/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.guide.Orientation.*
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.max
import kotlin.math.min

internal object BreakLabelsLayoutUtil {
    fun getFlexBreaks(breaksProvider: AxisBreaksProvider, maxCount: Int, axisLength: Double): GuideBreaks {
        checkArgument(!breaksProvider.isFixedBreaks, "fixed breaks not expected")
        checkArgument(maxCount > 0, "maxCount=$maxCount")
        var breaks = breaksProvider.getBreaks(maxCount, axisLength)

        if (maxCount == 1 && !breaks.isEmpty) {
            return GuideBreaks(
                breaks.domainValues.subList(
                    0,
                    1
                ), breaks.transformedValues.subList(0, 1), breaks.labels.subList(0, 1)
            )
        }
        var count = maxCount
        while (breaks.size() > maxCount) {
            val delta = max(1, (breaks.size() - maxCount) / 2)
            count -= delta
            breaks = breaksProvider.getBreaks(count, axisLength)
        }
        return breaks
    }

    fun maxLength(labels: List<String>): Int {
        var max = 0
        for (label in labels) {
            max = max(max, label.length)
        }
        return max
    }

    fun horizontalCenteredLabelBounds(labelSize: DoubleVector): DoubleRectangle {
        return DoubleRectangle(-labelSize.x / 2.0, 0.0, labelSize.x, labelSize.y)
    }

    fun doLayoutVerticalAxisLabels(
        orientation: jetbrains.datalore.plot.builder.guide.Orientation,
        breaks: GuideBreaks,
        axisDomain: ClosedRange<Double>,
        axisMapper: (Double?) -> Double?,
        theme: AxisTheme): AxisLabelsLayoutInfo {

        val axisBounds = when {
            theme.showTickLabels() -> {
                val labelsBounds =
                    verticalAxisLabelsBounds(
                        breaks,
                        axisDomain,
                        axisMapper
                    )
                applyLabelsOffset(
                    labelsBounds,
                    theme.tickLabelDistance(),
                    orientation
                )
            }
            theme.showTickMarks() -> {
                val labelsBounds = DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
                applyLabelsOffset(
                    labelsBounds,
                    theme.tickLabelDistance(),
                    orientation
                )
            }
            else -> DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)
        }

        return AxisLabelsLayoutInfo.Builder()
                .breaks(breaks)
                .bounds(axisBounds)     // labels bounds actually
                .build()
    }

    fun mapToAxis(breaks: List<Double>, axisDomain: ClosedRange<Double>, axisMapper: (Double?) -> Double?): List<Double> {
        val axisMin = axisDomain.lowerEnd
        val axisBreaks = ArrayList<Double>()
        for (v in breaks) {
            val mapped = axisMapper(v - axisMin)
            axisBreaks.add(mapped!!)
        }
        return axisBreaks
    }

    fun applyLabelsOffset(labelsBounds: DoubleRectangle, offset: Double, orientation: jetbrains.datalore.plot.builder.guide.Orientation): DoubleRectangle {
        @Suppress("NAME_SHADOWING")
        var labelsBounds = labelsBounds
        val offsetVector = when (orientation) {
            LEFT -> DoubleVector(-offset, 0.0)
            RIGHT -> DoubleVector(offset, 0.0)
            TOP -> DoubleVector(0.0, -offset)
            BOTTOM -> DoubleVector(0.0, offset)
        }

        if (orientation === RIGHT || orientation === BOTTOM) {
            labelsBounds = labelsBounds.add(offsetVector)
        } else if (orientation === LEFT || orientation === TOP) {
            labelsBounds = labelsBounds.add(offsetVector).subtract(DoubleVector(labelsBounds.width, 0.0))
        }

        return labelsBounds
    }


    private fun verticalAxisLabelsBounds(breaks: GuideBreaks, axisDomain: ClosedRange<Double>, axisMapper: (Double?) -> Double?): DoubleRectangle {
        val maxLength =
            maxLength(breaks.labels)
        val maxLabelWidth = AxisLabelsLayout.TICK_LABEL_SPEC.width(maxLength)
        var y1 = 0.0
        var y2 = 0.0
        if (!breaks.isEmpty) {
            val axisBreaks =
                mapToAxis(
                    breaks.transformedValues,
                    axisDomain,
                    axisMapper
                )

            y1 = min(axisBreaks[0], Iterables.getLast(axisBreaks))
            y2 = max(axisBreaks[0], Iterables.getLast(axisBreaks))
            y1 -= AxisLabelsLayout.TICK_LABEL_SPEC.height() / 2
            y2 += AxisLabelsLayout.TICK_LABEL_SPEC.height() / 2
        }

        val origin = DoubleVector(0.0, y1)
        val dimensions = DoubleVector(maxLabelWidth, y2 - y1)
        return DoubleRectangle(origin, dimensions)
    }
}
