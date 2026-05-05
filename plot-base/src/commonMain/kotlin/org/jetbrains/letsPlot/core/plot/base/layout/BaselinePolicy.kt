/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.layout

import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics

// Text is positioned by two visual conventions:
// - `offsetEmBox()`: anchor against the text block.
// - `offsetForVjust()` / `offsetCapCenter()`: anchor against visible text.
//
// If the two conventions ever collapse into one, `offsetEmBox()` likely becomes a special case of `offsetForVjust()`.
object BaselinePolicy {
    fun offsetEmBox(anchor: VerticalAnchor, metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        return when (anchor) {
            VerticalAnchor.TOP -> first.ascent - EM_BOX_TOP_NUDGE * fontSize
            VerticalAnchor.CENTER -> {
                val textHeight = if (metrics.isEmpty()) fontSize else metrics.sumOf { it.height }
                first.ascent - EM_BOX_TOP_NUDGE * fontSize - textHeight / 2
            }
            VerticalAnchor.BOTTOM -> 0.0 // no callers
        }
    }

    fun offsetForVjust(vjust: Double, metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        return first.ascent - CAP_VJUST_NUDGE * vjust * fontSize
    }

    fun offsetCapCenter(metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        val textHeight = if (metrics.isEmpty()) fontSize else metrics.sumOf { it.height }
        return first.ascent - CAP_CENTER_NUDGE * fontSize - textHeight / 2
    }

    fun firstLineTopExcess(metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        return (first.ascent - fontSize).coerceAtLeast(0.0)
    }

    // Spacing inside a multi-line text block (extra space per line beyond the base font size).
    // Not for inter-shelf / inter-label spacing in axis layouts (i.e. HorizontalMultilineLabelsLayout):
    // those share the same formula by coincidence, not by concept.
    fun lineSpacing(lineHeight: Double, fontSize: Double): Double = (lineHeight - 1) * fontSize

    // Visual nudges in font-size units, derived from the font height ratios
    // provided by TextMetricsEstimator.
    private val EM_BOX_TOP_NUDGE = 1.0 - TextMetricsEstimator.baselineRatio()
    private val CAP_VJUST_NUDGE = 1.0 - TextMetricsEstimator.capHeightRatio()
    private val CAP_CENTER_NUDGE = CAP_VJUST_NUDGE / 2
}