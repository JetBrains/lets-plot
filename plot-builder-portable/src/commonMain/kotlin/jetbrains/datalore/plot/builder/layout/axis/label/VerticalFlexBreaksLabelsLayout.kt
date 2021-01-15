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
import kotlin.math.ceil

internal class VerticalFlexBreaksLabelsLayout(
    orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    axisDomain: ClosedRange<Double>,
    labelSpec: PlotLabelSpec,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme) :
        AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {

    private fun maxTickCount(axisLength: Double): Int {
        return ceil(axisLength / (TICK_LABEL_SPEC.height() + MIN_TICK_LABEL_DISTANCE)).toInt()
    }

    init {
        checkArgument(!orientation.isHorizontal, orientation.toString())
        checkArgument(!myBreaksProvider.isFixedBreaks, "fixed breaks")
    }

    override fun doLayout(
            axisLength: Double,
            axisMapper: (Double?) -> Double?,
            maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        checkArgument(axisLength > 0, "axis length: $axisLength")
        val maxTickCount = maxTickCount(axisLength)
        val breaks = getBreaks(maxTickCount)

        return BreakLabelsLayoutUtil.doLayoutVerticalAxisLabels(
            orientation, breaks,
            axisDomain,
            axisMapper,
            theme
        )
    }

    private fun getBreaks(maxCount: Int): GuideBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(
            myBreaksProvider,
            maxCount
        )
    }
}
