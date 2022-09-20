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
import kotlin.math.ceil

internal class VerticalFlexBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    private val myBreaksProvider: AxisBreaksProvider,
    theme: AxisTheme
) :
    AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {

    private fun maxTickCount(axisLength: Double): Int {
        return ceil(axisLength / (PlotLabelSpecFactory.axisTick(theme).height() + MIN_TICK_LABEL_DISTANCE)).toInt()
    }

    init {
        require(!orientation.isHorizontal) { orientation.toString() }
        require(!myBreaksProvider.isFixedBreaks) { "fixed breaks" }
    }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
        maxLabelsBounds: DoubleRectangle?
    ): AxisLabelsLayoutInfo {

        require(axisLength > 0) { "axis length: $axisLength" }
        val maxTickCount = maxTickCount(axisLength)
        val breaks = getBreaks(maxTickCount, axisLength)

        return BreakLabelsLayoutUtil.doLayoutVerticalAxisLabels(
            orientation, breaks,
            axisDomain,
            axisMapper,
            theme
        )
    }

    protected fun getBreaks(maxCount: Int, axisLength: Double): ScaleBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(
            myBreaksProvider,
            maxCount,
            axisLength
        )
    }
}
