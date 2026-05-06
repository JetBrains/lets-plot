/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.layout

import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.LineLayoutMetrics

// Text is positioned by two visual conventions:
// - offsetEmBoxTop(): anchor against the em-box top.
// - offsetCap() and offsetCapForVjust(): anchor against visible text.
object BaselinePolicy {
    fun offsetEmBoxTop(metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        return first.ascent - EM_BOX_TOP_NUDGE * fontSize
    }

    fun offsetCapForVjust(vjust: Double, metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        return first.ascent - CAP_VJUST_NUDGE * vjust * fontSize
    }

    fun offsetCap(anchor: VerticalAnchor, metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val textHeight = if (metrics.isEmpty()) fontSize else metrics.sumOf { it.height }
        return when (anchor) {
            VerticalAnchor.TOP -> offsetCapForVjust(1.0, metrics, fontSize)
            VerticalAnchor.CENTER -> offsetCapForVjust(0.5, metrics, fontSize) - textHeight / 2
            VerticalAnchor.BOTTOM -> offsetCapForVjust(0.0, metrics, fontSize) - textHeight // not currently in use
        }
    }

    fun firstLineTopExcess(metrics: List<LineLayoutMetrics>, fontSize: Double): Double {
        val first = metrics.firstOrNull() ?: LineLayoutMetrics.ascentOnly(fontSize)
        return (first.ascent - fontSize).coerceAtLeast(0.0)
    }

    // Spacing inside a multi-line text block (extra space per line beyond the base font size).
    // Not for inter-shelf / inter-label spacing in axis layouts (i.e. HorizontalMultilineLabelsLayout):
    // those share the same formula by coincidence, not by concept.
    fun lineSpacing(lineHeight: Double, fontSize: Double): Double = (lineHeight - 1) * fontSize

    // Visual nudges in font-size units, derived from the font height ratios provided by TextMetricsEstimator.
    private val EM_BOX_TOP_NUDGE = 1.0 - TextMetricsEstimator.baselineRatio()
    private val CAP_VJUST_NUDGE = 1.0 - TextMetricsEstimator.capHeightRatio()
}