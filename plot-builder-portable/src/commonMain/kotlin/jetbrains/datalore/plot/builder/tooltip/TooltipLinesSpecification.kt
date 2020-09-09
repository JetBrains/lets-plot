/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

open class TooltipLinesSpecification(
    val valueSources: List<ValueSource>,
    val tooltipLinePatterns: List<TooltipLine>?
) {
    companion object {
        fun emptyTooltipLines() =
            TooltipLinesSpecification(valueSources = emptyList(), tooltipLinePatterns = emptyList())

        fun defaultTooltipLines() = TooltipLinesSpecification(valueSources = emptyList(), tooltipLinePatterns = null)
    }
}