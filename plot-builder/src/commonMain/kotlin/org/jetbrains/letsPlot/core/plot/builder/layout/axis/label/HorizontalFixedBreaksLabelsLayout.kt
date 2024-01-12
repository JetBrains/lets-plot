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

        val axisSpanExpanded = DoubleSpan(
            lower = -axisLeftExpand,
            upper = axisLength + axisRightExpand
        )

        if (theme.rotateLabels()) {
            return rotatedLayout(theme.labelAngle()).doLayout(axisLength, axisMapper)
        }

        // Don't run this expensive code when num of breaks is too large.
        val labelsLayoutInfo = if (breaks.size > 400) {
            // Don't even try variants when num of breaks is too large (optimization, see issue #932).
            verticalLayout().doLayout(axisLength, axisMapper)
        } else {
            var layoutInfo = simpleLayout().doLayout(axisLength, axisMapper)
            if (overlap(layoutInfo, axisSpanExpanded)) {
                layoutInfo = multilineLayout().doLayout(axisLength, axisMapper)
                if (overlap(layoutInfo, axisSpanExpanded)) {
                    layoutInfo = tiltedLayout().doLayout(axisLength, axisMapper)
                    if (overlap(layoutInfo, axisSpanExpanded)) {
                        layoutInfo = verticalLayout().doLayout(axisLength, axisMapper)
                    }
                }
            }
            layoutInfo
        }
        return labelsLayoutInfo
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
