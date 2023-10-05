/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.PlotContext

// `open` for Mockito tests
open class ContextualMapping(
    private val tooltipLines: List<LineSpec>,
    val tooltipAnchor: TooltipAnchor?,
    val tooltipMinWidth: Double?,
    val ignoreInvisibleTargets: Boolean,
    val hasGeneralTooltip: Boolean,
    val hasAxisTooltip: Boolean,
    val isCrosshairEnabled: Boolean,
    private val tooltipTitle: LineSpec?
) {
    fun getDataPoints(index: Int, ctx: PlotContext): List<LineSpec.DataPoint> {
        return tooltipLines.mapNotNull { it.getDataPoint(index, ctx) }
    }

    fun getTitle(index: Int, ctx: PlotContext): String? {
        return tooltipTitle?.getDataPoint(index, ctx)?.value
    }
}