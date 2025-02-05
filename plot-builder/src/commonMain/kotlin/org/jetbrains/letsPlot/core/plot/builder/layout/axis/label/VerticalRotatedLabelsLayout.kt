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
	    
        val maxLabelWidth = labelBoundsList.maxOf { it.width }

        val orientationSign = when (orientation) {
            Orientation.LEFT -> -1.0
            Orientation.RIGHT -> 1.0
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }

        val vjust = theme.labelVJust()
        val hjust = if (theme.labelHJust().isNaN()) {
            if (orientation == Orientation.RIGHT) 0.0 else 1.0
        } else {
            theme.labelHJust()
        }

        val xBBoxOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            orientationSign * (maxLabelWidth - rect.width) * (1.0 - hjust)
        }

        val yBBoxOffset: (DoubleRectangle) -> Double = { d: DoubleRectangle ->
            d.height * (-vjust)
        }

        val xOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            ((maxLabelWidth - rect.width) * (1 - hjust) + rect.width / 2) * orientationSign
        }

        val yOffset: (DoubleRectangle) -> Double = { d: DoubleRectangle ->
            d.height * (0.5 - vjust)
        }

        val labelAdditionalOffsets = labelBoundsList.map {
            DoubleVector(xOffset(it), yOffset(it))
        }

        val horizontalAnchor = Text.HorizontalAnchor.MIDDLE
        val verticalAnchor = Text.VerticalAnchor.CENTER

        val adjustedLabelBoundsList = labelBoundsList.map {
            val origin = DoubleVector( it.origin.x + xBBoxOffset(it), yBBoxOffset(it) + it.origin.y)
            DoubleRectangle(origin, it.dimension)
        }

        return createAxisLabelsLayoutInfoBuilder(bounds, overlap)
            .labelHorizontalAnchor(horizontalAnchor)
            .labelVerticalAnchor(verticalAnchor)
            .labelRotationAngle(-myRotationAngle)
            .hJust(theme.labelHJust())
            .vJust(theme.labelVJust())
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(adjustedLabelBoundsList.map(::alignToLabelMargin)) // for debug drawing
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            DoubleRectangle(0.0, 0.0, it.width, it.height)
        }
    }
}