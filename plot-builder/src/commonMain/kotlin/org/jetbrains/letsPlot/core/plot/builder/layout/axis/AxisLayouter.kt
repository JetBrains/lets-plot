/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.AxisLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.label.AxisLabelsLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.label.AxisLabelsLayout.Companion.horizontalFixedBreaks
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.label.AxisLabelsLayout.Companion.horizontalFlexBreaks
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.label.AxisLabelsLayout.Companion.verticalFixedBreaks
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.label.AxisLabelsLayout.Companion.verticalFlexBreaks
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.label.BreakLabelsLayoutUtil
import org.jetbrains.letsPlot.core.plot.builder.layout.util.Insets

internal class AxisLayouter private constructor(
    val orientation: Orientation,
    private val labelsLayout: AxisLabelsLayout
) {

    fun doLayout(axisDomain: DoubleSpan, axisLength: Double): AxisLayoutInfo {

        val labelsLayout = labelsLayout.filterBreaks(axisDomain)
        val labelsInfo = labelsLayout.doLayout(axisDomain, axisLength)
        val axisBreaks = labelsInfo.breaks!!
        val labelsBounds = labelsInfo.bounds!!
        return AxisLayoutInfo(
            axisLength = axisLength,
            axisDomain = axisDomain, //domainRange,
            orientation = orientation,
            axisBreaks = axisBreaks,
            tickLabelsBounds = labelsBounds,
            tickLabelRotationAngle = labelsInfo.labelRotationAngle,
            tickLabelHorizontalAnchor = labelsInfo.labelHorizontalAnchor,
            tickLabelVerticalAnchor = labelsInfo.labelVerticalAnchor,
            tickLabelHJust = labelsInfo.labelHJust,
            tickLabelVJust = labelsInfo.labelVJust,
            tickLabelAdditionalOffsets = labelsInfo.labelAdditionalOffsets,
            tickLabelsTextBounds = BreakLabelsLayoutUtil.textBounds(
                labelsBounds,
                labelsLayout.theme.tickLabelMargins(),
                orientation
            ),
            tickLabelBoundsList = labelsInfo.labelBoundsList
        )
    }

    companion object {
        fun create(
            orientation: Orientation,
            breaksProvider: AxisBreaksProvider,
            geomAreaInsets: Insets,
            theme: AxisTheme,
            polar: Boolean
        ): AxisLayouter {
            val labelsLayout =
                if (breaksProvider.isFixedBreaks) {
                    if (orientation.isHorizontal) {
                        horizontalFixedBreaks(
                            orientation,
                            breaksProvider.fixedBreaks,
                            geomAreaInsets,
                            theme,
                            polar
                        )
                    } else {
                        verticalFixedBreaks(
                            orientation,
                            breaksProvider.fixedBreaks, theme
                        )
                    }
                } else {
                    if (orientation.isHorizontal) {
                        horizontalFlexBreaks(
                            orientation,
                            breaksProvider, theme
                        )
                    } else {
                        verticalFlexBreaks(
                            orientation,
                            breaksProvider, theme
                        )
                    }
                }

            return AxisLayouter(
                orientation,
                labelsLayout
            )
        }
    }
}
