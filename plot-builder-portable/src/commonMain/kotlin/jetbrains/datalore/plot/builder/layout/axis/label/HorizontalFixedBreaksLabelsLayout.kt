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
import jetbrains.datalore.plot.builder.layout.LayoutConstants.H_AXIS_LABELS_EXPAND
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

    private fun overlap(labelsInfo: AxisLabelsLayoutInfo, axisSpanExpanded: DoubleSpan): Boolean {
        return labelsInfo.isOverlap || !axisSpanExpanded.encloses(labelsInfo.bounds!!.xRange())
    }

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {
        if (!theme.showLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        val axisSpanExpanded = DoubleSpan(
            lower = -H_AXIS_LABELS_EXPAND,
            upper = axisLength + H_AXIS_LABELS_EXPAND
        )

        var labelsInfo = simpleLayout().doLayout(axisLength, axisMapper)
        if (overlap(labelsInfo, axisSpanExpanded)) {
            labelsInfo = multilineLayout().doLayout(axisLength, axisMapper)
            if (overlap(labelsInfo, axisSpanExpanded)) {
                labelsInfo = tiltedLayout().doLayout(axisLength, axisMapper)
                if (overlap(labelsInfo, axisSpanExpanded)) {
                    labelsInfo = verticalLayout(labelSpec).doLayout(axisLength, axisMapper)
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
