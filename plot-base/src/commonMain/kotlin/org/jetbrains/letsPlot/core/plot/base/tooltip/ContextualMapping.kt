/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LineSpec
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource

// `open` for Mockito tests
open class ContextualMapping(
    private val tooltipLines: List<LinePattern>,
    val tooltipAnchor: TooltipAnchor?,
    val tooltipMinWidth: Double?,
    val ignoreInvisibleTargets: Boolean,
    val isCrosshairEnabled: Boolean,
    val tooltipGroup: String?,
    private val tooltipTitle: LineSpec?
) {
    val hasGeneralTooltip = tooltipLines.any { line ->
        line.fields.none(ValueSource::isSide)
    }

    val hasAxisTooltip = tooltipLines.any { line ->
        line.fields.any(ValueSource::isAxis)
    }

    fun getDataPoints(index: Int, ctx: PlotContext): List<LineSpec.DataPoint> {
        return tooltipLines.mapNotNull { it.getDataPoint(index, ctx) }
    }

    fun getTitle(index: Int, ctx: PlotContext): String? {
        return tooltipTitle?.getDataPoint(index, ctx)?.value
    }
}