/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.builder.interact.MathUtil.bottomEdgeOf
import jetbrains.datalore.plot.builder.interact.MathUtil.leftEdgeOf
import jetbrains.datalore.plot.builder.interact.MathUtil.rightEdgeOf
import jetbrains.datalore.plot.builder.interact.MathUtil.topEdgeOf
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip
import kotlin.math.max

internal class VerticalTooltipShiftingExpander(
    @Suppress("UNUSED_PARAMETER") space: DoubleSpan
) {

    private var mySpacedTooltips: MutableList<Pair<Int, DoubleVector>>? = null

    val spacedTooltips: List<Pair<Int, DoubleVector>>?
        get() = mySpacedTooltips

    fun fixOverlapping(tooltips: List<Pair<Int, PositionedTooltip>>, restrictions: List<DoubleRectangle>) {
        mySpacedTooltips = ArrayList()

        for (tooltip in tooltips) {
            val overlappings = analyzeOverlapping(tooltip.second, restrictions)

            if (overlappings.isEmpty()) {
                mySpacedTooltips!!.add(Pair(tooltip.first, tooltip.second.tooltipCoord))
                continue
            }

            mySpacedTooltips!!.add(Pair(tooltip.first, tooltip.second.tooltipCoord.add(findVector(overlappings))))
        }
    }

    private fun analyzeOverlapping(tooltip: PositionedTooltip, restrictions: List<DoubleRectangle>): Map<Side, Double> {
        val tooltipRect = tooltip.rect()
        val topSide = topEdgeOf(tooltipRect)
        val leftSide = leftEdgeOf(tooltipRect)
        val bottomSide = bottomEdgeOf(tooltipRect)
        val rightSide = rightEdgeOf(tooltipRect)

        val overlappings = HashMap<Side, Double>()
        for (restriction in restrictions) {
            val intersection = tooltipRect.intersect(restriction) ?: continue

            val currentOverlappings = HashMap<Side, Double>()
            for (part in restriction.parts) {
                checkSideOverlapping(topSide, part, intersection, Side.TOP, currentOverlappings)
                checkSideOverlapping(leftSide, part, intersection, Side.LEFT, currentOverlappings)
                checkSideOverlapping(bottomSide, part, intersection, Side.BOTTOM, currentOverlappings)
                checkSideOverlapping(rightSide, part, intersection, Side.RIGHT, currentOverlappings)
            }

            if (currentOverlappings.isEmpty()) {
                throw IllegalStateException("Intersection was detected, but no points added")

            } else if (currentOverlappings.size == 1) {
                overlappings.putAll(currentOverlappings)

            } else if (currentOverlappings.size == 2) {
                // Detect overlapping direction. When both parallel sides are intersected check
                // rectangle points.
                if (containsAll(currentOverlappings, Side.LEFT, Side.RIGHT)) {
                    if (pointsOf(topEdgeOf(tooltipRect)).contains(intersection.origin)) {
                        // If top point of intersected rectangle is same as top point of tooltip rect - top side overlapped
                        overlappings[Side.TOP] = intersection.dimension.y
                    } else if (pointsOf(bottomEdgeOf(tooltipRect)).contains(bottomEdgeOf(intersection).start)) {
                        // If bottom point of intersected rectangle is same as bottom point of tooltip rect - bottom side overlapped
                        overlappings[Side.BOTTOM] = intersection.dimension.y
                    }

                } else if (containsAll(currentOverlappings, Side.TOP, Side.BOTTOM)) {
                    if (pointsOf(leftEdgeOf(tooltipRect)).contains(intersection.origin)) {
                        overlappings[Side.LEFT] = intersection.dimension.x
                    } else if (pointsOf(bottomEdgeOf(tooltipRect)).contains(rightEdgeOf(intersection).start)) {
                        overlappings[Side.RIGHT] = intersection.dimension.x
                    }

                } else {
                    putAllIfGreater(overlappings, currentOverlappings)
                }
            } else {
                putAllIfGreater(overlappings, currentOverlappings)
            }
        }

        return overlappings
    }

    private fun putAllIfGreater(existingOverlappings: MutableMap<Side, Double>, newOverlappings: Map<Side, Double>) {
        for (side in newOverlappings.keys) {
            if (!existingOverlappings.containsKey(side) || existingOverlappings[side]!! < newOverlappings.getValue(side)) {
                existingOverlappings[side] = newOverlappings.getValue(side)
            }
        }
    }

    private fun pointsOf(doubleSegment: DoubleSegment): List<DoubleVector> {
        val points = ArrayList<DoubleVector>()
        points.add(doubleSegment.start)
        points.add(doubleSegment.end)
        return points
    }

    private fun checkSideOverlapping(
        tooltipSide: DoubleSegment, restrictedPart: DoubleSegment, intersection: DoubleRectangle, side: Side, overlappings: MutableMap<Side, Double>) {
        if (restrictedPart.intersection(tooltipSide) == null) {
            return
        }

        val intersectionLength = if (side == Side.LEFT || side == Side.RIGHT)
            intersection.dimension.x
        else
            intersection.dimension.y

        if (!overlappings.containsKey(side)) {
            overlappings[side] = intersectionLength
        } else {
            overlappings[side] = max(overlappings[side]!!, intersectionLength)
        }
    }

    private fun findVector(overlappings: Map<Side, Double>): DoubleVector {
        if (overlappings.isEmpty()) {
            return DoubleVector.ZERO
        }

        if (overlappings.size == 1) {
            val side = overlappings.keys.iterator().next()
            val value = overlappings.getValue(side)
            return vectorBySide(side, value)
        }

        if (overlappings.containsKey(Side.LEFT)) {
            return vectorBySide(Side.LEFT, overlappings.getValue(Side.LEFT))
        }

        if (overlappings.containsKey(Side.RIGHT)) {
            return vectorBySide(Side.RIGHT, overlappings.getValue(Side.RIGHT))
        }

        val minOverlappedSide = min(overlappings)
        return vectorBySide(minOverlappedSide, overlappings.getValue(minOverlappedSide))
    }

    private fun vectorBySide(side: Side, overlapping: Double): DoubleVector {
        return when (side) {

            Side.LEFT -> DoubleVector(overlapping, 0.0)

            Side.RIGHT -> DoubleVector(-overlapping, 0.0)

            Side.TOP -> DoubleVector(0.0, overlapping)

            Side.BOTTOM -> DoubleVector(0.0, -overlapping)
        }
    }

    private fun containsAll(map: Map<Side, Double>, vararg keys: Side): Boolean {
        if (map.size != keys.size) {
            return false
        }

        for (side in keys) {
            if (!map.containsKey(side)) {
                return false
            }
        }

        return true
    }

    private fun min(map: Map<Side, Double>): Side {
        var minSide: Side? = null

        for (side in map.keys) {
            if (minSide == null) {
                minSide = side
            } else {
                if (map.getValue(minSide) > map.getValue(side)) {
                    minSide = side
                }
            }
        }

        return minSide!!
    }

    internal enum class Side {
        LEFT, RIGHT, TOP, BOTTOM
    }

}
