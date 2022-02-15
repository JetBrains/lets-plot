/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.TooltipAnchor

class TooltipSpecification(
    val valueSources: List<ValueSource>,
    val tooltipLinePatterns: List<TooltipLine>?,
    val tooltipProperties: TooltipProperties,
    val tooltipTitle: TooltipLine?
) {
    class TooltipProperties(
        val anchor: TooltipAnchor?,
        val minWidth: Double?
    ) {
        companion object {
            val NONE = TooltipProperties(
                anchor = null,
                minWidth = null
            )
        }
    }

    fun useDefaultTooltips() = tooltipLinePatterns == null

    fun hideTooltips() = tooltipLinePatterns?.isEmpty() ?: false

    companion object {
        fun withoutTooltip() = TooltipSpecification(
            valueSources = emptyList(),
            tooltipLinePatterns = emptyList(),
            tooltipProperties = TooltipProperties.NONE,
            tooltipTitle = null
        )

        fun defaultTooltip() = TooltipSpecification(
            valueSources = emptyList(),
            tooltipLinePatterns = null,
            tooltipProperties = TooltipProperties.NONE,
            tooltipTitle = null
        )
    }
}