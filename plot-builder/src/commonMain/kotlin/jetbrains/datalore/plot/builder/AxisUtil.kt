/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.ScaleUtil
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.theme.AxisTheme

object AxisUtil {
    fun applyLayoutInfo(axis: jetbrains.datalore.plot.builder.guide.AxisComponent, info: AxisLayoutInfo) {
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

    fun applyTheme(axis: jetbrains.datalore.plot.builder.guide.AxisComponent, theme: AxisTheme) {
        axis.tickLabelsEnabled().set(theme.showTickLabels())
        axis.tickMarksEnabled().set(theme.showTickMarks())
        axis.axisLineEnabled().set(theme.showLine())

        axis.lineWidth.set(theme.lineWidth())
        axis.tickMarkLength.set(theme.tickMarkLength())
        axis.tickMarkPadding.set(theme.tickMarkPadding())
        axis.tickMarkWidth.set(theme.tickMarkWidth())
    }

    fun setBreaks(axis: jetbrains.datalore.plot.builder.guide.AxisComponent, scale: Scale<Double>, coord: CoordinateSystem, isHorizontal: Boolean) {
        axis.breaks.set(ScaleUtil.axisBreaks(scale, coord, isHorizontal))
        axis.labels.set(ScaleUtil.labels(scale))
    }
}
