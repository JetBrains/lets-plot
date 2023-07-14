/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.Companion.moveIntoLimit
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip

internal class HorizontalTooltipExpander(private val mySpace: DoubleSpan) {

    fun fixOverlapping(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
        return tooltips
            .sortedWith(compareBy({ it.stemCoord.y }, { it.tooltipCoord.y }))
            .fold(ArrayList(), ::spaceOutTooltip)
            .flatMap {
                var y = it.range.lowerEnd
                it.tooltips.map { tooltip ->
                    tooltip.moveTo(DoubleVector(tooltip.left, y))
                        .also { y += tooltip.height + MARGIN_BETWEEN_TOOLTIPS }
                }
            }
    }

    private fun spaceOutTooltip(groups: ArrayList<Group>, tt: PositionedTooltip): ArrayList<Group> {
        groups.add(Group(tt, mySpace))

        // space out one by one overlapped group
        // 50 overlapped groups maximum in case of very limited space, when we have no room for all groups
        repeat(50) {
            groups
                .windowed(2) // [1, 2, 3, 4] -> [1, 2], [2, 3], [3, 4]
                .firstOrNull { (g1, g2) -> g1.overlaps(g2) }
                ?.let { (g1, g2) ->
                    val index = groups.indexOf(g1)
                    groups[index] = Group(g1.tooltips + g2.tooltips, mySpace)
                    groups.remove(g2)
                }
                ?: return groups // stop the loop if no overlappings
        }
        return groups
    }

    internal class Group internal constructor(
        internal val tooltips: List<PositionedTooltip>,
        private val space: DoubleSpan
    ) {
        constructor(tooltip: PositionedTooltip, space: DoubleSpan) : this(listOf(tooltip), space)

        internal val range: DoubleSpan

        init {
            val height = tooltips.sumOf(PositionedTooltip::height) + (tooltips.size - 1) * MARGIN_BETWEEN_TOOLTIPS
            val start = when (tooltips.size) {
                0 -> 0.0
                1 -> tooltips[0].top
                else -> tooltips.sumOf { it.bottom - it.height / 2 } / tooltips.size - height / 2
            }

            range = DoubleSpan.withLowerEnd(start, height).run { moveIntoLimit(this, space) }
        }

        fun overlaps(other: Group): Boolean =
            DoubleSpan(
                range.lowerEnd - MARGIN_BETWEEN_TOOLTIPS,
                range.upperEnd + MARGIN_BETWEEN_TOOLTIPS
            ).connected(other.range)
    }
}
