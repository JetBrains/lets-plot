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

internal class HorizontalFixedBreaksLabelsLayout constructor(
    orientation: Orientation,
    breaks: ScaleBreaks,
    private val geomAreaInsets: Insets,
    theme: AxisTheme,
    private val polar: Boolean
) : AbstractFixedBreaksLabelsLayout(
    orientation,
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
        axisDomain: DoubleSpan,
        axisLength: Double,
    ): AxisLabelsLayoutInfo {
        if (!theme.showLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        val axisSpanExpanded = DoubleSpan(
            lower = -axisLeftExpand,
            upper = axisLength + axisRightExpand
        )

        if (theme.rotateLabels()) {
            return rotatedLayout(theme.labelAngle()).doLayout(axisDomain, axisLength)
        }

        // Don't run this expensive code when num of breaks is too large.
        val labelsLayoutInfo = if (breaks.size > 400) {
            // Don't even try variants when num of breaks is too large (optimization, see issue #932).
            verticalLayout().doLayout(axisDomain, axisLength)
        } else {
            if (!polar) {
                var layoutInfo = simpleLayout().doLayout(axisDomain, axisLength)
                if (overlap(layoutInfo, axisSpanExpanded)) {
                    layoutInfo = multilineLayout().doLayout(axisDomain, axisLength)
                    if (overlap(layoutInfo, axisSpanExpanded)) {
                        layoutInfo = tiltedLayout().doLayout(axisDomain, axisLength)
                        if (overlap(layoutInfo, axisSpanExpanded)) {
                            layoutInfo = verticalLayout().doLayout(axisDomain, axisLength)
                        }
                    }
                }
                layoutInfo
            } else {
                simpleLayout().doLayout(axisDomain, axisLength)
            }
        }
        return labelsLayoutInfo
    }

    override fun withScaleBreaks(breaks: ScaleBreaks): AxisLabelsLayout {
        return HorizontalFixedBreaksLabelsLayout(
            orientation,
            breaks,
            geomAreaInsets,
            theme,
            polar
        )
    }

    private fun simpleLayout(): AxisLabelsLayout {
        return HorizontalSimpleLabelsLayout(
            orientation,
            breaks,
            theme
        )
    }

    private fun multilineLayout(): AxisLabelsLayout {
        return HorizontalMultilineLabelsLayout(
            orientation,
            breaks,
            theme,
            2
        )
    }

    private fun tiltedLayout(): AxisLabelsLayout {
        return HorizontalTiltedLabelsLayout(
            orientation,
            breaks,
            theme
        )
    }

    private fun rotatedLayout(angle: Double): AxisLabelsLayout {
        return HorizontalRotatedLabelsLayout(
            orientation,
            breaks,
            theme,
            angle
        )
    }

    private fun verticalLayout(): AxisLabelsLayout {
        return HorizontalVerticalLabelsLayout(
            orientation,
            breaks,
            theme
        )
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        throw IllegalStateException("Not implemented here")
    }
}
