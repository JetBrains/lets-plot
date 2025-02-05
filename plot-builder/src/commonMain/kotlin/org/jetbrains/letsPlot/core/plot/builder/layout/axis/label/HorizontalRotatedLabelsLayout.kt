/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.axis.label

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.GeometryUtil
import kotlin.math.*

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

        val maxLabelHeight = labelBoundsList.maxOf { it.height }

        val orientationSign = when (orientation) {
            Orientation.TOP -> -1.0
            Orientation.BOTTOM -> 1.0
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }

        val vjust = if (theme.labelVJust().isNaN()) {
            if (orientation == Orientation.TOP) 0.0 else 1.0
        } else {
            theme.labelVJust()
        }
        val hjust = theme.labelHJust()
        val angle = theme.labelAngle()

        val radAngle = toRadians(angle)
        val sinA = sin(radAngle)
        val cosA = cos(radAngle)
        val isVertical = abs(cosA) < 1e-6
        val isUpsideDown = cosA < 0
        val isHorizontal = abs(sinA) < 1e-6 && !isUpsideDown

        val isLabelDirectedFromTick = when (orientation) {
            Orientation.TOP -> sinA > 0
            Orientation.BOTTOM -> sinA < 0
            else -> throw IllegalStateException("Unsupported orientation $orientation")
        }

        val horizontalAnchor = when {
            isVertical -> hAnchorForVerticalLabels(vjust, angle)
            isUpsideDown -> Text.HorizontalAnchor.MIDDLE
            hjust == 0.0 && (isHorizontal || isLabelDirectedFromTick) -> Text.HorizontalAnchor.LEFT
            hjust == 1.0 && (isHorizontal || !isLabelDirectedFromTick) -> Text.HorizontalAnchor.RIGHT
            else -> Text.HorizontalAnchor.MIDDLE
        }

        val isCornerCase = !isHorizontal && horizontalAnchor != Text.HorizontalAnchor.MIDDLE

        val yBBoxOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            orientationSign * maxLabelHeight / 2 + (maxLabelHeight - rect.height) * (0.5 - vjust)
        }

        val xBBoxOffset: (DoubleRectangle) -> Double = { d: DoubleRectangle ->
            d.width * (0.5 - theme.labelHJust())
        }

        val verticalAnchor = when {
            isVertical -> vAnchorForVerticalLabels(hjust, angle)
            isHorizontal && vjust == 0.0 -> Text.VerticalAnchor.BOTTOM
            isHorizontal && vjust == 1.0 -> Text.VerticalAnchor.TOP
            isCornerCase && orientation == Orientation.BOTTOM -> Text.VerticalAnchor.TOP
            isCornerCase && orientation == Orientation.TOP -> Text.VerticalAnchor.BOTTOM
            else -> Text.VerticalAnchor.CENTER
        }

        val yOffsetSpecial = orientationSign * maxLabelHeight * yOffsetCoefficient(isLabelDirectedFromTick, horizontalAnchor)

        val yOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            when {
                isVertical && horizontalAnchor != Text.HorizontalAnchor.MIDDLE -> yOffsetSpecial
                isCornerCase -> orientationSign * (maxLabelHeight - rect.height) * (1 - vjust)
                isHorizontal && verticalAnchor == Text.VerticalAnchor.TOP -> (1.0 - orientationSign) * yBBoxOffset(rect)
                isHorizontal && verticalAnchor == Text.VerticalAnchor.BOTTOM -> (orientationSign + 1.0) * yBBoxOffset(rect)
                else -> yBBoxOffset(rect)
            }
        }

        val xOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            when {
                isVertical -> 0.0
                horizontalAnchor != Text.HorizontalAnchor.MIDDLE -> 0.0
                else -> xBBoxOffset(rect)
            }
        }

        val labelAdditionalOffsets = labelBoundsList.map {
            DoubleVector(xOffset(it), yOffset(it))
        }

        val adjustedLabelBoundsList = labelBoundsList.map {
            val origin =
                DoubleVector(xBBoxOffset(it) + it.origin.x, yBBoxOffset(it) + it.origin.y - orientationSign * it.height / 2)
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
            BreakLabelsLayoutUtil.horizontalCenteredLabelBounds(it.dimension)
        }
    }

    private fun vAnchorForVerticalLabels(hjust: Double, angle: Double): Text.VerticalAnchor {
        if (hjust != 0.0 && hjust != 1.0) {
            return Text.VerticalAnchor.CENTER
        }

        return when (angle) {
            90.0 -> if (hjust == 0.0) Text.VerticalAnchor.TOP else Text.VerticalAnchor.BOTTOM
            -90.0 -> if (hjust == 0.0) Text.VerticalAnchor.BOTTOM else Text.VerticalAnchor.TOP
            else -> Text.VerticalAnchor.CENTER
        }
    }

    private fun hAnchorForVerticalLabels(vjust: Double, angle: Double): Text.HorizontalAnchor {
        if (vjust != 0.0 && vjust != 1.0) {
            return Text.HorizontalAnchor.MIDDLE
        }

        return when (angle) {
            90.0 -> if (vjust == 1.0) Text.HorizontalAnchor.RIGHT else Text.HorizontalAnchor.LEFT
            -90.0 -> if (vjust == 1.0) Text.HorizontalAnchor.LEFT else Text.HorizontalAnchor.RIGHT
            else -> Text.HorizontalAnchor.MIDDLE
        }
    }

    private fun yOffsetCoefficient(isLabelDirectedFromTick: Boolean, horizontalAnchor: Text.HorizontalAnchor): Double =
        when {
            isLabelDirectedFromTick && horizontalAnchor == Text.HorizontalAnchor.RIGHT -> 1.0
            !isLabelDirectedFromTick && horizontalAnchor == Text.HorizontalAnchor.LEFT -> 1.0
            horizontalAnchor != Text.HorizontalAnchor.MIDDLE -> 0.0
            else -> 0.5
        }
}