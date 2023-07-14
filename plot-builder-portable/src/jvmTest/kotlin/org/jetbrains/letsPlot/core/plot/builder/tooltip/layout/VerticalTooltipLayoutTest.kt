/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.coord
import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.size
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.BOTTOM
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.TOP
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory
import kotlin.test.Test
import kotlin.test.assertFalse

internal class VerticalTooltipLayoutTest : TooltipLayoutTestBase() {

    @Test
    fun verticalOverlappedByHorizontal_WhenTooltipIsSmall_ShouldMoveAroundPoint() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(0.0)
            .defaultTipSize(size(40.0, 20.0))

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(tooltipBuilder.horizontal(HORIZONTAL_TIP_KEY, coord(200.0, 200.0)).buildTooltip())
            .addTooltip(tooltipBuilder.vertical(VERTICAL_TIP_KEY, coord(200.0, 200.0)).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertFalse(tooltip(HORIZONTAL_TIP_KEY).rect().intersects(tooltip(VERTICAL_TIP_KEY).rect()))
    }

    @Test
    fun verticalWithRadius_ShouldOffsetTooltipForRadiusValue() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(factory.vertical(VERTICAL_TIP_KEY, VIEWPORT.center).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect()
                .text(VERTICAL_TIP_KEY)
                .tooltipY(expectedAroundPointY(VERTICAL_TIP_KEY, TOP))
                .tooltipX(expectedAroundPointX(VERTICAL_TIP_KEY))
        )
    }

    @Test
    fun verticalWithRadius_WhenThereIsNotEnoughVerticalSpace_AndCursorAboveTooltip() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(22.0)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(coord(75.0, 68.0))
            .addTooltip(factory.vertical(VERTICAL_TIP_KEY, coord(79.0, 26.0)).size(58.0, 21.0).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect()
                .stemCoord(expectedAroundPointStem(VERTICAL_TIP_KEY))
        )
    }

    @Test
    fun vertical_WhenCursorIsNotOverlappedByTooltip_ButTooltipVerticalProjectionOverlapsCursorProjection_ShouldSaveTooltipTopPosition() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(0.0)

        val cursorCoord = coord(0.0, 200.0)
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(cursorCoord)
            .addTooltip(
                factory.vertical(VERTICAL_TIP_KEY, cursorCoord.add(size(300.0, 50.0))).size(100.0, 100.0).buildTooltip()
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect()
                .tooltipY(expectedAroundPointY(VERTICAL_TIP_KEY, TOP))
        )
    }

    @Test
    fun vertical_WhenCursorIsOverlappedByTooltip_ShouldMoveTooltipToBottomPosition() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(0.0)

        val cursorCoord = coord(200.0, 200.0)
        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(cursorCoord)
            .addTooltip(
                factory.vertical(VERTICAL_TIP_KEY, cursorCoord.add(size(0.0, 50.0))).size(100.0, 100.0).buildTooltip()
            )
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect()
                .tooltipY(expectedAroundPointY(VERTICAL_TIP_KEY, BOTTOM))
        )
    }

    @Test
    fun `tooltips are out of visibility`() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(tooltipBuilder.vertical(VERTICAL_TIP_KEY, coord(10.0, 10.0)).buildTooltip())
            .addTooltip(tooltipBuilder.vertical(VERTICAL_TIP_KEY, coord(250.0, 350.0)).buildTooltip())
            .addTooltip(tooltipBuilder.vertical(VERTICAL_TIP_KEY, coord(350.0, 250.0)).buildTooltip())
            .geomBounds(LIMIT_RECT)
            .build()
        arrange(layoutManagerController)

        assertNoTooltips()
    }

    @Test
    fun `top stem coordinate is out of visibility - should move tooltip to bottom position`() {
        // the top coordinate (pointed to by the tooltip) is outside the area,
        // so tooltip will be moved to bottom position
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(tooltipBuilder.vertical(VERTICAL_TIP_KEY, coord(100.0, 100.0)).buildTooltip())
            .geomBounds(LIMIT_RECT)
            .build()
        arrange(layoutManagerController)

        assertAllTooltips(
            expect()
                .tooltipY(expectedAroundPointY(VERTICAL_TIP_KEY, BOTTOM))
        )
    }

    companion object {
        private const val VERTICAL_TIP_KEY = "vertical"
        private const val HORIZONTAL_TIP_KEY = "horizontal"
    }
}
