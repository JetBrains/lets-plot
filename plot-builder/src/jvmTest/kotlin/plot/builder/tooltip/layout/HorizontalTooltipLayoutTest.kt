/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.TestUtil.coord
import jetbrains.datalore.plot.builder.interact.TestUtil.size
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment.LEFT
import jetbrains.datalore.plot.builder.tooltip.layout.LayoutManager.HorizontalAlignment.RIGHT
import jetbrains.datalore.plot.builder.tooltip.layout.MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory
import kotlin.test.Test

internal class HorizontalTooltipLayoutTest : TooltipLayoutTestBase() {

    @Test
    fun whenThereIsNotEnoughHorizontalSpaceFromLeft_ShouldAlignTooltipToRight() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(TOOLTIP_KEY, coord(80.0, 200.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect()
                        .tooltipX(expectedSideTipX(TOOLTIP_KEY, RIGHT))
                        .tooltipY(expectedSideTipY(TOOLTIP_KEY))
        )
    }

    @Test
    fun whenThereIsEnoughHorizontalAndVerticalSpaceFromBothSides_ShouldAlignTooltipToLeft() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(TOOLTIP_KEY, coord(250.0, 200.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect()
                        .tooltipX(expectedSideTipX(TOOLTIP_KEY, LEFT))
                        .tooltipY(expectedSideTipY(TOOLTIP_KEY))
        )
    }

    @Test
    fun whenThereIsNotEnoughVerticalSpaceFromTop_ShouldMoveTooltipDown() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(TOOLTIP_KEY, coord(250.0, 10.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect()
                        .tooltipY(0.0)
        )
    }

    @Test
    fun whenOverlapped_AndThereIsNotEnoughSpaceFromLeft_ShouldAlignTooltipsToRight() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val tipX = 20.0

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(FIRST_TOOLTIP_KEY, coord(tipX, 250.0)).buildTooltip())
                .addTooltip(tooltipBuilder.horizontal(SECOND_TOOLTIP_KEY, coord(tipX, 260.0)).buildTooltip())
                .addTooltip(tooltipBuilder.horizontal(THIRD_TOOLTIP_KEY, coord(tipX, 270.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect().tooltipX(expectedSideTipX(FIRST_TOOLTIP_KEY, RIGHT)),
                expect().tooltipX(expectedSideTipX(SECOND_TOOLTIP_KEY, RIGHT)),
                expect().tooltipX(expectedSideTipX(THIRD_TOOLTIP_KEY, RIGHT))
        )
    }

    @Test
    fun whenThereIsNotEnoughVerticalSpaceFromBottom_ShouldMoveTooltipUp() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(IGNORED_KEY, coord(250.0, 490.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(expect().tooltipY(460.0))
    }

    @Test
    fun whenOverlappedAndThereIsNotEnoughVerticalSpaceFromBottom_ShouldMoveTooltipUp() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(IGNORED_KEY, coord(80.0, 480.0)).buildTooltip())
                .addTooltip(tooltipBuilder.horizontal(IGNORED_KEY, coord(80.0, 490.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect().tooltipY(415.0),
                expect().tooltipY(460.0)
        )

        assertInsideView(VIEWPORT)
    }

    @Test
    fun whenTwoFromThreeAreOverlapped_shouldRearrangeOnlyOverlappedItems() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
                .defaultTipSize(80.0, 80.0)

        val tipX = 20.0

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .addTooltip(tooltipBuilder.horizontal(FIRST_TOOLTIP_KEY, coord(tipX, 50.0)).buildTooltip())
                .addTooltip(tooltipBuilder.horizontal(SECOND_TOOLTIP_KEY, coord(tipX, 400.0)).buildTooltip())
                .addTooltip(tooltipBuilder.horizontal(THIRD_TOOLTIP_KEY, coord(tipX, 420.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect().tooltipY(expectedSideTipY(FIRST_TOOLTIP_KEY)),
                expect().tooltipY(327.5),
                expect().tooltipY(412.5)
        )
    }

    @Test
    fun whenOverlappedAfterArrange_ShouldRearrangeOverlappedAgain() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)

        val tipWidth = 80.0

        val layoutManagerController = createTipLayoutManagerBuilder(DoubleRectangle(0.0, 0.0, 500.0, 800.0))
                .addTooltip(tooltipBuilder.horizontal(IGNORED_KEY, coord(20.0, 200.0)).size(tipWidth, 40.0).buildTooltip())   // tip1 is not overlapped right now
                .addTooltip(tooltipBuilder.horizontal(IGNORED_KEY, coord(20.0, 400.0)).size(tipWidth, 200.0).buildTooltip()) // tip2 and tip3 are overlapped
                .addTooltip(tooltipBuilder.horizontal(IGNORED_KEY, coord(20.0, 400.0)).size(tipWidth, 200.0).buildTooltip())
                .build()

        arrange(layoutManagerController)

        // after rearrange tip1 should overlap tip2 and another rearrange should happen
        assertAllTooltips(
                expect().tooltipY(108.33333333333331),
                expect().tooltipY(153.33333333333331),
                expect().tooltipY(358.3333333333333)
        )
    }

    @Test
    fun whenThereIsNotEnoughVerticalSpaceForAllTooltips_ShouldSelectNearestTooltip() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(size(80.0, 200.0))

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(coord(90.0, 320.0))
            .addTooltip(tooltipBuilder.horizontal(FIRST_TOOLTIP_KEY,  coord(20.0, 250.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(SECOND_TOOLTIP_KEY, coord(20.0, 300.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(THIRD_TOOLTIP_KEY,  coord(20.0, 350.0)).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect().text(SECOND_TOOLTIP_KEY)
                    .tooltipY(expectedSideTipY(SECOND_TOOLTIP_KEY))
        )
    }

    @Test
    fun whenThereIsNotEnoughVerticalSpaceForAllTooltips_And_AllAboveCursor_ShouldSelectNearestTooltip() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(size(80.0, 200.0))

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(coord(90.0, 400.0))
            .addTooltip(tooltipBuilder.horizontal(FIRST_TOOLTIP_KEY,  coord(20.0, 150.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(SECOND_TOOLTIP_KEY, coord(20.0, 350.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(THIRD_TOOLTIP_KEY,  coord(20.0, 250.0)).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect().text(SECOND_TOOLTIP_KEY)
                .tooltipY(expectedSideTipY(SECOND_TOOLTIP_KEY))
        )
    }

    @Test
    fun whenThereIsNotEnoughVerticalSpaceForAllTooltips_And_AllUnderCursor_ShouldSelectNearestTooltip() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(size(80.0, 200.0))

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(coord(90.0, 50.0))
            .addTooltip(tooltipBuilder.horizontal(FIRST_TOOLTIP_KEY,  coord(20.0, 250.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(SECOND_TOOLTIP_KEY, coord(20.0, 150.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(THIRD_TOOLTIP_KEY,  coord(20.0, 350.0)).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect().text(SECOND_TOOLTIP_KEY)
                .tooltipY(expectedSideTipY(SECOND_TOOLTIP_KEY))
        )
    }

    @Test
    fun whenThereIsNotEnoughHorizontalSpaceFromBothSides_AndHorizontalAlignmentIsLeft_ShouldAlignTooltipToRightOfTheLeftBorder() {
        val objectRadius = 200.0
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(objectRadius)
            .defaultTipSize(size(80.0, 80.0))

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(tooltipBuilder.horizontal(TOOLTIP_KEY, VIEWPORT.center).buildTooltip())
            .build()

        arrange(layoutManagerController)

        val stemX = VIEWPORT.center.x - objectRadius
        assertAllTooltips(
            expect()
                .tooltipX(stemX + NORMAL_STEM_LENGTH)
                .stemCoord(DoubleVector(stemX, VIEWPORT.center.y))
        )
    }

    @Test
    fun whenThereIsNotEnoughHorizontalSpaceFromBothSides_AndHorizontalAlignmentIsRight_ShouldAlignTooltipToLeftOfTheRightBorder() {
        val objectRadius = 200.0
        val tipSize = size(80.0, 80.0)
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(objectRadius)
            .defaultTipSize(tipSize)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .preferredHorizontalAlignment(RIGHT)
            .addTooltip(tooltipBuilder.horizontal(TOOLTIP_KEY, VIEWPORT.center).buildTooltip())
            .build()

        arrange(layoutManagerController)

        val stemX = VIEWPORT.center.x + objectRadius
        assertAllTooltips(
            expect()
                .tooltipX(stemX - tipSize.x - NORMAL_STEM_LENGTH)
                .stemCoord(DoubleVector(stemX, VIEWPORT.center.y))
        )
    }

    @Test
    fun `tooltips are out of visibility`() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(tooltipBuilder.horizontal(FIRST_TOOLTIP_KEY,  coord(10.0,  10.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(SECOND_TOOLTIP_KEY, coord(250.0, 350.0)).buildTooltip())
            .addTooltip(tooltipBuilder.horizontal(THIRD_TOOLTIP_KEY,  coord(350.0, 250.0)).buildTooltip())
            .geomBounds(LIMIT_RECT)
            .build()
        arrange(layoutManagerController)

        assertNoTooltips()
    }

    @Test
    fun `left stem coordinate is out of visibility - move tooltip to right position`() {
        // the left coordinate (pointed to by the tooltip) is outside the area,
        // so tooltip will be moved to right position
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(20.0)
            .defaultTipSize(DoubleVector(40.0, 40.0))

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(tooltipBuilder.horizontal(TOOLTIP_KEY, coord(110.0,100.0)).buildTooltip())
            .geomBounds(LIMIT_RECT)
            .build()
        arrange(layoutManagerController)

        assertAllTooltips(
            expect().tooltipX(expectedSideTipX(TOOLTIP_KEY, RIGHT))
        )
    }

    companion object {
        private const val IGNORED_KEY = "ignored"
        private const val TOOLTIP_KEY = "tooltip"
        private const val FIRST_TOOLTIP_KEY = "1"
        private const val SECOND_TOOLTIP_KEY = "2"
        private const val THIRD_TOOLTIP_KEY = "3"
    }
}
