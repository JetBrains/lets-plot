/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font

/**
 * Box-local vertical metrics of one laid-out line box.
 *
 * These values describe the line box used for placement and baseline-to-baseline
 * stacking. They are not raw font metrics returned by a shaping or rasterization
 * engine.
 */
data class LineBoxMetrics(
    val boxHeight: Double,        // Total height of the line box.
    val topToBaseline: Double     // Distance from the line-box top to its placement baseline.
) {
    init {
        require(boxHeight >= 0.0) { "boxHeight must be non-negative." }
        require(topToBaseline >= 0.0) { "topToBaseline must be non-negative." }
        require(topToBaseline <= boxHeight) { "topToBaseline must not exceed boxHeight." }
    }

    val bottomToBaseline: Double get() = boxHeight - topToBaseline

    companion object {
        // Creates a line box whose whole height lives above the baseline.
        fun fromBoxHeight(boxHeight: Double): LineBoxMetrics {
            return LineBoxMetrics(boxHeight, boxHeight)
        }

        // Returns the default line box used for a plain-text line in the current layout model.
        fun plainText(font: Font): LineBoxMetrics {
            return fromBoxHeight(font.size.toDouble())
        }

        internal fun mergeOnBaseline(
            metrics: Collection<LineBoxMetrics>,
            defaultIfEmpty: LineBoxMetrics
        ): LineBoxMetrics {
            if (metrics.isEmpty()) return defaultIfEmpty
            return metrics.reduce { left, right ->
                val mergedTopToBaseline = maxOf(left.topToBaseline, right.topToBaseline)
                LineBoxMetrics(
                    boxHeight = mergedTopToBaseline + maxOf(left.bottomToBaseline, right.bottomToBaseline),
                    topToBaseline = mergedTopToBaseline
                )
            }
        }
    }
}
