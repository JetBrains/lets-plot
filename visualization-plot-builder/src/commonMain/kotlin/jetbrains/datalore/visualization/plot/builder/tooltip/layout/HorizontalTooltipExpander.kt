package jetbrains.datalore.visualization.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.builder.interact.MathUtil
import jetbrains.datalore.visualization.plot.builder.presentation.Defaults.Common.Tooltip.MARGIN_BETWEEN_TOOLTIPS
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.Companion.moveIntoLimit
import jetbrains.datalore.visualization.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip

internal class HorizontalTooltipExpander(private val mySpace: MathUtil.DoubleRange) {

    private val myGroups = ArrayList<Group>()
    private fun yCoordComparator(t1: PositionedTooltip, t2: PositionedTooltip): Int {
        if (t1 == t2) {
            return 0
        }

        val stemCompare = t1.stemCoord.y.compareTo(t2.stemCoord.y)
        return if (stemCompare != 0) {
            stemCompare
        } else t1.tooltipCoord.y.compareTo(t2.tooltipCoord.y)

    }

    private fun prepareTooltipData(tooltips: List<PositionedTooltip>): List<Pair<Int, MathUtil.DoubleRange>> {
        val tooltipHeights = ArrayList<Pair<Int, MathUtil.DoubleRange>>()
        var i = 0
        val n = tooltips.size
        while (i < n) {
            val data = tooltips[i]
            tooltipHeights.add(Pair(i, MathUtil.DoubleRange.withStartAndLength(data.tooltipCoord.y, data.height)))
            i++
        }

        return tooltipHeights
    }

    fun fixOverlapping(tooltips: List<PositionedTooltip>): List<PositionedTooltip> {
        @Suppress("NAME_SHADOWING")
        var tooltips = tooltips
        tooltips = ArrayList(tooltips)
        tooltips.sortWith(Comparator { t1, t2 -> yCoordComparator(t1, t2) })

        val tooltipHeights = prepareTooltipData(tooltips)

        myGroups.clear()

        for (pair in tooltipHeights) {
            addGroup(pair.first, pair.second)

            var limit = 50
            while (getOverlappedGroups(myGroups) != null && limit-- > 0) {
                spaceOutGroups(myGroups)
            }
        }

        val separatedTooltips = ArrayList<PositionedTooltip>()
        for (expandedPlacementInfo in groupsToRange(myGroups)) {
            val positionedTooltip = tooltips[expandedPlacementInfo.first]
            separatedTooltips.add(
                    positionedTooltip.moveTo(
                            DoubleVector(
                                    positionedTooltip.tooltipCoord.x,
                                    expandedPlacementInfo.second.start()
                            )
                    )
            )
        }
        return separatedTooltips
    }

    private fun groupsToRange(groups: List<Group>): List<Pair<Int, MathUtil.DoubleRange>> {
        val result = ArrayList<Pair<Int, MathUtil.DoubleRange>>()
        var index = 0
        for (group in groups) {
            for (range in group.ranges()) {
                result.add(Pair(index++, range))
            }
        }
        return result
    }

    private fun addGroup(index: Int, range: MathUtil.DoubleRange?) {
        val newGroup = Group(index, range!!, mySpace)
        myGroups.add(newGroup)
    }

    private fun spaceOutGroups(groups: List<Group>) {
        val overlappedGroups = getOverlappedGroups(groups) ?: return

        join(overlappedGroups.first, overlappedGroups.second)
    }

    private fun join(first: Group?, second: Group?) {
        val firstIndex = myGroups.indexOf(first)
        myGroups[firstIndex] = first!!.join(second!!)
        myGroups.remove(second)
    }

    private fun getOverlappedGroups(groups: List<Group>): Pair<Group, Group>? {
        var i = 0
        val n = groups.size - 1
        while (i < n) {
            val g1 = groups[i]
            val g2 = groups[i + 1]

            if (g1.overlaps(g2)) {
                return Pair(g1, g2)
            }
            i++
        }

        return null
    }

    private class Group internal constructor(index: Int, private var myRange: MathUtil.DoubleRange, private val mySpace: MathUtil.DoubleRange) {
        private val myLengths = ArrayList<Pair<Int, Double>>()

        init {
            myLengths.add(Pair(index, myRange.length()))
        }

        internal fun start(): Double {
            return myRange.start()
        }

        internal fun length(): Double {
            return myRange.length()

        }

        private fun middle(g1: Group, g2: Group): Double {
            return (middle(g1) + middle(g2)) / 2
        }

        private fun middle(group: Group): Double {
            return group.start() + group.length() / 2
        }

        internal fun ranges(): List<MathUtil.DoubleRange> {
            val result = ArrayList<MathUtil.DoubleRange>()

            var start = myRange.start()
            for (pair in myLengths) {
                result.add(MathUtil.DoubleRange.withStartAndLength(start, pair.second))
                start += pair.second + MARGIN_BETWEEN_TOOLTIPS
            }

            return result
        }

        fun overlaps(other: Group): Boolean {
            return myRange.overlaps(other.myRange)
        }

        fun join(group: Group): Group {
            for (pair in group.myLengths) {
                myLengths.add(Pair(pair.first, pair.second))
            }

            val newMiddle = middle(this, group)
            update(newMiddle)
            return this
        }

        private fun update(newMiddle: Double) {
            var desiredLength = 0.0
            for (pair in myLengths) {
                desiredLength += pair.second
            }
            desiredLength += ((myLengths.size - 1) * MARGIN_BETWEEN_TOOLTIPS).toDouble()

            val offset = desiredLength / 2
            myRange = MathUtil.DoubleRange.withStartAndLength(newMiddle - offset, desiredLength)
            myRange = moveIntoLimit(myRange, mySpace)
        }
    }
}
