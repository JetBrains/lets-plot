/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.TooltipAnchor

class TooltipSpecification(
    val valueSources: List<ValueSource>,
    val tooltipLinePatterns: List<TooltipLine>?,
    val tooltipProperties: TooltipProperties
) {
    class TooltipProperties(
        val anchor: TooltipAnchor?,
        val minWidth: Double?,
        val color: Color?
    ) {
        companion object {
            val NONE = TooltipProperties(
                anchor = null,
                minWidth = null,
                color = null
            )
        }
    }

    companion object {
        fun withoutTooltip() = TooltipSpecification(
            valueSources = emptyList(),
            tooltipLinePatterns = emptyList(),
            tooltipProperties = TooltipProperties.NONE
        )

        fun defaultTooltip() = TooltipSpecification(
            valueSources = emptyList(),
            tooltipLinePatterns = null,
            tooltipProperties = TooltipProperties.NONE
        )
    }
}