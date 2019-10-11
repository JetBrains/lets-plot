package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.layout.axis.GuideBreaks
import jetbrains.datalore.plot.builder.presentation.PlotLabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max

internal class HorizontalSimpleLabelsLayout(
    orientation: jetbrains.datalore.plot.builder.guide.Orientation,
    axisDomain: ClosedRange<Double>,
    labelSpec: PlotLabelSpec,
    breaks: GuideBreaks,
    theme: AxisTheme) :
        AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    override fun doLayout(
            axisLength: Double,
            axisMapper: (Double?) -> Double?,
            maxLabelsBounds: DoubleRectangle?): AxisLabelsLayoutInfo {

        if (breaks.isEmpty) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        if (!theme.showTickLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        var bounds: DoubleRectangle? = null
        var overlap = false
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)

        val boundsList = labelBoundsList(ticks, breaks.labels, HORIZONTAL_TICK_LOCATION)
        for (labelBounds in boundsList) {
            overlap = overlap || bounds != null && bounds.xRange().isConnected(
                    SeriesUtil.expand(labelBounds.xRange(), MIN_TICK_LABEL_DISTANCE / 2, MIN_TICK_LABEL_DISTANCE / 2.0))
            bounds = GeometryUtil.union(labelBounds, bounds)
        }

        return AxisLabelsLayoutInfo.Builder()
                .breaks(breaks)
                .bounds(applyLabelsOffset(bounds!!))
                .smallFont(false)
                .overlap(overlap)
                .labelAdditionalOffsets(null)
                .labelHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
                .labelVerticalAnchor(TextLabel.VerticalAnchor.TOP)
                .build()
    }

    /*
  private AxisLabelsLayoutInfo noLabelsLayoutInfo(double axisLength) {
    DoubleRectangle bounds = new DoubleRectangle(axisLength / 2, 0, 0, 0); // empty bounds in the middle of the axis;
    bounds = BreakLabelsLayoutUtil.applyLabelsOffset(bounds, myTheme.tickLabelDistance(), getOrientation());
    return new AxisLabelsLayoutInfo.Builder()
        .breaks(getBreaks())
        //.bounds(applyLabelsOffset(bounds))
        .bounds(bounds)
        .smallFont(false)
        .overlap(false)
        .labelAdditionalOffsets(null)
        .labelHorizontalAnchor(TextLabel.HorizontalAnchor.MIDDLE)
        .labelVerticalAnchor(TextLabel.VerticalAnchor.TOP)
        .build();
  }
  */

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(labelNormalSize)
    }

    companion object {
        fun estimateBreakCountInitial(axisLength: Double): Int {
            return estimateBreakCount(INITIAL_TICK_LABEL_LENGTH, axisLength)
        }

        fun estimateBreakCount(labels: List<String>, axisLength: Double): Int {
            val maxLength = BreakLabelsLayoutUtil.maxLength(labels)
            return estimateBreakCount(maxLength, axisLength)
        }

        private fun estimateBreakCount(labelLength: Int, axisLength: Double): Int {
            val tickDistance = TICK_LABEL_SPEC.width(labelLength) + MIN_TICK_LABEL_DISTANCE
            return max(1.0, axisLength / tickDistance).toInt()
        }
    }
}
