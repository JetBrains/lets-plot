/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.TooltipAnchor
import jetbrains.datalore.plot.base.interact.TooltipAnchor.HorizontalAnchor.*
import jetbrains.datalore.plot.base.interact.TooltipAnchor.VerticalAnchor.*
import kotlin.test.Test


internal class TooltipAnchorTest : TooltipLayoutTestBase() {

    private val tooltipBuilder = MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory()
        .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
        .defaultTipSize(DEFAULT_TOOLTIP_SIZE)
        .defaultObjectRadius(10.0)

    @Test
    fun `anchor = top_left`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .build()
        arrange(layoutManagerController)
        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(5.0).tooltipY(0.0)
        )
    }

    @Test
    fun `anchor = bottom_right`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(BOTTOM, RIGHT))
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(420.0).tooltipY(455.0)
        )
    }

    @Test
    fun `anchor = middle_center`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(MIDDLE, CENTER))
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(210.0).tooltipY(227.5)
        )
    }

    @Test
    fun `corner tooltip under cursor should be moved to the opposite corner`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(DoubleVector.ZERO)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .build()
        arrange(layoutManagerController)
        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(420.0).tooltipY(0.0)
        )
    }

    @Test
    fun `tooltips with different anchors`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .addTooltip(
                tooltipBuilder.horizontal(
                    SECOND_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, RIGHT))
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(5.0).tooltipY(0.0),
            expect(SECOND_TOOLTIP_KEY).tooltipX(420.0).tooltipY(0.0)
        )
    }

    @Test
    fun `tooltips with same anchors -  should arrange one under the other`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .addTooltip(
                tooltipBuilder.horizontal(
                    SECOND_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(5.0).tooltipY(0.0),
            expect(SECOND_TOOLTIP_KEY).tooltipX(5.0).tooltipY(45.0)
        )
    }

    @Test
    fun `anchor tooltip is overlapped with a horizontal tooltip - should fix overlapping for horizontals`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .addTooltip(
                tooltipBuilder.horizontal(
                    SECOND_TOOLTIP_KEY,
                    DoubleVector(120.0, 20.0)
                ).buildTooltip(anchor = null)
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(5.0).tooltipY(0.0),
            expect(SECOND_TOOLTIP_KEY).tooltipX(18.0).tooltipY(45.0)
        )
    }

    @Test
    fun `anchor tooltip is overlapped with a vertical tooltip - should fix overlapping for verticals`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(COORD)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    COORD
                ).buildTooltip(anchor = TooltipAnchor(MIDDLE, CENTER))
            )
            .addTooltip(
                tooltipBuilder.vertical(
                    SECOND_TOOLTIP_KEY,
                    DoubleVector(200.0, 270.0)
                ).buildTooltip(anchor = null)
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(210.0).tooltipY(227.5),
            expect(SECOND_TOOLTIP_KEY).tooltipX(212.0).tooltipY(280.0)
        )
    }

    companion object {
        private const val FIRST_TOOLTIP_KEY = "1"
        private const val SECOND_TOOLTIP_KEY = "2"
        private val COORD = VIEWPORT.center
    }
}