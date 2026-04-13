/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpec
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipAnchor
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.LinePattern
import org.jetbrains.letsPlot.core.plot.base.tooltip.text.ValueSource

data class TooltipBehavior(
    val lookupSpec: LookupSpec = LookupSpec.NONE,
    val axisAesFromFunctionKind: List<Aes<*>> = emptyList(),
    val axisTooltipEnabled: Boolean = true,
    val isCrosshairEnabled: Boolean = false,
    val ignoreInvisibleTargets: Boolean = false,
    val valueSources: List<ValueSource>,
    val tooltipLinePatterns: List<LinePattern>?,
    val anchor: TooltipAnchor?,
    val minWidth: Double?,
    val tooltipTitle: LinePattern?,
    val disableSplitting: Boolean,
    val tooltipGroup: String?,
) {
    fun useDefaultTooltips() = tooltipLinePatterns == null

    fun hideTooltips() = tooltipLinePatterns?.isEmpty() ?: false

    fun withTooltipGroup(tooltipGroup: String?): TooltipBehavior {
        return copy(tooltipGroup = tooltipGroup)
    }

    companion object {
        val NONE = TooltipBehavior(
            valueSources = emptyList(),
            tooltipLinePatterns = emptyList(),
            anchor = null,
            minWidth = null,
            tooltipTitle = null,
            disableSplitting = false,
            tooltipGroup = null,
        )

        fun defaultTooltip() = TooltipBehavior(
            valueSources = emptyList(),
            tooltipLinePatterns = null,
            anchor = null,
            minWidth = null,
            tooltipTitle = null,
            disableSplitting = false,
            tooltipGroup = null,
        )
    }
}
