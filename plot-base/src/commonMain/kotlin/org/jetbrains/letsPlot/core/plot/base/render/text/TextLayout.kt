/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

/**
 * Vertical layout data for a rendered multi-line text block.
 */
class TextLayout internal constructor(
    internal val lineMetrics: List<LineLayoutMetrics>,
    internal val lineInterval: Double = 0.0,
) {
    init {
        require(lineMetrics.isNotEmpty()) { "TextLayout must contain at least one line." }
    }

    val firstLineMetrics: LineLayoutMetrics get() = lineMetrics.first()

    val maxLineHeight: Double get() = lineMetrics.maxOf(LineLayoutMetrics::height)

    val totalHeight: Double
        get() = lineMetrics.sumOf(LineLayoutMetrics::height) + lineInterval * (lineMetrics.size - 1)

    companion object {
        fun uniform(
            lineCount: Int,
            lineMetrics: LineLayoutMetrics,
            lineInterval: Double = 0.0
        ): TextLayout {
            require(lineCount > 0) { "TextLayout.uniform() requires at least one line." }
            return fromLineMetrics(List(lineCount) { lineMetrics }, lineInterval)
        }

        fun fromLineMetrics(
            lineMetrics: List<LineLayoutMetrics>,
            lineInterval: Double = 0.0
        ): TextLayout {
            return TextLayout(lineMetrics, lineInterval)
        }
    }
}