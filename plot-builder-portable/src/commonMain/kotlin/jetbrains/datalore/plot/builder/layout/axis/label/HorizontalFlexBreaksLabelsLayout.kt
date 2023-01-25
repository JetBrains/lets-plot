/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.PlotLabelSpecFactory
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.max

internal class HorizontalFlexBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme
) :
    AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {
    private val myRotationAngle: Double? = theme.labelAngle()

    init {
        require(orientation.isHorizontal) { orientation.toString() }
        require(!myBreaksProvider.isFixedBreaks) { "fixed breaks" }
    }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        var targetBreakCount = estimateBreakCountInitial(
            axisLength,
            PlotLabelSpecFactory.axisTick(theme),
            myRotationAngle
        )
        var breaks = getBreaks(targetBreakCount, axisLength)
        var labelsInfo = doLayoutLabels(breaks, axisLength, axisMapper)

        while (labelsInfo.isOverlap) {
            // reduce tick count
            val newTargetBreakCount = estimateBreakCount(
                breaks.labels,
                axisLength,
                PlotLabelSpecFactory.axisTick(theme),
                myRotationAngle
            )
            if (newTargetBreakCount >= targetBreakCount) {
                // paranoid - highly impossible.
                break
            }
            targetBreakCount = newTargetBreakCount
            breaks = getBreaks(targetBreakCount, axisLength)
            labelsInfo = doLayoutLabels(breaks, axisLength, axisMapper)
        }

        return labelsInfo
    }

    private fun doLayoutLabels(
        breaks: ScaleBreaks,
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
    ): AxisLabelsLayoutInfo {
        val layout = if (myRotationAngle != null) {
            HorizontalRotatedLabelsLayout(
                orientation,
                axisDomain,
                labelSpec,
                breaks,
                theme,
                myRotationAngle
            )
        } else {
            HorizontalSimpleLabelsLayout(
                orientation,
                axisDomain,
                labelSpec,
                breaks,
                theme
            )
        }
        return layout.doLayout(axisLength, axisMapper)
    }

    private fun getBreaks(maxCount: Int, axisLength: Double): ScaleBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(
            myBreaksProvider,
            maxCount,
            axisLength
        )
    }

    companion object {
        private fun estimateBreakCountInitial(
            axisLength: Double,
            tickLabelSpec: LabelSpec,
            rotationAngle: Double?
        ): Int {
            val initialDim = tickLabelSpec.dimensions(INITIAL_TICK_LABEL)
            val width = if (rotationAngle != null) {
                BreakLabelsLayoutUtil.rotatedLabelBounds(initialDim, rotationAngle).width
            } else {
                initialDim.x
            }
            return estimateBreakCount(
                width,
                axisLength
            )
        }

        private fun estimateBreakCount(
            labels: List<String>,
            axisLength: Double,
            axisTick: LabelSpec,
            rotationAngle: Double?
        ): Int {
            val longestLabelWidth = if (rotationAngle != null) {
                labels.map { label ->
                    val dim = axisTick.dimensions(label)
                    BreakLabelsLayoutUtil.rotatedLabelBounds(dim, rotationAngle)
                }.maxOfOrNull(DoubleRectangle::width) ?: 0.0
            } else {
                BreakLabelsLayoutUtil.longestLabelWidth(labels, axisTick::width)
            }
            return estimateBreakCount(
                longestLabelWidth,
                axisLength
            )
        }

        private fun estimateBreakCount(width: Double, axisLength: Double): Int {
            val tickDistance = width + MIN_TICK_LABEL_DISTANCE
            return max(1.0, axisLength / tickDistance).toInt()
        }
    }
}
