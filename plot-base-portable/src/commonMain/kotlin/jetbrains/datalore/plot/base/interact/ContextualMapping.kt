/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

// `open` for Mockito tests
open class ContextualMapping(
    private val tooltipLines: List<TooltipLineSpec>,
    val tooltipAnchor: TooltipAnchor?,
    val tooltipMinWidth: Double?,
    val ignoreInvisibleTargets: Boolean,
    val hasGeneralTooltip: Boolean,
    val hasAxisTooltip: Boolean,
    val isCrosshairEnabled: Boolean,
    private val tooltipTitle: TooltipLineSpec?
) {
    fun getDataPoints(index: Int): List<TooltipLineSpec.DataPoint> {
        return tooltipLines.mapNotNull { it.getDataPoint(index) }
    }
    fun getTitle(index: Int): String? {
        return tooltipTitle?.getDataPoint(index)?.value
    }
}