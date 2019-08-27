package jetbrains.datalore.visualization.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.interact.MathUtil.DoubleRange
import jetbrains.datalore.visualization.plot.builder.interact.MathUtil.DoubleRange.Companion.withStartAndEnd
import jetbrains.datalore.visualization.plot.builder.interact.MathUtil.DoubleRange.Companion.withStartAndLength
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.Companion.moveIntoLimit
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip

internal typealias TtGroup  = HorizontalTooltipExpander.Group<PositionedTooltip>

internal class HorizontalTooltipExpander(private val mySpace: DoubleRange) {
    internal class Group<ItemT: PositionedTooltip> internal constructor(
        item: ItemT,
        private var range: DoubleRange,
        private val space: DoubleRange
    ) {
        private val items = ArrayList<Pair<ItemT, Double>>()

        init {
            items.add(item, range.length())
        }

        fun overlaps(other: Group<ItemT>) = range.extend(MARGIN_BETWEEN_TOOLTIPS).overlaps(other.range)

        internal fun positions(): List<Pair<PositionedTooltip, DoubleRange>> {
            var y = range.start()
            return items.map { Pair(it.tooltip, rangeWithLength(y, it.length)).apply { y += it.length + MARGIN_BETWEEN_TOOLTIPS } }
        }

        fun join(other: Group<ItemT>): Group<ItemT> {
            items.addAll(other.items)
            val unitedLength = items.map { it.length + MARGIN_BETWEEN_TOOLTIPS }.sum() - MARGIN_BETWEEN_TOOLTIPS
            val unitedMiddle = (this.range.middle() + other.range.middle()) / 2
            val unitedRange = withStartAndLength(unitedMiddle - unitedLength / 2, unitedLength)
            range = moveIntoLimit(unitedRange, space)
            return this
        }
    }


    fun fixOverlapping(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
        return tooltips
            .sortedWith(compareBy( { it.stemCoord.y}, { it.tooltipCoord.y } ) )
            .fold(ArrayList<TtGroup>(), ::spaceOutTooltip)
            .flatMap { it.positions() }
            .map { (tt, position) -> tt.moveTo(DoubleVector(tt.left, position.start())) }
    }

    private fun spaceOutTooltip(groups: ArrayList<TtGroup>, tt: PositionedTooltip): ArrayList<TtGroup> {
        groups.add(TtGroup(tt, withStartAndLength(tt.tooltipCoord.y, tt.height), mySpace))

        // space out one by one overlapped group
        // 50 overlapped groups maximum in case of very limited space, when we have no room for all groups
        for (i in 1..50) {
            val overlapping = findOverlappedGroups(groups)
            if (overlapping == null) {
                break;
            }

            val upperIndex = groups.indexOf(overlapping.first)
            groups[upperIndex] = overlapping.first.join(overlapping.second)
            groups.remove(overlapping.second)
        }
        return groups
    }

    private fun findOverlappedGroups(groups: List<TtGroup>): Pair<TtGroup, TtGroup>? {
        return groups
            .windowed(2)
            .filter { (upper, lower) -> upper.overlaps(lower) }
            .map { (upper, lower) -> Pair(upper, lower) }
            .firstOrNull()
    }
}

private fun DoubleRange.extend(delta: Double) = withStartAndEnd(start() - delta, end() + delta)
private fun DoubleRange.middle() = start() + length() / 2
private fun <A, B> MutableList<Pair<A, B>>.add(a: A, b: B) = add(Pair(a, b))
private val Pair<PositionedTooltip, Double>.length get() = second
private val Pair<PositionedTooltip, Double>.tooltip get() = first
private fun rangeWithLength(start: Double, length: Double) = withStartAndLength(start, length)
