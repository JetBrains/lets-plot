/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.axis.label

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.scale.ScaleBreaks
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.GeometryUtil
import jetbrains.datalore.plot.builder.presentation.LabelSpec
import jetbrains.datalore.plot.builder.theme.AxisTheme
import kotlin.math.max

internal class HorizontalSimpleLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    labelSpec: LabelSpec,
    breaks: ScaleBreaks,
    theme: AxisTheme
) : AbstractFixedBreaksLabelsLayout(orientation, axisDomain, labelSpec, breaks, theme) {

    override fun doLayout(
        axisLength: Double,
        axisMapper: (Double?) -> Double?
    ): AxisLabelsLayoutInfo {

        if (breaks.isEmpty) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        if (!theme.showLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        var bounds: DoubleRectangle? = null
        var overlap = false
        val ticks = mapToAxis(breaks.transformedValues, axisMapper)

        val boundsList = labelBoundsList(
            ticks, breaks.labels,
            HORIZONTAL_TICK_LOCATION
        )
        for (labelBounds in boundsList) {
            overlap = overlap || bounds != null && bounds.xRange().connected(
//                SeriesUtil.expand(labelBounds.xRange(), MIN_TICK_LABEL_DISTANCE / 2, MIN_TICK_LABEL_DISTANCE / 2.0)
                labelBounds.xRange().expanded(MIN_TICK_LABEL_DISTANCE / 2)
            )
            bounds = GeometryUtil.union(labelBounds, bounds)
        }

        val verticalAnchor = when (orientation) {
            Orientation.BOTTOM -> Text.VerticalAnchor.TOP
            else -> Text.VerticalAnchor.BOTTOM
        }
        return createAxisLabelsLayoutInfoBuilder(bounds!!, overlap)
            .labelAdditionalOffsets(null)
            .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            .labelVerticalAnchor(verticalAnchor)
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(
            labelNormalSize
        )
    }

    companion object {
        fun estimateBreakCountInitial(axisLength: Double, tickLabelSpec: LabelSpec): Int {
            return estimateBreakCount(
                tickLabelSpec.width(INITIAL_TICK_LABEL),
                axisLength
            )
        }

        fun estimateBreakCount(labels: List<String>, axisLength: Double, tickLabelSpec: LabelSpec): Int {
            val longestLabelWidth = BreakLabelsLayoutUtil.longestLabelWidth(labels) { tickLabelSpec.width(it) }
            return estimateBreakCount(
                longestLabelWidth,
                axisLength
            )
        }

        private fun estimateBreakCount(width: Double, axisLength: Double): Int {
            val tickDistance = width + MIN_TICK_LABEL_DISTANCE
            return max(1.0, axisLength / tickDistance).toInt()
        }
    }
}
