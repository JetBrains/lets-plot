/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

// `open` for Mockito tests
open class ContextualMapping(
    private val tooltipLines: List<LineSpec>,
    val tooltipAnchor: TooltipAnchor?,
    val tooltipMinWidth: Double?,
    val ignoreInvisibleTargets: Boolean,
    val hasGeneralTooltip: Boolean,
    val hasAxisTooltip: Boolean,
    val isCrosshairEnabled: Boolean,
    private val tooltipTitle: LineSpec?,
    private val formatterProvider: FormatterProvider
) {
    fun getDataPoints(index: Int): List<LineSpec.DataPoint> {
        return tooltipLines.mapNotNull { it.getDataPoint(index, formatterProvider) }
    }

    fun getTitle(index: Int): String? {
        return tooltipTitle?.getDataPoint(index, formatterProvider)?.value
    }
}