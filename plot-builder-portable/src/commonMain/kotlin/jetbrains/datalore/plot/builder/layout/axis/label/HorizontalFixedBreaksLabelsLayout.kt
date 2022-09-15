/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class HorizontalFixedBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme
) : AbstractFixedBreaksLabelsLayout(
    orientation,
    axisDomain,
    labelSpec,
    breaks,
    theme
) {
    init {
        require(orientation.isHorizontal) { orientation.toString() }
    }

    private fun overlap(labelsInfo: AxisLabelsLayoutInfo, maxTickLabelsBounds: DoubleRectangle?): Boolean {
        return labelsInfo.isOverlap || maxTickLabelsBounds != null && !(maxTickLabelsBounds.xRange()
            .encloses(labelsInfo.bounds!!.xRange()) && maxTickLabelsBounds.yRange()
            .encloses(labelsInfo.bounds.yRange()))
    }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?,
        maxLabelsBounds: DoubleRectangle?
    ): AxisLabelsLayoutInfo {
        if (!theme.showLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        var labelsInfo = simpleLayout().doLayout(axisLength, axisMapper, maxLabelsBounds)
        if (overlap(labelsInfo, maxLabelsBounds)) {
            labelsInfo = multilineLayout().doLayout(axisLength, axisMapper, maxLabelsBounds)
            if (overlap(labelsInfo, maxLabelsBounds)) {
                labelsInfo = tiltedLayout().doLayout(axisLength, axisMapper, maxLabelsBounds)
                if (overlap(labelsInfo, maxLabelsBounds)) {
                    labelsInfo = verticalLayout(labelSpec).doLayout(axisLength, axisMapper, maxLabelsBounds)
                }
            }
        }
        return labelsInfo
    }

    private fun simpleLayout(): AxisLabelsLayout {
        return HorizontalSimpleLabelsLayout(
            orientation,
            axisDomain,
            labelSpec,
            breaks,
            theme
        )
    }

    private fun multilineLayout(): AxisLabelsLayout {
        return HorizontalMultilineLabelsLayout(
            orientation,
            axisDomain,
            labelSpec,
            breaks,
            theme,
            2
        )
    }

    private fun tiltedLayout(): AxisLabelsLayout {
        return HorizontalTiltedLabelsLayout(
            orientation,
            axisDomain,
            labelSpec,
            breaks,
            theme
        )
    }

    private fun verticalLayout(labelSpec: LabelSpec): AxisLabelsLayout {
        return HorizontalVerticalLabelsLayout(
            orientation,
            axisDomain,
            labelSpec,
            breaks,
            theme
        )
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        throw IllegalStateException("Not implemented here")
    }
}
