/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleVector.Companion.ZERO
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec.Line
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpecFactory
import kotlin.test.assertEquals

object TooltipTestUtil {

    private val axisTheme = DefaultTheme.minimal2().horizontalAxis(false)

    private fun createTooltipSpecs(layer: GeomLayer, hitIndex: Int, tipLayoutHint: TipLayoutHint): List<TooltipSpec> {
        val factory = TooltipSpecFactory(
            contextualMapping = layer.createContextualMapping(),
            axisOrigin = ZERO,
            flippedAxis = false,
            xAxisTheme = axisTheme,
            yAxisTheme = axisTheme
        )
        return factory.create(
            geomTarget = GeomTarget(
                hitIndex = hitIndex,
                tipLayoutHint = tipLayoutHint,
                aesTipLayoutHints = emptyMap()
            ),
            ctx = TestingPlotContext.create(layer)
        )
    }

    internal fun assertXAxisTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val tooltipSpecs = createTooltipSpecs(layer, hitIndex, TipLayoutHint.xAxisTooltip(coord = ZERO))
        val actualXAxisTooltip = tooltipSpecs.filter { it.layoutHint.kind == TipLayoutHint.Kind.X_AXIS_TOOLTIP }

        assertEquals(expectedLines.isEmpty(), actualXAxisTooltip.isEmpty())

        if (actualXAxisTooltip.isNotEmpty()) {
            val actualLines = actualXAxisTooltip.single().lines.map(Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }

    internal fun assertGeneralTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val tooltipSpecs = createTooltipSpecs(layer, hitIndex, TipLayoutHint.cursorTooltip(coord = ZERO))
        val actualGeneralTooltips = tooltipSpecs.filterNot(TooltipSpec::isSide)

        assertEquals(expectedLines.isEmpty(), actualGeneralTooltips.isEmpty())

        if (actualGeneralTooltips.isNotEmpty()) {
            val actualLines = actualGeneralTooltips.single().lines.map(Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }
}
