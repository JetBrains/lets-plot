/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class HorizontalFlexBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme
) : AxisLabelsLayout(orientation, axisDomain, theme) {

    private val myRotationAngle = if (theme.rotateLabels()) theme.labelAngle() else null

    init {
        require(orientation.isHorizontal) { orientation.toString() }
        require(!myBreaksProvider.isFixedBreaks) { "fixed breaks" }
    }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        var targetBreakCount = BreakLabelsLayoutUtil.estimateBreakCountInitial(
            axisLength,
            labelSpec,
            myRotationAngle,
            side = DoubleVector::x
        )
        var breaks = getBreaks(targetBreakCount, axisLength)
        var labelsInfo = doLayoutLabels(breaks, axisLength, axisMapper)

        while (labelsInfo.isOverlap) {
            // reduce tick count
            val newTargetBreakCount = BreakLabelsLayoutUtil.estimateBreakCount(
                breaks.labels,
                axisLength,
                labelSpec,
                myRotationAngle,
                side = DoubleVector::x
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
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {
        val layout = if (myRotationAngle != null) {
            HorizontalRotatedLabelsLayout(
                orientation,
                axisDomain,
                breaks,
                theme,
                myRotationAngle
            )
        } else {
            HorizontalSimpleLabelsLayout(
                orientation,
                axisDomain,
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
}
