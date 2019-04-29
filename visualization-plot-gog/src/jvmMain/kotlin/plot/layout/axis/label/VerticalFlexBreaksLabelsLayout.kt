package jetbrains.datalore.visualization.plot.gog.plot.layout.axis.label

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.AxisBreaksProvider
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.GuideBreaks
import jetbrains.datalore.visualization.plot.gog.plot.presentation.PlotLabelSpec
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme

internal class VerticalFlexBreaksLabelsLayout(orientation: Orientation, axisDomain: ClosedRange<Double>,
                                              labelSpec: PlotLabelSpec, private val myBreaksProvider: AxisBreaksProvider, theme: AxisTheme) : AxisLabelsLayout(orientation, axisDomain, labelSpec, theme) {
    private fun maxTickCount(axisLength: Double): Int {
        return Math.ceil(axisLength / (AxisLabelsLayout.TICK_LABEL_SPEC.height() + MIN_TICK_LABEL_DISTANCE)).toInt()
    }

    init {
        checkArgument(!orientation.isHorizontal, orientation.toString())
        checkArgument(!myBreaksProvider.isFixedBreaks, "fixed breaks")
    }

    override fun doLayout(axisLength: Double, axisMapper: (Double) -> Double, maxLabelsBounds: DoubleRectangle): AxisLabelsLayoutInfo {
        checkArgument(axisLength > 0, "axis length: $axisLength")
        val maxTickCount = maxTickCount(axisLength)
        val breaks = getBreaks(maxTickCount, axisLength)

        return BreakLabelsLayoutUtil.doLayoutVerticalAxisLabels(
                orientation, breaks,
                axisDomain,
                axisMapper,
                theme)
    }

    protected fun getBreaks(maxCount: Int, axisLength: Double): GuideBreaks {
        return BreakLabelsLayoutUtil.getFlexBreaks(myBreaksProvider, maxCount, axisLength)
    }
}
