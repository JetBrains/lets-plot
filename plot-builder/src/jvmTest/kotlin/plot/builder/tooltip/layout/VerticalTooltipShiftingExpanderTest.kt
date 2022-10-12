/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.builder.interact.TestUtil.coord
import jetbrains.datalore.plot.builder.interact.TestUtil.rect
import jetbrains.datalore.plot.builder.interact.TestUtil.size
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.MeasuredTooltip
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.PositionedTooltip
import jetbrains.datalore.plot.builder.tooltip.layout.MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory
import jetbrains.datalore.plot.builder.tooltip.layout.VerticalTooltipShiftingExpanderTest.Intersection.Companion.center
import jetbrains.datalore.plot.builder.tooltip.layout.VerticalTooltipShiftingExpanderTest.Intersection.OffsetDirection
import jetbrains.datalore.plot.builder.tooltip.layout.VerticalTooltipShiftingExpanderTest.Intersection.Position.CENTER_SIDE
import jetbrains.datalore.plot.builder.tooltip.layout.VerticalTooltipShiftingExpanderTest.Intersection.Position.OFFSET
import jetbrains.datalore.plot.builder.tooltip.layout.VerticalTooltipShiftingExpanderTest.Intersection.Side
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class VerticalTooltipShiftingExpanderTest {

    private lateinit var myTooltipsFactory: MeasuredTooltipBuilderFactory
    private lateinit var arrangedTooltips: MutableList<Pair<Int, PositionedTooltip>>
    private lateinit var restrictions: MutableList<DoubleRectangle>
    private lateinit var helper: VerticalTooltipShiftingExpander

    @BeforeTest
    fun setup() {
        myTooltipsFactory = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(0.0)
                .defaultTipText("")

        arrangedTooltips = ArrayList()
        restrictions = ArrayList()

        helper = VerticalTooltipShiftingExpander(DoubleSpan.withLowerEnd(0.0, 1000.0))
    }

    @Test
    fun whenNoOverlapping_ShouldAddTooltips() {
        val firstTooltipCoord = coord(75.0, 40.0)
        val secondTooltipCoord = coord(75.0, 40.0)

        addTooltip(positionedTooltip(rect(firstTooltipCoord, size(50.0, 50.0))))
        addTooltip(positionedTooltip(rect(secondTooltipCoord, size(50.0, 50.0))))

        fixOverlapping()

        assertEquals(firstTooltipCoord, getNewTooltipPos(0))
        assertEquals(secondTooltipCoord, getNewTooltipPos(1))
    }

    @Test
    fun whenOverlappedInsideLeftSide_AndCanMoveRight_ShouldFixOverlapping() {
        val tooltipX = 75.0
        val intersectionDepth = 25.0

        val tooltipRect = rect(coord(tooltipX, 40.0), size(50.0, 50.0))
        addRestriction(intersect(tooltipRect)
                .with(center(Side.LEFT).depth(intersectionDepth))
                .build())

        addTooltip(positionedTooltip(tooltipRect))

        fixOverlapping()

        assertEquals(coord(tooltipX + intersectionDepth, 40.0), getNewTooltipPos(0))
    }

    @Test
    fun whenOverlappedInsideTopSide_AndCanMoveDown_ShouldFixOverlapping() {
        val tooltipX = 75.0
        val tooltipY = 40.0
        val intersectionDepth = 25.0

        val tooltipRect = rect(
                coord(tooltipX, tooltipY),
                size(50.0, 50.0)
        )

        addRestriction(intersect(tooltipRect)
                .with(center(Side.TOP).depth(intersectionDepth))
                .build())

        addTooltip(positionedTooltip(tooltipRect))

        fixOverlapping()

        assertEquals(coord(tooltipX, tooltipY + intersectionDepth), getNewTooltipPos(0))
    }

    @Test
    fun whenOverlappedInsideLeftAndTop_AndCanMoveToTheRightAndDown_ShouldMoveToShortestDirection() {
        val tooltipX = 75.0
        val leftDepth = 25.0
        val topDepth = 10.0

        val tooltipRect = rect(
                coord(tooltipX, 40.0),
                size(50.0, 50.0)
        )

        addRestriction(intersect(tooltipRect)
                .with(center(Side.LEFT).depth(leftDepth))
                .build())

        addRestriction(intersect(tooltipRect)
                .with(center(Side.TOP).depth(topDepth))
                .build())

        addTooltip(positionedTooltip(tooltipRect))

        fixOverlapping()

        assertEquals(coord(tooltipRect.left + leftDepth, tooltipRect.top), getNewTooltipPos(0))
    }

    private fun getNewTooltipPos(index: Int): DoubleVector {
        return helper.spacedTooltips!![index].second
    }

    private fun addRestriction(rectangle: DoubleRectangle?) {
        restrictions.add(rectangle!!)
    }

    private fun addTooltip(tooltipData: PositionedTooltip) {
        arrangedTooltips.add(Pair(arrangedTooltips.size, tooltipData))
    }

    private fun positionedTooltip(): TooltipsBuilder {
        return TooltipsBuilder()
    }

    private fun positionedTooltip(tooltipRect: DoubleRectangle): PositionedTooltip {
        return positionedTooltip()
                .tooltipCoord(tooltipRect.origin)
                .measuredTooltip(myTooltipsFactory.vertical(IGNORED_KEY, coord(0.0, 0.0)).size(tooltipRect.dimension).buildTooltip())
                .build()
    }

    private fun intersect(target: DoubleRectangle): IntersectionBuilder {
        return IntersectionBuilder(target)
    }

    private fun fixOverlapping() {
        helper.fixOverlapping(arrangedTooltips, restrictions)
    }

    internal class Intersection private constructor(internal val mySide: Side, internal val myPosition: Position, internal val myOffset: Double, internal val myDirection: OffsetDirection?) {
        internal var myDepth: Double = 0.toDouble()
        internal val myLedge: Double

        init {
            myDepth = 0.0
            myLedge = 10.0
        }

        fun depth(depth: Double): Intersection {
            myDepth = depth
            return this
        }

        internal enum class Side {
            LEFT, RIGHT, TOP, BOTTOM
        }

        internal enum class Position {
            CENTER_SIDE,
            WHOLE_SIDE,
            OFFSET
        }

        internal enum class OffsetDirection {
            FROM_START,
            FROM_END
        }

        companion object {

            fun center(side: Side): Intersection {
                return Intersection(side, CENTER_SIDE, 0.0, null)
            }
        }

    }

    internal class IntersectionBuilder(private val myTarget: DoubleRectangle) {
        private val myIntersections = ArrayList<Intersection>()

        fun with(vararg intersections: Intersection): IntersectionBuilder {
            myIntersections.addAll(intersections)
            return this
        }

        internal fun build(): DoubleRectangle? {
            if (myIntersections.isEmpty()) {
                throw IllegalStateException("No intersections were added")
            }
            if (myIntersections.size == 1) {
                val intersection = myIntersections[0]
                return getIntersectionRectangle(intersection)
            }

            if (myIntersections.size == 2) {
                return getIntersectionRectangle(myIntersections[0], myIntersections[1])
            }

            throw IllegalStateException("Too many intersections. Should be one or two.")
        }

        private fun getIntersectionRectangle(intersection1: Intersection, intersection2: Intersection): DoubleRectangle? {
            if (intersection1.mySide == intersection2.mySide) {
                throw IllegalArgumentException("Intersections have same side kind")
            }

            if (intersection1.myPosition == OFFSET) {
                val point1 = getIntersectionPointWithOffset(intersection1)
                val point2 = getIntersectionPointWithOffset(intersection2)

                if (isSameSideKind(intersection1, intersection2)) {
                    assertEquals(intersection1.myDirection, intersection2.myDirection)
                    assertDoubleEquals(intersection1.myOffset, intersection2.myOffset)
                    assertDoubleEquals(intersection1.myLedge, intersection2.myLedge)

                    if (isVertical(intersection1)) {
                        assertDoubleEquals(point1.y, point2.y)
                        val x = Math.min(point1.x, point2.x)
                        val width = Math.max(point1.x, point2.x) - x
                        val height = intersection1.myOffset + intersection1.myLedge

                        val y: Double = if (intersection1.myDirection == OffsetDirection.FROM_END) {
                            myTarget.bottom
                        } else {
                            myTarget.top
                        }
                        return DoubleRectangle(x, y, width, height)
                    }
                }
            }
            return null
        }

        private fun getIntersectionPointWithOffset(intersection1: Intersection): DoubleVector {
            val segment = segmentBySide(intersection1.mySide)
            val relativePoint = if (intersection1.myDirection == OffsetDirection.FROM_START) segment.start else segment.end
            return relativePoint.add(vectorBySide(intersection1.myOffset, intersection1.mySide))
        }

        private fun getIntersectionRectangle(intersection: Intersection): DoubleRectangle {
            when (intersection.myPosition) {
                CENTER_SIDE -> {
                    val segment = segmentBySide(intersection.mySide)
                    val step = segment.length() / 3
                    val intersectionLength = intersection.myLedge + intersection.myDepth

                    when (intersection.mySide) {
                        Side.LEFT -> {
                            val x = myTarget.left - intersection.myLedge
                            val y = myTarget.top + step
                            return DoubleRectangle(x, y, intersectionLength, step)
                        }

                        Side.RIGHT -> {
                            val x = myTarget.right - intersection.myDepth
                            val y = myTarget.top + step
                            return DoubleRectangle(x, y, intersectionLength, step)
                        }

                        Side.TOP -> {
                            val x = myTarget.left + step
                            val y = myTarget.top - intersection.myLedge
                            return DoubleRectangle(x, y, step, intersectionLength)
                        }

                        Side.BOTTOM -> {
                            val x = myTarget.left + step
                            val y = myTarget.bottom - intersection.myDepth
                            return DoubleRectangle(x, y, step, intersectionLength)
                        }

                        else -> throw IllegalStateException()
                    }
                }

                else -> throw IllegalStateException("Not implemented")
            }
        }

        fun segmentBySide(side: Side): DoubleSegment {
            return when (side) {

                Side.LEFT -> jetbrains.datalore.plot.builder.interact.MathUtil.leftEdgeOf(myTarget)

                Side.RIGHT -> jetbrains.datalore.plot.builder.interact.MathUtil.rightEdgeOf(myTarget)

                Side.TOP -> jetbrains.datalore.plot.builder.interact.MathUtil.topEdgeOf(myTarget)

                Side.BOTTOM -> jetbrains.datalore.plot.builder.interact.MathUtil.bottomEdgeOf(myTarget)
            }
        }

        fun vectorBySide(value: Double, side: Side): DoubleVector {
            return if (side === Side.LEFT || side === Side.RIGHT) {
                DoubleVector(0.0, value)
            } else {
                DoubleVector(value, 0.0)
            }
        }

        fun isVertical(intersection: Intersection): Boolean {
            return intersection.mySide == Side.LEFT || intersection.mySide == Side.RIGHT
        }

        fun isSameSideKind(vararg intersections: Intersection): Boolean {
            var controlValue: Boolean? = null
            for (intersection in intersections) {
                if (controlValue == null) {
                    controlValue = isVertical(intersection)
                } else {
                    if (controlValue != isVertical(intersection)) {
                        return false
                    }
                }
            }

            return true
        }

        private fun assertDoubleEquals(expected: Double, actual: Double) {
            jetbrains.datalore.base.assertion.assertEquals(expected, actual, 0.001)
        }

    }

    internal class TooltipsBuilder {
        private lateinit var myTooltipCoord: DoubleVector
        private var myStemCoord: DoubleVector? = null
        private lateinit var myMeasuredTooltip: MeasuredTooltip

        fun build(): PositionedTooltip {
            return PositionedTooltip(myMeasuredTooltip, myTooltipCoord, myStemCoord ?: myTooltipCoord)
        }

        fun tooltipCoord(v: DoubleVector): TooltipsBuilder {
            myTooltipCoord = v
            return this
        }

        fun stemCoord(v: DoubleVector): TooltipsBuilder {
            myStemCoord = v
            return this
        }

        fun measuredTooltip(measuredTooltip: MeasuredTooltip): TooltipsBuilder {
            myMeasuredTooltip = measuredTooltip
            return this
        }

    }

    companion object {
        private const val IGNORED_KEY = "ignored"
    }
}