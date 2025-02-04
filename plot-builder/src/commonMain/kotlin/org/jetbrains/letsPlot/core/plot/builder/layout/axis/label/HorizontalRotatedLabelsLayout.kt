/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.GeometryUtil

internal class HorizontalRotatedLabelsLayout(
    orientation: Orientation,
    breaks: ScaleBreaks,
    theme: AxisTheme,
    private val myRotationAngle: Double
) : AbstractFixedBreaksLabelsLayout(
    orientation,
    breaks,
    theme
) {

    override fun doLayout(
        axisDomain: DoubleSpan,
        axisLength: Double,
    ): AxisLabelsLayoutInfo {
        if (breaks.isEmpty) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        if (!theme.showLabels()) {
            return noLabelsLayoutInfo(axisLength, orientation)
        }

        val ticks = breaks.projectOnAxis(axisDomain, axisLength, isHorizontal = true)
        val labelBoundsList = labelBoundsList(ticks, breaks.labels, HORIZONTAL_TICK_LOCATION)

        var overlap = false
        val bounds = labelBoundsList.fold(null) { acc: DoubleRectangle?, b ->
            overlap = overlap || acc != null && acc.xRange().connected(
                b.xRange().expanded(MIN_TICK_LABEL_DISTANCE / 2)
            )
            GeometryUtil.union(b, acc)
        }!! // labels are not empty so bounds can't be null

        // add additional offsets for labels
        val yOffset: (DoubleRectangle) -> Double = when (orientation) {
            Orientation.TOP -> { d: DoubleRectangle -> -d.height / 2 }
            Orientation.BOTTOM -> { d: DoubleRectangle -> d.height / 2 }
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }
        val labelAdditionalOffsets = labelBoundsList.map { DoubleVector(0.0, yOffset(it)) }

        return createAxisLabelsLayoutInfoBuilder(bounds, overlap)
            .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            .labelVerticalAnchor(Text.VerticalAnchor.CENTER)
            .labelRotationAngle(-myRotationAngle)
            .hJust(theme.labelHJust())
            .vJust(theme.labelVJust())
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(labelBoundsList.map(::alignToLabelMargin))  // for debug drawing
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(it.dimension)
        }
    }
}