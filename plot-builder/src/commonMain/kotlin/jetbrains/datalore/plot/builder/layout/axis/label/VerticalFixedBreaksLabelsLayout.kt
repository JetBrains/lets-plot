package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class VerticalFixedBreaksLabelsLayout(
    orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    axisDomain: ClosedRange<Double>,
    tickLabelSpec: PlotLabelSpec,
    breaks: GuideBreaks,
    theme: AxisTheme) :
        AbstractFixedBreaksLabelsLayout(orientation, axisDomain, tickLabelSpec, breaks, theme) {

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        throw IllegalStateException("Not implemented here")
    }

    override fun doLayout(
            axisLength: Double,
            axisMapper: (Double?) -> Double?,
            maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        return BreakLabelsLayoutUtil.doLayoutVerticalAxisLabels(
                orientation, breaks,
                axisDomain,
                axisMapper,
                theme)
    }
}
