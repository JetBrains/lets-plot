/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.LayoutConstants.H_AXIS_LABELS_EXPAND
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets
import kotlin.math.max

internal class HorizontalFixedBreaksLabelsLayout(
    orientation: Orientation,
    axisDomain: DoubleSpan,
    breaks: ScaleBreaks,
    geomAreaInsets: Insets,
    theme: AxisTheme
) : AbstractFixedBreaksLabelsLayout(
    orientation,
    axisDomain,
    breaks,
    theme
) {

    private val axisLeftExpand = max(geomAreaInsets.left, H_AXIS_LABELS_EXPAND)
    private val axisRightExpand = max(geomAreaInsets.right, H_AXIS_LABELS_EXPAND)

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

//        val axisSpanExpanded = DoubleSpan(0.0, axisLength)
//            .expanded(H_AXIS_LABELS_EXPAND)
        val axisSpanExpanded = DoubleSpan(
            lower = -axisLeftExpand,
            upper = axisLength + axisRightExpand
        )

        if (theme.rotateLabels()) {
            return rotatedLayout(theme.labelAngle()).doLayout(axisLength, axisMapper)
        }

        var labelsInfo = simpleLayout().doLayout(axisLength, axisMapper)
        if (overlap(labelsInfo, axisSpanExpanded)) {
            labelsInfo = multilineLayout().doLayout(axisLength, axisMapper)
            if (overlap(labelsInfo, axisSpanExpanded)) {
                labelsInfo = tiltedLayout().doLayout(axisLength, axisMapper)
                if (overlap(labelsInfo, axisSpanExpanded)) {
//                    println("Overlap")
                    labelsInfo = verticalLayout().doLayout(axisLength, axisMapper)
                }
            }
        }
        return labelsInfo
    }

    private fun simpleLayout(): AxisLabelsLayout {
        return HorizontalSimpleLabelsLayout(
            orientation,
            axisDomain,
            breaks,
            theme
        )
    }

    private fun multilineLayout(): AxisLabelsLayout {
        return HorizontalMultilineLabelsLayout(
            orientation,
            axisDomain,
            breaks,
            theme,
            2
        )
    }

    private fun tiltedLayout(): AxisLabelsLayout {
        return HorizontalTiltedLabelsLayout(
            orientation,
            axisDomain,
            breaks,
            theme
        )
    }

    private fun rotatedLayout(angle: Double): AxisLabelsLayout {
        return HorizontalRotatedLabelsLayout(
            orientation,
            axisDomain,
            breaks,
            theme,
            angle
        )
    }

    private fun verticalLayout(): AxisLabelsLayout {
        return HorizontalVerticalLabelsLayout(
            orientation,
            axisDomain,
            breaks,
            theme
        )
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        throw IllegalStateException("Not implemented here")
    }
}
