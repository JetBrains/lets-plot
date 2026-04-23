/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font

data class LineMetrics(
    val ascent: Double,  // distance from baseline up to line top
    val descent: Double  // distance from baseline down to line bottom
) {
    val height: Double get() = ascent + descent

    fun mergeOnBaseline(other: LineMetrics): LineMetrics {
        return LineMetrics(
            ascent = maxOf(ascent, other.ascent),
            descent = maxOf(descent, other.descent)
        )
    }

    companion object {
        fun ascentOnly(height: Double): LineMetrics {
            return LineMetrics(height, 0.0)
        }

        fun plainText(font: Font): LineMetrics {
            return ascentOnly(font.size.toDouble())
        }

        fun mergeOnBaseline(
            metrics: Collection<LineMetrics>,
            defaultIfEmpty: LineMetrics
        ): LineMetrics {
            if (metrics.isEmpty()) return defaultIfEmpty
            return metrics.reduce(LineMetrics::mergeOnBaseline)
        }
    }
}