/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec.DataPoint
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import kotlin.test.assertEquals

internal object TooltipAssertions {
    data class TooltipLine(val label: String?, val value: String) {
        override fun toString(): String {
            return if (label.isNullOrEmpty()) value else "$label: $value"
        }
    }

    fun assertGeneralTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val actualLines = getGeneralTooltipLines(layer, hitIndex).map(TooltipLine::toString)
        assertEquals(expectedLines.size, actualLines.size, "Wrong number of lines in the general tooltip")
        expectedLines.zip(actualLines).forEachIndexed { index, (expected, actual) ->
            assertEquals(expected, actual, "Wrong line #$index in the general tooltip")
        }
    }

    fun assertXAxisTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val actualLines = getAxisTooltips(layer, Aes.X, hitIndex).map(DataPoint::value)
        assertEquals(expectedLines.size, actualLines.size, "Wrong number of X-axis tooltip lines")
        expectedLines.zip(actualLines).forEachIndexed { index, (expected, actual) ->
            assertEquals(expected, actual, "Wrong X-axis tooltip line #$index")
        }
    }

    fun getGeneralTooltipLines(layer: GeomLayer, hitIndex: Int = 0): List<TooltipLine> {
        return getDataPoints(layer, hitIndex)
            .filter { !it.isSide && !it.isAxis }
            .map { TooltipLine(it.label, it.value) }
    }

    fun getAxisTooltips(layer: GeomLayer, aes: Aes<*>, hitIndex: Int = 0): List<DataPoint> {
        return getDataPoints(layer, hitIndex).filter { it.isAxis && it.aes == aes }
    }

    fun getSideTooltips(layer: GeomLayer, hitIndex: Int = 0): Map<Aes<*>, String> {
        return getDataPoints(layer, hitIndex)
            .filter { it.isSide && !it.isAxis }
            .associate { it.aes!! to it.value }
    }

    fun getTitleString(layer: GeomLayer, hitIndex: Int = 0): String? {
        val ctx = TestingPlotContext.create(layer)
        return layer.createContextualMapping()?.getTitle(index = hitIndex, ctx)
    }

    private fun getDataPoints(layer: GeomLayer, hitIndex: Int): List<DataPoint> {
        val ctx = TestingPlotContext.create(layer)
        return layer.createContextualMapping()?.getDataPoints(index = hitIndex, ctx) ?: emptyList()
    }
}
