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

internal class VerticalRotatedLabelsLayout(
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
        check(!orientation.isHorizontal)

        val ticks = breaks.projectOnAxis(axisDomain, axisLength, isHorizontal = false)
        val labelBoundsList = labelBoundsList(ticks, breaks.labels) { y: Double -> DoubleVector(0.0, y) }

        // total bounds
        var overlap = false
        val bounds = labelBoundsList.fold(null) { acc: DoubleRectangle?, b ->
            overlap = overlap || acc != null && acc.yRange().connected(
                b.yRange().expanded(MIN_TICK_LABEL_DISTANCE / 2)
            )
            GeometryUtil.union(b, acc)
        }
            ?: // labels can be empty so bounds may be null, it is safe to use empty rect
            DoubleRectangle(DoubleVector.ZERO, DoubleVector.ZERO)

        // add additional offsets for labels
        val xOffset: (DoubleRectangle) -> Double = when (orientation) {
            Orientation.LEFT -> { d: DoubleRectangle -> -d.width / 2 }
            Orientation.RIGHT -> { d: DoubleRectangle -> d.width / 2 }
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }
        val labelAdditionalOffsets = labelBoundsList.map { DoubleVector(xOffset(it), 0.0) }

        return createAxisLabelsLayoutInfoBuilder(bounds, overlap)
            .labelHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            .labelVerticalAnchor(Text.VerticalAnchor.CENTER)
            .labelRotationAngle(-myRotationAngle)
            .hJust(theme.labelHJust())
            .vJust(theme.labelVJust())
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(labelBoundsList.map(::alignToLabelMargin)) // for debug drawing
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            // make vertical centered
            DoubleRectangle(0.0, -it.height / 2, it.width, it.height)
        }
    }
}