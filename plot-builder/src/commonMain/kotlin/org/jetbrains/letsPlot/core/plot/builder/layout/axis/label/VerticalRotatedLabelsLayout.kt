/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.GeometryUtil
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class VerticalRotatedLabelsLayout(
    orientation: Orientation,
    breaks: ScaleBreaks,
    theme: AxisTheme,
) : AbstractFixedBreaksLabelsLayout(
    orientation,
    breaks,
    theme
) {
    private val myRotationAngle = theme.labelAngle().takeIf { !it.isNaN() } ?: 0.0

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
            DoubleRectangle.ZERO

        val maxLabelWidth = labelBoundsList.maxOfOrNull { it.width } ?: 0.0

        val orientationSign = when (orientation) {
            Orientation.LEFT -> -1.0
            Orientation.RIGHT -> 1.0
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }

        val radAngle = toRadians(myRotationAngle)
        val sinA = sin(radAngle)
        val cosA = cos(radAngle)
        val isVertical = abs(cosA) < 1e-6
        val isUpsideDown = cosA < 0
        val isHorizontal = abs(sinA) < 1e-6 && !isUpsideDown
        val isLabelDirectedFromTick = when (orientation) {
            Orientation.LEFT -> sinA > 0
            Orientation.RIGHT -> sinA < 0
        }

        val flushHjust = if (orientation == Orientation.LEFT) 1.0 else 0.0
        val hJust = if (theme.labelHJust().isNaN()) flushHjust else theme.labelHJust()

        val vJust = if (theme.labelVJust().isNaN()) {
            when {
                isHorizontal || isVertical -> 0.5
                isLabelDirectedFromTick -> 0.0
                else -> 1.0
            }
        } else {
            theme.labelVJust()
        }

        val verticalAnchor = when {
            isVertical || isUpsideDown -> Text.VerticalAnchor.CENTER
            isHorizontal && vJust == 0.0 -> Text.VerticalAnchor.TOP
            isHorizontal && vJust == 1.0 -> Text.VerticalAnchor.BOTTOM
            vJust == 0.0 && isLabelDirectedFromTick -> Text.VerticalAnchor.BOTTOM
            vJust == 1.0 && !isLabelDirectedFromTick -> Text.VerticalAnchor.TOP
            else -> Text.VerticalAnchor.CENTER
        }

        val isCornerCase = !isHorizontal && verticalAnchor != Text.VerticalAnchor.CENTER

        val horizontalAnchor = when {
            isVertical -> hAnchorForVerticalLabels(vJust, sinA)
            isHorizontal && hJust == 0.0 -> Text.HorizontalAnchor.LEFT
            isHorizontal && hJust == 1.0 -> Text.HorizontalAnchor.RIGHT
            isCornerCase && orientation == Orientation.LEFT -> Text.HorizontalAnchor.RIGHT
            isCornerCase && orientation == Orientation.RIGHT -> Text.HorizontalAnchor.LEFT
            else -> Text.HorizontalAnchor.MIDDLE
        }

        val slotCenterX = orientationSign * maxLabelWidth / 2
        val xBBoxOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            slotCenterX + (maxLabelWidth - rect.width) * (hJust - 0.5)
        }

        val yBBoxOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            rect.height * (0.5 - vJust)
        }

        val leftAnchorX = slotCenterX - maxLabelWidth / 2
        val rightAnchorX = slotCenterX + maxLabelWidth / 2

        val xOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            when {
                isCornerCase -> (maxLabelWidth - rect.width) * (hJust - flushHjust)
                isHorizontal && horizontalAnchor == Text.HorizontalAnchor.LEFT -> leftAnchorX
                isHorizontal && horizontalAnchor == Text.HorizontalAnchor.RIGHT -> rightAnchorX
                else -> xBBoxOffset(rect)
            }
        }

        val yOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            when {
                isVertical -> 0.0
                verticalAnchor != Text.VerticalAnchor.CENTER -> 0.0
                else -> yBBoxOffset(rect)
            }
        }

        val labelAdditionalOffsets = labelBoundsList.map {
            DoubleVector(xOffset(it), yOffset(it))
        }

        val renderedLabelBounds: (DoubleRectangle, DoubleVector) -> DoubleRectangle = { rect, offset ->
            val w = rect.width
            val h = rect.height
            val centerShiftY = when {
                isVertical -> when (horizontalAnchor) {
                    Text.HorizontalAnchor.LEFT -> if (sinA > 0) -h / 2 else h / 2
                    Text.HorizontalAnchor.RIGHT -> if (sinA > 0) h / 2 else -h / 2
                    else -> 0.0
                }
                verticalAnchor == Text.VerticalAnchor.TOP -> h / 2
                verticalAnchor == Text.VerticalAnchor.BOTTOM -> -h / 2
                else -> 0.0
            }
            val centerShiftX = when {
                isVertical -> 0.0
                horizontalAnchor == Text.HorizontalAnchor.LEFT -> w / 2
                horizontalAnchor == Text.HorizontalAnchor.RIGHT -> -w / 2
                else -> 0.0
            }
            val centerX = rect.origin.x + offset.x + centerShiftX
            val centerY = rect.origin.y + offset.y + centerShiftY
            DoubleRectangle(centerX - w / 2, centerY - h / 2, w, h)
        }

        val adjustedLabelBoundsList = labelBoundsList.mapIndexed { i, rect ->
            renderedLabelBounds(rect, labelAdditionalOffsets[i])
        }

        return createAxisLabelsLayoutInfoBuilder(bounds, overlap)
            .labelHorizontalAnchor(horizontalAnchor)
            .labelVerticalAnchor(verticalAnchor)
            .labelRotationAngle(-myRotationAngle)
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(adjustedLabelBoundsList.map(::alignToLabelMargin))
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            DoubleRectangle(0.0, 0.0, it.width, it.height)
        }
    }

    private fun hAnchorForVerticalLabels(vjust: Double, sinA: Double): Text.HorizontalAnchor {
        if (vjust != 0.0 && vjust != 1.0) {
            return Text.HorizontalAnchor.MIDDLE
        }
        return when {
            sinA > 0.0 -> if (vjust == 1.0) Text.HorizontalAnchor.LEFT else Text.HorizontalAnchor.RIGHT
            sinA < 0.0 -> if (vjust == 1.0) Text.HorizontalAnchor.RIGHT else Text.HorizontalAnchor.LEFT
            else -> Text.HorizontalAnchor.MIDDLE
        }
    }
}
