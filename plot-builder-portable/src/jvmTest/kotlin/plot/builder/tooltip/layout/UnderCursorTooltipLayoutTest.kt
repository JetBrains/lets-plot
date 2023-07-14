/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.layout

import jetbrains.datalore.plot.builder.tooltip.TestUtil.coord
import jetbrains.datalore.plot.builder.tooltip.TestUtil.size
import jetbrains.datalore.plot.builder.tooltip.layout.MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory
import kotlin.test.Test

internal class UnderCursorTooltipLayoutTest : TooltipLayoutTestBase() {

    @Test
    fun underCursorShouldStayAtDesiredCoord_AroundPointMovedOut() {
        val size = size(100.0, 100.0)
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
                .defaultObjectRadius(0.0)
                .defaultTipSize(size)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
                .cursor(coord(200.0, 200.0))
                .addTooltip(tooltipBuilder.cursor(CURSOR_TIP_KEY).buildTooltip())
                .addTooltip(tooltipBuilder.vertical(VERTICAL_TIP_KEY, coord(200.0, 200.0)).buildTooltip())
                .build()

        arrange(layoutManagerController)

        assertAllTooltips(
                expect().text(CURSOR_TIP_KEY).tooltipCoord(coord(150.0, 88.0)),
                expect().text(VERTICAL_TIP_KEY).tooltipCoord(coord(212.0, 250.0))
        )
    }

    @Test
    fun `tooltip is out of visibility`() {
        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)
            .defaultTipSize(DEFAULT_TOOLTIP_SIZE)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(coord(250.0, 350.0))
            .addTooltip(tooltipBuilder.cursor(CURSOR_TIP_KEY).buildTooltip())
            .geomBounds(LIMIT_RECT)
            .build()
        arrange(layoutManagerController)

        assertNoTooltips()
    }

    companion object {

        private const val VERTICAL_TIP_KEY = "vertical"
        private const val CURSOR_TIP_KEY = "cursor"
    }


}
