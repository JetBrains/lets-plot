/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleVector.Companion.ZERO
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipModel
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipModel.Line
import org.jetbrains.letsPlot.core.plot.base.tooltip.loc.createTooltipModels
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import kotlin.test.assertEquals

object TooltipTestUtil {

    private val axisTheme = DefaultTheme.minimal2().horizontalAxis(false)

    private fun createTooltipModels(layer: GeomLayer, hitIndex: Int, tooltipHint: TooltipHint): List<TooltipModel> {
        return createTooltipModels(
            geomTarget = GeomTarget(
                hitIndex = hitIndex,
                tooltipHint = tooltipHint,
                aesTooltipHint = emptyMap()
            ),
            contextualMapping = layer.createContextualMapping()!!,
            axisOrigin = ZERO,
            flippedAxis = false,
            xAxisTheme = axisTheme,
            yAxisTheme = axisTheme,
            ctx = TestingPlotContext.create(layer)
        )
    }

    internal fun assertXAxisTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val tooltipModels = createTooltipModels(layer, hitIndex, TooltipHint.xAxisTooltip(coord = ZERO))
        val actualXAxisTooltip = tooltipModels.filter { it.tooltipHint.placement == TooltipHint.Placement.X_AXIS }

        assertEquals(expectedLines.isEmpty(), actualXAxisTooltip.isEmpty())

        if (actualXAxisTooltip.isNotEmpty()) {
            val actualLines = actualXAxisTooltip.single().lines.map(Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }

    internal fun assertGeneralTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val tooltipModels = createTooltipModels(layer, hitIndex, TooltipHint.cursorTooltip(coord = ZERO))
        val actualGeneralTooltips = tooltipModels.filterNot(TooltipModel::isSide)

        assertEquals(expectedLines.isEmpty(), actualGeneralTooltips.isEmpty())

        if (actualGeneralTooltips.isNotEmpty()) {
            val actualLines = actualGeneralTooltips.single().lines.map(Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }
}
