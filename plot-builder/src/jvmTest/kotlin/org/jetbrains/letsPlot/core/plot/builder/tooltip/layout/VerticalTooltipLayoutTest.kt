/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
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

    @Test
    fun issue837() {
        /*
        {
          "data": {
            "x": [ "a" ],
            "y0": [ -2.6197451040897444 ],
            "y33": [ -0.46572975357025687 ],
            "y50": [ -0.1269562917797126 ],
            "y66": [ 0.3142473325952739 ],
            "y100": [ 1.8522781845089378 ]
          },
          "mapping": { "x": "x" },
          "data_meta": {
            "series_annotations": [
              { "type": "str", "column": "x" },
              { "type": "float", "column": "y0" },
              { "type": "float", "column": "y33" },
              { "type": "float", "column": "y50" },
              { "type": "float", "column": "y66" },
              { "type": "float", "column": "y100" }
            ]
          },
          "coord": { "name": "flip", "flip": true },
          "ggsize": { "width": 700.0, "height": 90.0 },
          "kind": "plot",
          "layers": [
            {
              "geom": "boxplot",
              "stat": "identity",
              "mapping": {
                "ymin": "y0",
                "lower": "y33",
                "middle": "y50",
                "upper": "y66",
                "ymax": "y100"
              },
              "height": 0.2
            }
          ]
        }
        */

        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(22.727272727272727)
            .defaultTipSize(DoubleVector(40.78125, 28.0))

        val layoutManagerController = createTipLayoutManagerBuilder(DoubleRectangle.XYWH(0, 0, 700, 90))
            .addTooltip(tooltipBuilder.rotated("rotated", coord(447.68382202975073, 59.0)).buildTooltip())
            .geomBounds(DoubleRectangle.XYWH(50.03470122028142, 34.0, 639.9652987797186, 50.0))
            .build()
        arrange(layoutManagerController)

        assertAllTooltips(
            expect()
                .tooltipY(45.0)
        )
    }

    @Test
    fun issue1275() {
        // geom_point(x=10, y=5, size=3, tooltips=layer_tooltips().line('foo\nbar\nbaz'))

        val tooltipBuilder = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(4.0)
            .defaultTipSize(DoubleVector(34, 48))

        val layoutManagerController = createTipLayoutManagerBuilder(DoubleRectangle.XYWH(0, 0, 700, 560))
            .addTooltip(tooltipBuilder.vertical("vertical", DoubleVector(507, 29)).buildTooltip())
            .geomBounds(DoubleRectangle.XYWH(51, 7, 912, 501))

        val bottomOriented = expect().tooltipY(45.0).tooltipX(490.0)

        // Not enough space to orient the tooltip to the top - move to the bottom
        arrange(layoutManagerController.cursor(DoubleVector(520, 7)).build())
        assertAllTooltips(bottomOriented)

        // Even if the cursor covers the tooltip, the tooltip should be moved to the bottom position
        arrange(layoutManagerController.cursor(DoubleVector(520, 59)).build())
        assertAllTooltips(bottomOriented)
    }

    @Test
    fun `very long tooltip may cover target if no space available`() {
        val tooltipHeight = 400
        val targetOffset = 12 + 4 // the stem + object radius
        val tooltipFactory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(4.0)
            .defaultTipSize(70, tooltipHeight)

        val geomBounds = DoubleRectangle.XYWH(0, 0, 800, 600)
        val targetX = geomBounds.center.x
        val topAlignmentOffset = targetOffset + tooltipHeight
        val bottomAlignmentOffset = targetOffset

        val plotState = createTipLayoutManagerBuilder(viewport = geomBounds)

        // not enough space for the tooltip
        run {
            val centerTooltip = tooltipFactory.vertical("center", DoubleVector(targetX, geomBounds.center.y)).buildTooltip()
            plotState.setTooltips(centerTooltip)

            // cursor covers the tooltip
            arrange(plotState.cursor(centerTooltip.hintCoord).build())
            assertAllTooltips(expect()
                .tooltipY(centerTooltip.hintCoord.y - centerTooltip.size.y / 2) // centered vertically
                .coversThePoint(centerTooltip.hintCoord)
            )
        }

        // enough space for the tooltip with the bottom orientation
        run {
            val topTooltip = tooltipFactory.vertical("top", DoubleVector(targetX, geomBounds.top)).buildTooltip()
            plotState.setTooltips(topTooltip)

            // cursor doesn't cover the tooltip
            arrange(plotState.cursor(topTooltip.hintCoord).build())
            assertAllTooltips(expect()
                .tooltipY(topTooltip.hintCoord.y + bottomAlignmentOffset) // bottom position
                .doesNotCoverThePoint(topTooltip.hintCoord)
            )
        }

        // enough space for the tooltip with the bottom orientation
        run {
            val bottomTooltip = tooltipFactory.vertical("bottom", DoubleVector(targetX, geomBounds.bottom)).buildTooltip()
            plotState.setTooltips(bottomTooltip)

            // cursor doesn't cover the tooltip
            arrange(plotState.cursor(bottomTooltip.hintCoord).build())
            assertAllTooltips(expect()
                .tooltipY(bottomTooltip.hintCoord.y - topAlignmentOffset) // top position
                .doesNotCoverThePoint(bottomTooltip.hintCoord)
            )
        }
    }

    @Test
    fun `tooltip should never cover the target point if there is enough space for either bottom or top orientation`() {
        val tooltipHeight = 50
        val tooltipWidth = 70
        val targetOffset = 12 + 4 // the stem + object radius
        val tooltipOffset = tooltipHeight + targetOffset + 10 // stem length + padding from the border
        val tooltipFactory = MeasuredTooltipBuilderFactory()
            .defaultObjectRadius(4.0)
            .defaultTipSize(tooltipWidth, tooltipHeight)

        val geomBounds = DoubleRectangle.XYWH(0, 0, 800, 600)
        val targetX = geomBounds.center.x
        val overlappingOffset = targetOffset + 20 // cursor overlapping the tooltip
        val topAlignmentOffset = targetOffset + tooltipHeight
        val bottomAlignmentOffset = targetOffset

        val plotState = createTipLayoutManagerBuilder(viewport = geomBounds)

        run {
            val topTooltip = tooltipFactory.vertical("top", DoubleVector(targetX, geomBounds.top)).buildTooltip()
            plotState.setTooltips(topTooltip)

            // cursor doesn't cover the tooltip
            arrange(plotState.cursor(topTooltip.hintCoord).build())
            // Not enough top space - orient to the bottom
            assertAllTooltips(expect()
                .tooltipY(topTooltip.hintCoord.y + bottomAlignmentOffset)
                .doesNotCoverThePoint(topTooltip.hintCoord)
            ) // bottom position

            // cursor covers the tooltip in bottom position
            arrange(plotState.cursor(targetX, topTooltip.hintCoord.y + overlappingOffset).build())
            // Not enough top space - keep the bottom position
            assertAllTooltips(expect()
                .tooltipY(topTooltip.hintCoord.y + bottomAlignmentOffset)
                .doesNotCoverThePoint(topTooltip.hintCoord)
            )
        }

        run {
            val upperTooltip = tooltipFactory.vertical("upper", DoubleVector(targetX, geomBounds.top + tooltipOffset)).buildTooltip()
            plotState.setTooltips(upperTooltip)

            // cursor doesn't cover the tooltip
            arrange(plotState.cursor(upperTooltip.hintCoord).build())
            // Enough top space - orient to the top
            assertAllTooltips(expect()
                .tooltipY(upperTooltip.hintCoord.y - topAlignmentOffset)
                .doesNotCoverThePoint(upperTooltip.hintCoord)
            ) // top position

            // cursor covers the tooltip in top position
            arrange(plotState.cursor(targetX, upperTooltip.hintCoord.y - overlappingOffset).build())
            assertAllTooltips(expect()
                .tooltipY(upperTooltip.hintCoord.y + bottomAlignmentOffset)
                .doesNotCoverThePoint(upperTooltip.hintCoord)
            ) // bottom position

            // cursor covers the tooltip in bottom position
            arrange(plotState.cursor(targetX, upperTooltip.hintCoord.y + overlappingOffset).build())
            assertAllTooltips(expect()
                .tooltipY(upperTooltip.hintCoord.y - topAlignmentOffset)
                .doesNotCoverThePoint(upperTooltip.hintCoord)
            ) // top position
        }

        run {
            val lowerTooltip = tooltipFactory.vertical("lower", DoubleVector(targetX, geomBounds.bottom - tooltipOffset)).buildTooltip()
            plotState.setTooltips(lowerTooltip)

            // cursor doesn't cover the tooltip
            arrange(plotState.cursor(lowerTooltip.hintCoord).build())
            // Enough top space - orient to the top
            assertAllTooltips(expect()
                .tooltipY(lowerTooltip.hintCoord.y - topAlignmentOffset)
                .doesNotCoverThePoint(lowerTooltip.hintCoord)
            )

            // cursor covers the tooltip in top position
            arrange(plotState.cursor(targetX, lowerTooltip.hintCoord.y - overlappingOffset).build())
            assertAllTooltips(expect()
                .tooltipY(lowerTooltip.hintCoord.y + bottomAlignmentOffset)
                .doesNotCoverThePoint(lowerTooltip.hintCoord)
            ) // bottom position

            // cursor covers the tooltip in bottom position
            arrange(plotState.cursor(targetX, lowerTooltip.hintCoord.y + overlappingOffset).build())
            assertAllTooltips(expect()
                .tooltipY(lowerTooltip.hintCoord.y - topAlignmentOffset)
                .doesNotCoverThePoint(lowerTooltip.hintCoord)
            ) // top position
        }

        run {
            val bottomTooltip = tooltipFactory.vertical("bottom", DoubleVector(targetX, geomBounds.bottom)).buildTooltip()
            plotState.setTooltips(bottomTooltip)

            // cursor doesn't cover the tooltip
            arrange(plotState.cursor(bottomTooltip.hintCoord).build())
            // Not enough top space - orient to the bottom
            assertAllTooltips(expect()
                .tooltipY(bottomTooltip.hintCoord.y - topAlignmentOffset)
                .doesNotCoverThePoint(bottomTooltip.hintCoord)
            )

            // cursor covers the tooltip in top position
            arrange(plotState.cursor(targetX, bottomTooltip.hintCoord.y - overlappingOffset).build())
            // Not enough bottom space - keep the top position
            assertAllTooltips(expect()
                .tooltipY(bottomTooltip.hintCoord.y - topAlignmentOffset)
                .doesNotCoverThePoint(bottomTooltip.hintCoord)
            )
        }
    }

    companion object {
        private const val VERTICAL_TIP_KEY = "vertical"
        private const val HORIZONTAL_TIP_KEY = "horizontal"
    }
}
