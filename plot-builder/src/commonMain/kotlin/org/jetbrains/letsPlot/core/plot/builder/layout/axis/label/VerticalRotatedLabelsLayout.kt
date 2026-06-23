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

/**
 * Lays out rotated tick labels for a vertical axis (LEFT / RIGHT).
 *
 * This mirrors [HorizontalRotatedLabelsLayout]. The guiding idea is the same: text alignment in SVG is
 * controlled by coarse, discrete anchors (`text-anchor` for screen-x; `dy` for screen-y), while `hjust`/`vjust`
 * are continuous. The anchor snaps to the *actually rendered* glyph extent, so it is exact, whereas any pixel
 * offset we compute relies on the *estimated* label width which overestimates. Therefore, whenever `hjust`/`vjust`
 * land exactly on 0/0.5/1 and the geometry is favorable, we use the exact anchor; otherwise we fall back to
 * MIDDLE/CENTER plus an estimated-bbox offset.
 *
 * `hjust` always controls the screen-x position and `vjust` the screen-y position (same as the horizontal axis).
 * The only structural differences from the horizontal axis are:
 *  - the shared "slot" (sized to the widest label) is on screen-x here (across the vertical axis), not screen-y;
 *  - `vjust` centers a label on its tick along screen-y (no slot), the role `hjust` plays on the horizontal axis;
 *  - the defaults differ (a vertical axis defaults to "flush to the axis").
 */
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
        // 'orientationSign' above already threw for non-vertical orientations, so 'orientation' is
        // narrowed to LEFT/RIGHT here and this 'when' is exhaustive without an 'else' branch.
        val isLabelDirectedFromTick = when (orientation) {
            Orientation.LEFT -> sinA > 0
            Orientation.RIGHT -> sinA < 0
        }

        // hjust drives the screen-x position (across the axis). Default: flush to the axis.
        val hJust = if (theme.labelHJust().isNaN()) {
            if (orientation == Orientation.LEFT) 1.0 else 0.0
        } else {
            theme.labelHJust()
        }

        // vjust drives the screen-y position (along the axis). Default: centered on the tick, except a
        // tilted label hangs from the tick by its near end (mirrors hjust's role on the horizontal axis).
        val vJust = if (theme.labelVJust().isNaN()) {
            when {
                isHorizontal || isVertical -> 0.5
                isLabelDirectedFromTick -> 0.0
                else -> 1.0
            }
        } else {
            theme.labelVJust()
        }

        // --- screen-y anchor (vjust): along the axis. This is the "primary" anchor, mirroring the
        // horizontal axis's horizontalAnchor; for tilted labels its non-CENTER value marks the corner case. ---
        val verticalAnchor = when {
            isVertical -> vAnchorForVerticalLabels(hJust)
            isUpsideDown -> Text.VerticalAnchor.CENTER
            vJust == 0.0 && (isHorizontal || isLabelDirectedFromTick) -> Text.VerticalAnchor.BOTTOM
            vJust == 1.0 && (isHorizontal || !isLabelDirectedFromTick) -> Text.VerticalAnchor.TOP
            else -> Text.VerticalAnchor.CENTER
        }

        val isCornerCase = !isHorizontal && verticalAnchor != Text.VerticalAnchor.CENTER

        // --- screen-x anchor (hjust): across the axis, within the shared max-width slot.
        // Mirrors the horizontal axis's verticalAnchor; the corner case pins the inner (toward-axis) edge. ---
        val horizontalAnchor = when {
            isVertical -> hAnchorForVerticalLabels(vJust)
            isHorizontal && hJust == 0.0 -> Text.HorizontalAnchor.LEFT
            isHorizontal && hJust == 1.0 -> Text.HorizontalAnchor.RIGHT
            isCornerCase && orientation == Orientation.LEFT -> Text.HorizontalAnchor.RIGHT
            isCornerCase && orientation == Orientation.RIGHT -> Text.HorizontalAnchor.LEFT
            else -> Text.HorizontalAnchor.MIDDLE
        }

        // Slot center, relative to the base offset, in screen-x (across the axis).
        val slotCenterX = orientationSign * maxLabelWidth / 2

        // bbox-centered screen-x offset (used for in-between hjust / corner cases)
        val xBBoxOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            slotCenterX + (maxLabelWidth - rect.width) * (0.5 - hJust)
        }

        // bbox-centered screen-y offset (used for in-between vjust)
        val yBBoxOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            rect.height * (0.5 - vJust)
        }

        val xOffsetSpecial =
            orientationSign * maxLabelWidth * xOffsetCoefficient(isLabelDirectedFromTick, verticalAnchor)

        val xOffset: (DoubleRectangle) -> Double = { rect: DoubleRectangle ->
            when {
                isVertical && verticalAnchor != Text.VerticalAnchor.CENTER -> xOffsetSpecial
                isCornerCase -> (maxLabelWidth - rect.width) * ((orientationSign + 1) / 2 - hJust)
                // Exact slot-edge alignment (independent of estimated width):
                isHorizontal && horizontalAnchor == Text.HorizontalAnchor.LEFT -> slotCenterX - maxLabelWidth / 2
                isHorizontal && horizontalAnchor == Text.HorizontalAnchor.RIGHT -> slotCenterX + maxLabelWidth / 2
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

        val adjustedLabelBoundsList = labelBoundsList.map {
            val origin = DoubleVector(it.origin.x + xBBoxOffset(it), yBBoxOffset(it) + it.origin.y)
            DoubleRectangle(origin, it.dimension)
        }

        return createAxisLabelsLayoutInfoBuilder(bounds, overlap)
            .labelHorizontalAnchor(horizontalAnchor)
            .labelVerticalAnchor(verticalAnchor)
            .labelRotationAngle(-myRotationAngle)
            .labelAdditionalOffsets(labelAdditionalOffsets)
            .labelBoundsList(adjustedLabelBoundsList.map(::alignToLabelMargin)) // for debug drawing
            .build()
    }

    override fun labelBounds(labelNormalSize: DoubleVector): DoubleRectangle {
        return BreakLabelsLayoutUtil.rotatedLabelBounds(labelNormalSize, myRotationAngle).let {
            DoubleRectangle(0.0, 0.0, it.width, it.height)
        }
    }

    // For vertical text (rotation ±90°): screen-y anchoring is done by `text-anchor` and is driven by vjust.
    private fun hAnchorForVerticalLabels(vjust: Double): Text.HorizontalAnchor {
        if (vjust != 0.0 && vjust != 1.0) {
            return Text.HorizontalAnchor.MIDDLE
        }
        return when (myRotationAngle) {
            90.0 -> if (vjust == 1.0) Text.HorizontalAnchor.RIGHT else Text.HorizontalAnchor.LEFT
            -90.0 -> if (vjust == 1.0) Text.HorizontalAnchor.LEFT else Text.HorizontalAnchor.RIGHT
            else -> Text.HorizontalAnchor.MIDDLE
        }
    }

    // For vertical text (rotation ±90°): screen-x anchoring is done by `dy` and is driven by hjust.
    private fun vAnchorForVerticalLabels(hjust: Double): Text.VerticalAnchor {
        if (hjust != 0.0 && hjust != 1.0) {
            return Text.VerticalAnchor.CENTER
        }
        return when (myRotationAngle) {
            90.0 -> if (hjust == 0.0) Text.VerticalAnchor.TOP else Text.VerticalAnchor.BOTTOM
            -90.0 -> if (hjust == 0.0) Text.VerticalAnchor.BOTTOM else Text.VerticalAnchor.TOP
            else -> Text.VerticalAnchor.CENTER
        }
    }

    private fun xOffsetCoefficient(isLabelDirectedFromTick: Boolean, verticalAnchor: Text.VerticalAnchor): Double =
        when {
            isLabelDirectedFromTick && verticalAnchor == Text.VerticalAnchor.BOTTOM -> 1.0
            !isLabelDirectedFromTick && verticalAnchor == Text.VerticalAnchor.TOP -> 1.0
            verticalAnchor != Text.VerticalAnchor.CENTER -> 0.0
            else -> 0.5
        }
}
