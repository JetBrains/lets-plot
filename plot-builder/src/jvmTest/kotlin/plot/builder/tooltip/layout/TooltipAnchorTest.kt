/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.plot.base.interact.TooltipAnchor
import jetbrains.datalore.plot.base.interact.TooltipAnchor.HorizontalAnchor.*
import jetbrains.datalore.plot.base.interact.TooltipAnchor.VerticalAnchor.*
import jetbrains.datalore.plot.builder.interact.TestUtil.coord
import kotlin.test.Test


internal class TooltipAnchorTest : TooltipLayoutTestBase() {

    private val tooltipBuilder = MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory()
        .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
        .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

    @Test
    fun `anchor = top_left`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(CURSOR)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
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
            .cursor(CURSOR)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
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
            .cursor(CURSOR)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
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
            .cursor(coord(5.0, 5.0))
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .build()
        arrange(layoutManagerController)
        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(420.0).tooltipY(0.0)
        )
    }

    @Test
    fun `two tooltips with different anchors`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(CURSOR)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .addTooltip(
                tooltipBuilder.horizontal(
                    SECOND_TOOLTIP_KEY,
                    VIEWPORT.center
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
            .cursor(CURSOR)
            .addTooltip(
                tooltipBuilder.horizontal(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .addTooltip(
                tooltipBuilder.horizontal(
                    SECOND_TOOLTIP_KEY,
                    VIEWPORT.center
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
    fun `vertical tooltips with same anchors -  should arrange one under the other`() {
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(CURSOR)
            .addTooltip(
                tooltipBuilder.vertical(
                    FIRST_TOOLTIP_KEY,
                    VIEWPORT.center
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .addTooltip(
                tooltipBuilder.vertical(
                    SECOND_TOOLTIP_KEY,
                    VIEWPORT.center
                ).buildTooltip(anchor = TooltipAnchor(TOP, LEFT))
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect(FIRST_TOOLTIP_KEY).tooltipX(5.0).tooltipY(0.0),
            expect(SECOND_TOOLTIP_KEY).tooltipX(5.0).tooltipY(45.0)
        )
    }

    companion object {
        private const val FIRST_TOOLTIP_KEY = "1"
        private const val SECOND_TOOLTIP_KEY = "2"
        private val CURSOR = VIEWPORT.center
    }
}