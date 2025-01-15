/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.layout

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment
import org.jetbrains.letsPlot.core.plot.builder.tooltip.layout.LayoutManager.VerticalAlignment.*

internal class VerticalAlignmentResolver(private val myVerticalSpace: DoubleSpan) {

    fun resolve(
        topPlacementRange: DoubleSpan,
        bottomPlacementRange: DoubleSpan,
        preferredPlacement: VerticalAlignment,
        cursorRange: DoubleSpan
    ): VerticalAlignment {
        val topCursorOk = !topPlacementRange.connected(cursorRange)
        val topSpaceOk = topPlacementRange in myVerticalSpace
        val bottomCursorOk = !bottomPlacementRange.connected(cursorRange)
        val bottomSpaceOk = bottomPlacementRange in myVerticalSpace
        val preferredAlignment = preferredPlacement

        val matchedRule = RULES.first {
            it.matches(
                topSpaceOk = topSpaceOk,
                topCursorOk = topCursorOk,
                bottomSpaceOk = bottomSpaceOk,
                bottomCursorOk = bottomCursorOk,
                preferredAlignment = preferredAlignment
            )
        }

        return matchedRule.resultAlignment
    }

    internal class Rule(
        val resultAlignment: VerticalAlignment,
        private val topSpaceOk: Boolean? = null,
        private val topCursorOk: Boolean? = null,
        private val bottomSpaceOk: Boolean? = null,
        private val bottomCursorOk: Boolean? = null,
        private val preferredAlignment: VerticalAlignment? = null
    ) {
        fun matches(
            topSpaceOk: Boolean,
            topCursorOk: Boolean,
            bottomSpaceOk: Boolean,
            bottomCursorOk: Boolean,
            preferredAlignment: VerticalAlignment
        ) =
            (this.bottomCursorOk?.equals(bottomCursorOk) != false) &&
                    (this.bottomSpaceOk?.equals(bottomSpaceOk) != false) &&
                    (this.topCursorOk?.equals(topCursorOk) != false) &&
                    (this.topSpaceOk?.equals(topSpaceOk) != false) &&
                    (this.preferredAlignment?.equals(preferredAlignment) != false)
    }

    companion object {
        private val RULES = listOf(
            Rule(resultAlignment = TOP, preferredAlignment = TOP, topSpaceOk = true, topCursorOk = true),
            Rule(resultAlignment = BOTTOM, preferredAlignment = BOTTOM, bottomSpaceOk = true, bottomCursorOk = true),
            Rule(resultAlignment = BOTTOM, bottomSpaceOk = true),
            Rule(resultAlignment = TOP, topSpaceOk = true),
            Rule(resultAlignment = FIT, topSpaceOk = false, bottomSpaceOk = false),
            Rule(resultAlignment = FIT)
        )
    }
}
