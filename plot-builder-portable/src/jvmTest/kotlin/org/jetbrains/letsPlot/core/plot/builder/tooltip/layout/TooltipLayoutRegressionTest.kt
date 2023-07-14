/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.core.plot.builder.tooltip.TestUtil.coord
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.MeasuredTooltipBuilder.MeasuredTooltipBuilderFactory
import kotlin.test.Test
import kotlin.test.assertFalse

internal class TooltipLayoutRegressionTest : TooltipLayoutTestBase() {

    @Test
    fun stemDirectingInsideTooltipCase() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .cursor(coord(152.0, 259.0))
            .addTooltip(factory.horizontal(HORIZONTAL_TIP_KEY, coord(161.20, 255.92)).size(60.12, 48.20).buildTooltip())
            .addTooltip(factory.vertical(VERTICAL_POINT_KEY, coord(151.22, 265.27)).size(41.64, 48.05).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertFalse(tooltip(VERTICAL_POINT_KEY).rect().contains(tooltip(VERTICAL_POINT_KEY).stemCoord()))
    }

    @Test
    fun sideTipAndPointTipsShouldBeArrangedProperly() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(0.0)
            .defaultTipSize(104.90, 21.29)

        val objectCoord = coord(119.70, 241.51)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(factory.horizontal(FIRST_TOOLTIP_KEY, objectCoord).objectRadius(30.92).buildTooltip())
            .addTooltip(factory.horizontal(SECOND_TOOLTIP_KEY, objectCoord).objectRadius(0.0).buildTooltip())
            .addTooltip(factory.vertical(THIRD_TOOLTIP_KEY, objectCoord).objectRadius(0.0).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect().text(FIRST_TOOLTIP_KEY).tooltipCoord(coord(162.62, 217.72)),
            expect().text(SECOND_TOOLTIP_KEY).tooltipCoord(coord(2.79, 244.01)),
            expect().text(THIRD_TOOLTIP_KEY).tooltipCoord(coord(131.7, 252.16))
        )
    }

    @Test
    fun notFixedOverlappingCase1() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(0.0)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(factory.horizontal(FIRST_TOOLTIP_KEY, coord(302.47, 127.52)).size(126.18, 23.61).buildTooltip())
            .addTooltip(
                factory.horizontal(SECOND_TOOLTIP_KEY, coord(302.47, 197.10)).size(135.50, 23.61).buildTooltip()
            )
            .addTooltip(factory.vertical(VERTICAL_POINT_KEY, coord(292.72, 166.17)).size(62.66, 23.40).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(
            expect().text(FIRST_TOOLTIP_KEY).tooltipCoord(coord(164.30, 115.71)),
            expect().text(SECOND_TOOLTIP_KEY).tooltipCoord(coord(154.98, 185.29)),
            expect().text(VERTICAL_POINT_KEY).tooltipCoord(coord(304.72, 177.87))
        )

        assertFalse(tooltip(FIRST_TOOLTIP_KEY).rect().intersects(tooltip(VERTICAL_POINT_KEY).rect()))
    }

    @Test
    fun wrongTooltipsReorderingCase1() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(factory.horizontal("1", coord(145.64, 226.18)).size(79.03, 31.40).buildTooltip())
            .addTooltip(factory.horizontal("2", coord(148.23, 244.30)).size(76.45, 31.40).buildTooltip())
            .addTooltip(factory.horizontal("3", coord(142.69, 254.41)).size(81.99, 29.29).buildTooltip())
            .addTooltip(factory.horizontal("4", coord(139.47, 268.00)).size(85.20, 29.29).buildTooltip())
            .addTooltip(factory.horizontal("5", coord(149.56, 298.54)).size(75.11, 31.61).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(*orderedListOf(5))
    }

    @Test
    fun wrongTooltipsReorderingCase2() {
        val factory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(DEFAULT_OBJECT_RADIUS)

        val layoutManagerController = createTipLayoutManagerBuilder(VIEWPORT)
            .addTooltip(factory.horizontal("1", coord(135.09, 208.07)).size(78.63, 31.40).buildTooltip())
            .addTooltip(factory.horizontal("2", coord(126.05, 230.71)).size(87.66, 31.40).buildTooltip())
            .addTooltip(factory.horizontal("3", coord(131.55, 236.30)).size(82.17, 29.29).buildTooltip())
            .addTooltip(factory.horizontal("4", coord(138.43, 253.25)).size(75.29, 31.61).buildTooltip())
            .addTooltip(factory.horizontal("5", coord(139.94, 254.41)).size(73.78, 29.29).buildTooltip())
            .build()

        arrange(layoutManagerController)

        assertAllTooltips(*orderedListOf(5))
    }

    companion object {
        private const val HORIZONTAL_TIP_KEY = "sidetipkey"
        private const val VERTICAL_POINT_KEY = "aroundpoint"
        private const val FIRST_TOOLTIP_KEY = "1"
        private const val SECOND_TOOLTIP_KEY = "2"
        private const val THIRD_TOOLTIP_KEY = "3"
    }
}
