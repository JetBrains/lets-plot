/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font

/**
 * Baseline-relative extents of one laid-out line box.
 *
 * These values describe the line box used for placement and baseline-to-baseline
 * stacking. They are not raw font metrics returned by a shaping or rasterization
 * engine.
 */
data class LineBoxMetrics(
    val ascent: Double,   // Distance from the baseline to the top of the line box.
    val descent: Double   // Distance from the baseline to the bottom of the line box.
) {
    val height: Double get() = ascent + descent

    companion object {
        // Creates a line box whose whole height lives above the baseline.
        fun ascentOnly(height: Double): LineBoxMetrics {
            return LineBoxMetrics(height, 0.0)
        }

        // Returns the default line box used for a plain-text line in the current layout model.
        fun plainText(font: Font): LineBoxMetrics {
            return ascentOnly(font.size.toDouble())
        }

        internal fun mergeOnBaseline(
            metrics: Collection<LineBoxMetrics>,
            defaultIfEmpty: LineBoxMetrics
        ): LineBoxMetrics {
            if (metrics.isEmpty()) return defaultIfEmpty
            return metrics.reduce { left, right ->
                LineBoxMetrics(
                    ascent = maxOf(left.ascent, right.ascent),
                    descent = maxOf(left.descent, right.descent)
                )
            }
        }
    }
}
