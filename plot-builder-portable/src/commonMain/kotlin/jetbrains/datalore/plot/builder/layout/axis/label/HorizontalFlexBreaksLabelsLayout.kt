/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProvider
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class HorizontalFlexBreaksLabelsLayout(
    orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    axisDomain: ClosedRange<Double>,
    labelSpec: PlotLabelSpec,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme) :
        AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {

    init {
        checkArgument(orientation.isHorizontal, orientation.toString())
        checkArgument(!myBreaksProvider.isFixedBreaks, "fixed breaks")
    }

    override fun doLayout(
            axisLength: Double,
            axisMapper: (Double?) -> Double?,
            maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        var targetBreakCount =
            HorizontalSimpleLabelsLayout.estimateBreakCountInitial(
                axisLength
            )
        var breaks = getBreaks(targetBreakCount, axisLength)
        var labelsInfo = doLayoutLabels(breaks, axisLength, axisMapper, maxLabelsBounds)

        while (labelsInfo.isOverlap) {
            // reduce tick count
            val newTargetBreakCount =
                HorizontalSimpleLabelsLayout.estimateBreakCount(
                    breaks.labels,
                    axisLength
                )
            if (newTargetBreakCount >= targetBreakCount) {
                // paranoid - highly impossible.
                break
            }
            targetBreakCount = newTargetBreakCount
            breaks = getBreaks(targetBreakCount, axisLength)
            labelsInfo = doLayoutLabels(breaks, axisLength, axisMapper, maxLabelsBounds)
        }

        return labelsInfo
    }

    private fun doLayoutLabels(
        breaks: GuideBreaks,
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
        maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        val layout = HorizontalSimpleLabelsLayout(
            orientation,
            axisDomain,
            labelSpec,
            breaks,
            theme
        )
        return layout.doLayout(axisLength, axisMapper, maxLabelsBounds)
    }

    private fun getBreaks(maxCount: Int, axisLength: Double): GuideBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(
            myBreaksProvider,
            maxCount,
            axisLength
        )
    }
}
