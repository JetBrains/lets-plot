/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProvider

internal class VerticalFlexBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme
) : AxisLabelsLayout(orientation, axisDomain, theme) {

    init {
        require(!orientation.isHorizontal) { orientation.toString() }
        require(!myBreaksProvider.isFixedBreaks) { "fixed breaks" }
    }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        require(axisLength > 0) { "axis length: $axisLength" }

        val rotationAngle = if (theme.rotateLabels()) theme.labelAngle() else null

        var targetBreakCount = BreakLabelsLayoutUtil.estimateBreakCountInitial(
            axisLength,
            labelSpec,
            rotationAngle,
            side = DoubleVector::y
        )

        var breaks = getBreaks(targetBreakCount)
        var labelsInfo = doLayoutLabels(breaks, axisLength, axisMapper)

        while (labelsInfo.isOverlap) {
            // reduce tick count
            val newTargetBreakCount = BreakLabelsLayoutUtil.estimateBreakCount(
                breaks.labels,
                axisLength,
                labelSpec,
                rotationAngle,
                side = DoubleVector::y

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

    private fun getBreaks(maxCount: Int): ScaleBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(myBreaksProvider, maxCount)
    }

    private fun doLayoutLabels(
        breaks: ScaleBreaks,
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
    ): AxisLabelsLayoutInfo {
        return BreakLabelsLayoutUtil.doLayoutVerticalAxisLabels(
            orientation,
            axisDomain,
            labelSpec,
            breaks,
            theme,
            axisLength,
            axisMapper
        )
    }
}
