package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.MathUtil.DoubleRange
import jetbrains.datalore.plot.builder.interact.MathUtil.DoubleRange.Companion.withStartAndEnd
import jetbrains.datalore.plot.builder.interact.MathUtil.DoubleRange.Companion.withStartAndLength
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.Companion.moveIntoLimit
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip

internal class HorizontalTooltipExpander(private val mySpace: DoubleRange) {

    fun fixOverlapping(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
        return tooltips
            .sortedWith(compareBy({ it.stemCoord.y }, { it.tooltipCoord.y }))
            .fold(ArrayList<Group>(), ::spaceOutTooltip)
            .flatMap {
                var y = it.range.start()
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
                    groups.set(index, Group(g1.tooltips + g2.tooltips, mySpace))
                    groups.remove(g2)
                }
                ?: return groups // stop the loop if no overlappings
        }
        return groups
    }

    internal class Group internal constructor(
        internal val tooltips: List<PositionedTooltip>,
        private val space: DoubleRange
    ) {
        internal val range: DoubleRange

        constructor(tooltip: PositionedTooltip, space: DoubleRange)
                : this(listOf(tooltip), space) {}

        init {
            val length = tooltips.map { it.length + MARGIN_BETWEEN_TOOLTIPS }.sum() - MARGIN_BETWEEN_TOOLTIPS
            val start = when(tooltips.size) {
                0 -> 0.0
                1 -> tooltips[0].top
                else -> tooltips.sumByDouble { it.middle } / tooltips.size  - length / 2
            }

            range = rangeWithLength(start, length).run { moveIntoLimit(this, space) }
        }

        fun overlaps(other: Group) = range.extend(MARGIN_BETWEEN_TOOLTIPS).overlaps(other.range)
    }
}

private fun DoubleRange.extend(delta: Double) = withStartAndEnd(start() - delta, end() + delta)
private fun rangeWithLength(start: Double, length: Double) = withStartAndLength(start, length)
private val PositionedTooltip.length get() = height
private val PositionedTooltip.middle get() = bottom - height / 2
