package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.visualization.plot.base.CoordinateSystem
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.scale.ScaleUtil
import jetbrains.datalore.visualization.plot.builder.guide.AxisComponent
import jetbrains.datalore.visualization.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.visualization.plot.builder.theme.AxisTheme

object AxisUtil {
    fun applyLayoutInfo(axis: AxisComponent, info: AxisLayoutInfo) {
        axis.tickLabelOffsets.set(info.tickLabelAdditionalOffsets)
        axis.tickLabelRotationDegree.set(info.tickLabelRotationAngle)
        if (info.tickLabelHorizontalAnchor != null) {
            axis.tickLabelHorizontalAnchor.set(info.tickLabelHorizontalAnchor)
        }
        if (info.tickLabelVerticalAnchor != null) {
            axis.tickLabelVerticalAnchor.set(info.tickLabelVerticalAnchor)
        }
        axis.tickLabelSmallFont.set(info.tickLabelSmallFont)
    }

    fun applyTheme(axis: AxisComponent, theme: AxisTheme) {
        axis.tickLabelsEnabled().set(theme.showTickLabels())
        axis.tickMarksEnabled().set(theme.showTickMarks())
        axis.axisLineEnabled().set(theme.showLine())

        axis.lineWidth.set(theme.lineWidth())
        axis.tickMarkLength.set(theme.tickMarkLength())
        axis.tickMarkPadding.set(theme.tickMarkPadding())
        axis.tickMarkWidth.set(theme.tickMarkWidth())
    }

    fun setBreaks(axis: AxisComponent, scale: Scale<Double>, coord: CoordinateSystem, isHorizontal: Boolean) {
        axis.breaks.set(ScaleUtil.axisBreaks(scale, coord, isHorizontal))
        axis.labels.set(ScaleUtil.labels(scale))
    }
}
