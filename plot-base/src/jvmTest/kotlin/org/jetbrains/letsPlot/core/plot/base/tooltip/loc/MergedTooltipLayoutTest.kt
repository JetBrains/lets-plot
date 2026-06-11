/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.NullPlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.*
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehavior
import org.jetbrains.letsPlot.core.plot.base.tooltip.layout.LayoutManager
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ConstantField
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import kotlin.test.Test

class MergedTooltipLayoutTest {

    @Test
    fun `merged bar tooltip is anchored to the targets span, blocks sorted top to bottom`() {
        val cursor = DoubleVector(120.0, 300.0)

        // three dodged bars: tops at different heights, HORIZONTAL placement like BarGeom
        val barTops = listOf(
            DoubleVector(100.0, 50.0),
            DoubleVector(120.0, 60.0),
            DoubleVector(140.0, 40.0)
        )
        val tooltipModels = chooseTooltipModels(
            barTops.map { TooltipHint.horizontalTooltip(it, objectRadius = 10.0, marker = MARKER) },
            cursor,
            flippedAxis = false
        )

        val mergedTooltip = tooltipModels.single()
        assertThat(mergedTooltip.isMerged).isTrue()
        assertThat(mergedTooltip.placement).isEqualTo(TooltipHint.Placement.HORIZONTAL)

        // blocks read top-to-bottom - same order as target markers on the plot
        assertThat(mergedTooltip.blocks.map(TooltipModel.Block::targetCoord)).containsExactly(
            DoubleVector(140.0, 40.0),
            DoubleVector(100.0, 50.0),
            DoubleVector(120.0, 60.0)
        )

        val positioned = arrange(tooltipModels, cursor, flippedAxis = false).single()

        // anchored beside the targets span (center x=120, half-span 20 + radius 10),
        // not following the cursor at (120, 300)
        assertThat(positioned.stemCoord).isEqualTo(DoubleVector(150.0, 50.0))
        assertThat(positioned.tooltipCoord).isEqualTo(DoubleVector(162.0, 30.0))
    }

    @Test
    fun `flipped merged bar tooltip is anchored to the targets span`() {
        val cursor = DoubleVector(300.0, 120.0)

        // three flipped (horizontal) bars: ends at different x, VERTICAL placement like flipped BarGeom
        val barEnds = listOf(
            DoubleVector(200.0, 100.0),
            DoubleVector(180.0, 120.0),
            DoubleVector(220.0, 140.0)
        )
        val tooltipModels = chooseTooltipModels(
            barEnds.map { TooltipHint.verticalTooltip(it, objectRadius = 10.0, marker = MARKER) },
            cursor,
            flippedAxis = true
        )

        val mergedTooltip = tooltipModels.single()
        assertThat(mergedTooltip.isMerged).isTrue()
        assertThat(mergedTooltip.placement).isEqualTo(TooltipHint.Placement.VERTICAL)

        val positioned = arrange(tooltipModels, cursor, flippedAxis = true).single()

        // anchored above the targets span (center (200,120), half y-span 20 + radius 10),
        // not following the cursor at (300, 120)
        assertThat(positioned.stemCoord).isEqualTo(DoubleVector(200.0, 90.0))
        assertThat(positioned.tooltipCoord).isEqualTo(DoubleVector(160.0, 38.0))
    }

    @Test
    fun `merged tooltip keeps per-block target coords for point markers and crosshairs`() {
        val cursor = DoubleVector(120.0, 300.0)
        val targetCoords = listOf(
            DoubleVector(100.0, 50.0),
            DoubleVector(120.0, 60.0)
        )
        val tooltipModels = chooseTooltipModels(
            targetCoords.map { TooltipHint.verticalTooltip(it, objectRadius = 5.0, marker = MARKER) },
            cursor,
            flippedAxis = false
        )

        val mergedTooltip = tooltipModels.single()
        assertThat(mergedTooltip.isMerged).isTrue()
        // the renderer draws point markers and crosshairs through these coords
        assertThat(mergedTooltip.blocks.map(TooltipModel.Block::targetCoord))
            .containsExactlyElementsOf(targetCoords)
        assertThat(mergedTooltip.blocks.map(TooltipModel.Block::targetRadius)).containsExactly(5.0, 5.0)
    }

    private fun chooseTooltipModels(
        tooltipHints: List<TooltipHint>,
        cursor: DoubleVector,
        flippedAxis: Boolean
    ): List<TooltipModel> {
        val targetsPicker = LocatedTargetsPicker(
            flippedAxis = flippedAxis,
            cursorCoord = cursor,
            axisOrigin = DoubleVector.ZERO,
            xAxisTheme = TestUtil.axisTheme,
            yAxisTheme = TestUtil.axisTheme,
            ctx = NullPlotContext
        )
        val contextualMapping = ContextualMapping(
            tooltipBehavior = TooltipBehavior.DEFAULT,
            tooltipLines = listOf(
                LinePattern("value", "{}", listOf(ConstantField(Aes.Y, "18.37")))
            ),
            tooltipTitle = null
        )
        return targetsPicker.chooseTooltipModels(
            tooltipHints.map { hint ->
                GeomTarget(
                    hitIndex = 0,
                    tooltipHint = hint,
                    aesTooltipHint = emptyMap()
                )
            },
            contextualMapping
        )
    }

    private fun arrange(
        tooltipModels: List<TooltipModel>,
        cursor: DoubleVector,
        flippedAxis: Boolean
    ): List<LayoutManager.PositionedTooltip> {
        val layoutManager = LayoutManager(
            DoubleRectangle(0.0, 0.0, 500.0, 500.0),
            LayoutManager.HorizontalAlignment.LEFT,
            margin = 5.0,
            flippedAxis = flippedAxis
        )
        return layoutManager.arrange(
            tooltipModels.map { LayoutManager.MeasuredTooltip(it, DoubleVector(80.0, 40.0), strokeWidth = 0.0) },
            cursor,
            DoubleRectangle(0.0, 0.0, 500.0, 500.0),
            HorizontalAxisTooltipPosition.BOTTOM,
            VerticalAxisTooltipPosition.LEFT
        )
    }

    companion object {
        private val MARKER = TooltipMarker.create(majorColor = Color.DARK_GREEN)
    }
}
