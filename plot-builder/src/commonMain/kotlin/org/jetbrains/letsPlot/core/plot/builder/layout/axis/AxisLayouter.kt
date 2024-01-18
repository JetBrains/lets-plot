/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
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

internal class AxisLayouter(
    val orientation: Orientation,
    private val domainRange: DoubleSpan,
    private val labelsLayout: AxisLabelsLayout
) {

    fun doLayout(
        axisLength: Double
    ): AxisLayoutInfo {
        val labelsInfo = labelsLayout.doLayout(axisLength, toAxisMapper(axisLength))
        val axisBreaks = labelsInfo.breaks!!
        val labelsBounds = labelsInfo.bounds!!
        return AxisLayoutInfo(
            axisLength = axisLength,
            axisDomain = domainRange,
            orientation = orientation,
            axisBreaks = axisBreaks,
            tickLabelsBounds = labelsBounds,
            tickLabelRotationAngle = labelsInfo.labelRotationAngle,
            tickLabelHorizontalAnchor = labelsInfo.labelHorizontalAnchor,
            tickLabelVerticalAnchor = labelsInfo.labelVerticalAnchor,
            tickLabelAdditionalOffsets = labelsInfo.labelAdditionalOffsets,
            tickLabelsTextBounds = BreakLabelsLayoutUtil.textBounds(
                labelsBounds,
                labelsLayout.theme.tickLabelMargins(),
                orientation
            ),
            tickLabelBoundsList = labelsInfo.labelBoundsList
        )
    }

    private fun toAxisMapper(axisLength: Double): (Double?) -> Double? {
        // Do reverse maping for vertical axis: screen coordinates: top->bottom, but y-axis coordinate: bottom->top
        val reverse = !orientation.isHorizontal
        val scaleMapper = toScaleMapper(axisLength, reverse)
        return { v -> scaleMapper(v) }
    }

    protected fun toScaleMapper(axisLength: Double, reverse: Boolean): ScaleMapper<Double> {
        return Mappers.linear(
            domain = domainRange,
            range = DoubleSpan(0.0, axisLength),
            reverse = reverse
        )
    }

    companion object {
        fun create(
            orientation: Orientation,
            axisDomain: DoubleSpan,
            breaksProvider: AxisBreaksProvider,
            geomAreaInsets: Insets,
            theme: AxisTheme
        ): AxisLayouter {
            val labelsLayout =
                if (breaksProvider.isFixedBreaks) {
                    if (orientation.isHorizontal) {
                        horizontalFixedBreaks(
                            orientation,
                            breaksProvider.fixedBreaks,
                            geomAreaInsets,
                            theme
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
                axisDomain,
                labelsLayout
            )
        }
    }
}
