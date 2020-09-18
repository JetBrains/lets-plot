/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

class TooltipSpecification(
    val valueSources: List<ValueSource>,
    val tooltipLinePatterns: List<TooltipLine>?
) {
    companion object {
        fun withoutTooltip() = TooltipSpecification(valueSources = emptyList(), tooltipLinePatterns = emptyList())

        fun defaultTooltip() = TooltipSpecification(valueSources = emptyList(), tooltipLinePatterns = null)
    }
}