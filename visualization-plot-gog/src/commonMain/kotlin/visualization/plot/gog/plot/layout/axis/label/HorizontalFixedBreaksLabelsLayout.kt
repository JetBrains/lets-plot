package jetbrains.datalore.visualization.plot.gog.plot.layout.axis.label

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.plot.guide.Orientation
import jetbrains.datalore.visualization.plot.gog.plot.layout.axis.GuideBreaks
import jetbrains.datalore.visualization.plot.gog.plot.presentation.PlotLabelSpec
import jetbrains.datalore.visualization.plot.gog.plot.theme.AxisTheme

internal class HorizontalFixedBreaksLabelsLayout(orientation: Orientation,
                                                 axisDomain: ClosedRange<Double>, labelSpec: PlotLabelSpec, breaks: GuideBreaks, theme: AxisTheme) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {
    private fun overlap(labelsInfo: AxisLabelsLayoutInfo, maxTickLabelsBounds: DoubleRectangle?): Boolean {
        return labelsInfo.isOverlap || maxTickLabelsBounds != null && !(maxTickLabelsBounds.xRange().encloses(labelsInfo.bounds!!.xRange()) && maxTickLabelsBounds.yRange().encloses(labelsInfo.bounds.yRange()))
    }


    init {
        checkArgument(orientation.isHorizontal, orientation.toString())
    }

    override fun doLayout(axisLength: Double, axisMapper: (Double) -> Double, maxLabelsBounds: DoubleRectangle): AxisLabelsLayoutInfo {
        if (!theme.showTickLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        var labelsInfo = simpleLayout().doLayout(axisLength, axisMapper, maxLabelsBounds)
        if (overlap(labelsInfo, maxLabelsBounds)) {
            labelsInfo = multilineLayout().doLayout(axisLength, axisMapper, maxLabelsBounds)
            if (overlap(labelsInfo, maxLabelsBounds)) {
                labelsInfo = tiltedLayout().doLayout(axisLength, axisMapper, maxLabelsBounds)
                if (overlap(labelsInfo, maxLabelsBounds)) {
                    labelsInfo = verticalLayout(labelSpec).doLayout(axisLength, axisMapper, maxLabelsBounds)
                    if (overlap(labelsInfo, maxLabelsBounds)) {
                        labelsInfo = verticalLayout(TICK_LABEL_SPEC_SMALL).doLayout(axisLength, axisMapper, maxLabelsBounds)
                    }
                }
            }
        }
        return labelsInfo
    }

    private fun simpleLayout(): AxisLabelsLayout {
        return HorizontalSimpleLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme)
    }

    private fun multilineLayout(): AxisLabelsLayout {
        return HorizontalMultilineLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme, 2)
    }

    private fun tiltedLayout(): AxisLabelsLayout {
        return HorizontalTiltedLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme)
    }

    private fun verticalLayout(labelSpec: PlotLabelSpec): AxisLabelsLayout {
        return HorizontalVerticalLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme)
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        throw IllegalStateException("Not implemented here")
    }
}
