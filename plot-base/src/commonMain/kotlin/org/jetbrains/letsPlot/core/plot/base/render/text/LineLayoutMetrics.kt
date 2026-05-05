/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font

/**
 * Baseline-relative layout metrics for one rendered line.
 *
 * They describe the line slot used for layout and placement, not the font's
 * intrinsic ascent/descent reported by a text engine.
 */
data class LineLayoutMetrics(
    val ascent: Double,  // Distance from the baseline to the top of the layout slot.
    val descent: Double  // Distance from the baseline to the bottom of the layout slot.
) {
    val height: Double get() = ascent + descent

    fun mergeOnBaseline(other: LineLayoutMetrics): LineLayoutMetrics {
        return LineLayoutMetrics(
            ascent = maxOf(ascent, other.ascent),
            descent = maxOf(descent, other.descent)
        )
    }

    companion object {
        // Creates a layout slot whose full height is above the baseline.
        fun ascentOnly(height: Double): LineLayoutMetrics {
            return LineLayoutMetrics(height, 0.0)
        }

        // Returns the default layout metrics for a plain-text line.
        fun plainText(font: Font): LineLayoutMetrics {
            return ascentOnly(font.size.toDouble())
        }

        fun mergeOnBaseline(
            metrics: Collection<LineLayoutMetrics>,
            defaultIfEmpty: LineLayoutMetrics
        ): LineLayoutMetrics {
            if (metrics.isEmpty()) return defaultIfEmpty
            return metrics.reduce(LineLayoutMetrics::mergeOnBaseline)
        }
    }
}