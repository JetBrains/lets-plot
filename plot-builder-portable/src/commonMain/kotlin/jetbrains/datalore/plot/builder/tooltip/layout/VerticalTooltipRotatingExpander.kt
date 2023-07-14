/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

internal class VerticalTooltipRotatingExpander(
    private val verticalSpace: DoubleSpan,
    private val horizontalSpace: DoubleSpan
) {

    fun fixOverlapping(tooltips: List<PositionedTooltip>, restrictions: List<DoubleRectangle>): List<PositionedTooltip> {
        // <tooltip index, tooltip coord>
        val expandedPositions = ArrayList<Pair<Int, DoubleVector>>()

        var i = 0
        val n = tooltips.size
        while (i < n) {
            val tooltip = tooltips[i]

            if (intersectsAny(tooltip.rect(), restrictions)) {

                val restrictionsWithStems = ArrayList(restrictions)
                restrictionsWithStems.add(DoubleRectangle(tooltip.stemCoord, POINT_RESTRICTION_SIZE))

                val newPlacement = findValidCandidate(getCandidates(tooltip), restrictionsWithStems)
                if (newPlacement == null) {
                    expandedPositions.add(Pair(i, tooltip.tooltipCoord))
                } else {
                    expandedPositions.add(Pair(i, newPlacement.origin))
                }
            } else {
                expandedPositions.add(Pair(i, tooltip.tooltipCoord))
            }
            i++
        }

        val separatedTooltips = ArrayList<PositionedTooltip>()
        for (expandedPosition in expandedPositions) {
            val positionedTooltip = tooltips[expandedPosition.first]
            separatedTooltips.add(positionedTooltip.moveTo(expandedPosition.second))
        }

        return separatedTooltips
    }

    private fun getCandidates(positionedTooltip: PositionedTooltip): List<DoubleRectangle> {
        val tooltipRotationHelper = TooltipRotationHelper(positionedTooltip)

        val candidates = ArrayList<DoubleRectangle>()

        // Init better positions first
        candidates.add(tooltipRotationHelper.rotate(1.0 / 2.0 * PI))
        candidates.add(tooltipRotationHelper.rotate(3.0 / 2.0 * PI))
        candidates.add(tooltipRotationHelper.rotate(0.0))
        candidates.add(tooltipRotationHelper.rotate(PI))

        var alpha = PI / 2
        var i = 0
        while (i < SECTOR_COUNT) {

            if (alpha > PI) {
                alpha -= PI
            }

            candidates.add(tooltipRotationHelper.rotate(alpha))
            i++
            alpha += SECTOR_ANGLE
        }

        return candidates
    }

    private fun intersectsAny(rect: DoubleRectangle, restrictions: List<DoubleRectangle>): Boolean {
        for (restriction in restrictions) {
            if (rect.intersects(restriction)) {
                return true
            }
        }

        return false
    }

    private fun findValidCandidate(candidates: List<DoubleRectangle>, restrictions: List<DoubleRectangle>): DoubleRectangle? {
        for (candidate in candidates) {
            if (intersectsAny(candidate, restrictions)) {
                continue
            }

            if (DoubleSpan.withLowerEnd(candidate.origin.y, candidate.dimension.y) !in verticalSpace) {
                continue
            }

            if (DoubleSpan.withLowerEnd(candidate.origin.x, candidate.dimension.x) !in horizontalSpace) {
                continue
            }

            return candidate
        }

        return null
    }

    internal class TooltipRotationHelper(positionedTooltip: PositionedTooltip) {

        private val myAttachToTooltipsTopOffset: DoubleVector
        private val myAttachToTooltipsBottomOffset: DoubleVector
        private val myAttachToTooltipsLeftOffset: DoubleVector
        private val myAttachToTooltipsRightOffset: DoubleVector
        private val myTooltipSize: DoubleVector = positionedTooltip.tooltipSize
        private val myTargetCoord: DoubleVector = positionedTooltip.stemCoord

        init {
            val middleX = myTooltipSize.x / 2
            val middleY = myTooltipSize.y / 2

            myAttachToTooltipsTopOffset = DoubleVector(-middleX, 0.0)
            myAttachToTooltipsBottomOffset = DoubleVector(-middleX, -myTooltipSize.y)
            myAttachToTooltipsLeftOffset = DoubleVector(0.0, middleY)
            myAttachToTooltipsRightOffset = DoubleVector(-myTooltipSize.x, middleY)
        }

        fun rotate(alpha: Double): DoubleRectangle {
            val r = TipLayoutHint.StemLength.NORMAL.value
            val newAttachmentCoord = DoubleVector(r * cos(alpha), r * sin(alpha)).add(myTargetCoord)

            val newTooltipCoord = when {
                STEM_TO_BOTTOM_SIDE_ANGLE_RANGE.contains(alpha) -> newAttachmentCoord.add(myAttachToTooltipsBottomOffset)
                STEM_TO_TOP_SIDE_ANGLE_RANGE.contains(alpha) -> newAttachmentCoord.add(myAttachToTooltipsTopOffset)
                STEM_TO_LEFT_SIDE_ANGLE_RANGE.contains(alpha) -> newAttachmentCoord.add(myAttachToTooltipsLeftOffset)
                STEM_TO_RIGHT_SIDE_ANGLE_RANGE.contains(alpha) -> newAttachmentCoord.add(myAttachToTooltipsRightOffset)
                else -> throw IllegalStateException()
            }

            return DoubleRectangle(newTooltipCoord, myTooltipSize)
        }

    }

    companion object {
        private val STEM_TO_LEFT_SIDE_ANGLE_RANGE = DoubleSpan(-1.0 / 4.0 * PI, 1.0 / 4.0 * PI)
        private val STEM_TO_BOTTOM_SIDE_ANGLE_RANGE = DoubleSpan(1.0 / 4.0 * PI, 3.0 / 4.0 * PI)
        private val STEM_TO_RIGHT_SIDE_ANGLE_RANGE = DoubleSpan(3.0 / 4.0 * PI, 5.0 / 4.0 * PI)
        private val STEM_TO_TOP_SIDE_ANGLE_RANGE = DoubleSpan(5.0 / 4.0 * PI, 7.0 / 4.0 * PI)
        private const val SECTOR_COUNT = 36
        private const val SECTOR_ANGLE = PI * 2 / SECTOR_COUNT
        private val POINT_RESTRICTION_SIZE = DoubleVector(1.0, 1.0)
    }
}
