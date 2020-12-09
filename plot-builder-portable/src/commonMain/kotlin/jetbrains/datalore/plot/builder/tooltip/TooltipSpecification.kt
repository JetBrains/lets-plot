/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.TooltipAnchor

class TooltipSpecification(
    val valueSources: List<ValueSource>,
    val tooltipLinePatterns: List<TooltipLine>?
) {
    private var myTooltipAnchor: TooltipAnchor? = null
    private var myTooltipMinWidth: Double? = null

    fun getTooltipAnchor(): TooltipAnchor? = myTooltipAnchor

    fun setTooltipAnchor(tooltipAnchor: TooltipAnchor?): TooltipSpecification {
        myTooltipAnchor = tooltipAnchor
        return this
    }

    fun getTooltipMinWidth(): Double? = myTooltipMinWidth

    fun setTooltipMinWidth(tooltipMinWidth: Double?): TooltipSpecification {
        myTooltipMinWidth = tooltipMinWidth
        return this
    }

    companion object {
        fun withoutTooltip() = TooltipSpecification(valueSources = emptyList(), tooltipLinePatterns = emptyList())

        fun defaultTooltip() = TooltipSpecification(valueSources = emptyList(), tooltipLinePatterns = null)
    }
}