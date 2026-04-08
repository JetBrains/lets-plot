/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.render

import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet

object TooltipCss {
    const val TOOLTIP_TEXT = "tooltip-text"
    const val TOOLTIP_TITLE = "tooltip-title"
    const val TOOLTIP_LABEL = "tooltip-label"
    const val AXIS_TOOLTIP_TEXT = "axis-tooltip-text"

    fun axisTextClass(axis: String): String = "$AXIS_TOOLTIP_TEXT-$axis"

    fun isTooltipTextClass(className: String): Boolean {
        return className == TOOLTIP_TEXT ||
                className == TOOLTIP_TITLE ||
                className == TOOLTIP_LABEL ||
                className == axisTextClass("x") ||
                className == axisTextClass("y")
    }

    fun generateScopedCss(styleSheet: StyleSheet, scopedId: String): String {
        return buildString {
            appendLine("text {")
            appendLine("  text-rendering: optimizeLegibility;")
            appendLine("}")
            scopedClasses.forEach { className ->
                append(styleSheet.toCSS(className, scopedId))
            }
        }
    }

    private val scopedClasses = listOf(
        TOOLTIP_TEXT,
        TOOLTIP_TITLE,
        TOOLTIP_LABEL,
        axisTextClass("x"),
        axisTextClass("y")
    )
}
