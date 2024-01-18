/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProvider

internal class HorizontalFlexBreaksLabelsLayout(
    orientation: Orientation,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme
) : AxisLabelsLayout(
    orientation,
    theme
) {

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
        var breaks = getBreaks(targetBreakCount)
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
            breaks = getBreaks(targetBreakCount)
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
                breaks,
                theme,
                myRotationAngle
            )
        } else {
            HorizontalSimpleLabelsLayout(
                orientation,
                breaks,
                theme
            )
        }
        return layout.doLayout(axisLength, axisMapper)
    }

    private fun getBreaks(maxCount: Int): ScaleBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(myBreaksProvider, maxCount)
    }
}
