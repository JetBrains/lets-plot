/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

/**
 * Immutable vertical layout for a rendered multi-line text block.
 *
 * The layout owns the line boxes and the baseline-to-baseline spacing policy for
 * the block. Placement code should consume this object directly instead of
 * reconstructing text geometry from font size heuristics.
 */
class TextBlockLayout internal constructor(
    val lineBoxes: List<LineBoxMetrics>,
    val lineSpacing: Double = 0.0,
) {
    init {
        require(lineBoxes.isNotEmpty()) { "TextBlockLayout must contain at least one line box." }
    }

    val firstLineBox: LineBoxMetrics get() = lineBoxes.first()

    val baselineOffsets: List<Double>
        get() = lineBoxes
            .zipWithNext { prev, next -> prev.bottomToBaseline + next.topToBaseline + lineSpacing }
            .runningFold(0.0, Double::plus)

    val baselineSpan: Double get() = baselineOffsets.last()

    val blockHeight: Double
        get() = lineBoxes.sumOf(LineBoxMetrics::boxHeight) + lineSpacing * (lineBoxes.size - 1)

    companion object {
        fun uniform(
            lineCount: Int,
            lineMetrics: LineBoxMetrics,
            lineInterval: Double = 0.0
        ): TextBlockLayout {
            require(lineCount > 0) { "TextBlockLayout.uniform() requires at least one line." }
            return TextBlockLayout(lineBoxes = List(lineCount) { lineMetrics }, lineSpacing = lineInterval)
        }

        fun fromLineBoxes(
            lineBoxes: List<LineBoxMetrics>,
            lineSpacing: Double = 0.0
        ): TextBlockLayout {
            return TextBlockLayout(lineBoxes = lineBoxes, lineSpacing = lineSpacing)
        }
    }
}
