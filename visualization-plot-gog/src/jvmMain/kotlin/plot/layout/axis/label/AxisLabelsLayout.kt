package jetbrains.datalore.visualization.plot.gog.plot.layout.axis.label

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.AxisBreaksProvider
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.GuideBreaks
import jetbrains.datalore.visualization.plot.gog.plot.presentation.PlotLabelSpec
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme

abstract class AxisLabelsLayout protected constructor(val orientation: Orientation, val axisDomain: ClosedRange<Double>, val labelSpec: PlotLabelSpec, val theme: AxisTheme) {

    protected val isHorizontal: Boolean
        get() = orientation.isHorizontal

    abstract fun doLayout(axisLength: Double, axisMapper: (Double) -> Double, maxLabelsBounds: DoubleRectangle): AxisLabelsLayoutInfo

    internal fun mapToAxis(breaks: List<Double>, axisMapper: (Double) -> Double): List<Double> {
        return BreakLabelsLayoutUtil.mapToAxis(breaks, axisDomain, axisMapper)
    }

    internal fun applyLabelsOffset(labelsBounds: DoubleRectangle): DoubleRectangle {
        return BreakLabelsLayoutUtil.applyLabelsOffset(labelsBounds, theme.tickLabelDistance(), orientation)
    }

    companion object {
        val TICK_LABEL_SPEC = PlotLabelSpec.AXIS_TICK
        val INITIAL_TICK_LABEL_LENGTH = 4 // symbols
        val MIN_TICK_LABEL_DISTANCE = 20.0  // px
        val TICK_LABEL_SPEC_SMALL = PlotLabelSpec.AXIS_TICK_SMALL

        fun horizontalFlexBreaks(orientation: Orientation,
                                 axisDomain: ClosedRange<Double>, breaksProvider: AxisBreaksProvider, theme: AxisTheme): AxisLabelsLayout {

            checkArgument(orientation.isHorizontal, orientation.toString())
            checkArgument(!breaksProvider.isFixedBreaks, "fixed breaks")
            return HorizontalFlexBreaksLabelsLayout(orientation, axisDomain, TICK_LABEL_SPEC, breaksProvider, theme)
        }

        fun horizontalFixedBreaks(orientation: Orientation,
                                  axisDomain: ClosedRange<Double>, breaks: GuideBreaks, theme: AxisTheme): AxisLabelsLayout {

            checkArgument(orientation.isHorizontal, orientation.toString())
            return HorizontalFixedBreaksLabelsLayout(orientation, axisDomain, TICK_LABEL_SPEC, breaks, theme)
        }

        fun verticalFlexBreaks(orientation: Orientation,
                               axisDomain: ClosedRange<Double>, breaksProvider: AxisBreaksProvider, theme: AxisTheme): AxisLabelsLayout {

            checkArgument(!orientation.isHorizontal, orientation.toString())
            checkArgument(!breaksProvider.isFixedBreaks, "fixed breaks")
            return VerticalFlexBreaksLabelsLayout(orientation, axisDomain, TICK_LABEL_SPEC, breaksProvider, theme)
        }

        fun verticalFixedBreaks(orientation: Orientation, axisDomain: ClosedRange<Double>, breaks: GuideBreaks, theme: AxisTheme): AxisLabelsLayout {
            checkArgument(!orientation.isHorizontal, orientation.toString())
            return VerticalFixedBreaksLabelsLayout(orientation, axisDomain, TICK_LABEL_SPEC, breaks, theme)
        }
    }
}
