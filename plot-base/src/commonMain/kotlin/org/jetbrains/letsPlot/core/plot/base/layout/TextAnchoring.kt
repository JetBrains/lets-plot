/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.layout

import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout

// Returns the y-offset from a chosen anchor to the SVG baseline, for two
// anchoring conventions:
// - offsetEmBoxTop: anchor against the em-box top (the layout slot).
// - offsetCap:      anchor against the visible cap-height.
object TextAnchoring {
    internal fun offsetEmBoxTop(textLayout: TextBlockLayout, fontSize: Double): Double {
        val first = textLayout.firstLineBox
        return first.topToBaseline - EM_BOX_TOP_NUDGE * fontSize
    }

    internal fun offsetCap(vjust: Double, textLayout: TextBlockLayout, fontSize: Double): Double {
        val first = textLayout.firstLineBox
        val totalHeight = textLayout.blockHeight
        return first.topToBaseline + (vjust - 1) * totalHeight - CAP_VJUST_NUDGE * vjust * fontSize
    }

    fun offsetCap(anchor: VerticalAnchor, textLayout: TextBlockLayout, fontSize: Double): Double {
        val vjust = when (anchor) {
            VerticalAnchor.TOP -> 1.0
            VerticalAnchor.CENTER -> 0.5
            VerticalAnchor.BOTTOM -> 0.0
        }
        return offsetCap(vjust, textLayout, fontSize)
    }

    // Visual nudges in font-size units, derived from the font height ratios provided by TextMetricsEstimator.
    private val EM_BOX_TOP_NUDGE = 1.0 - TextMetricsEstimator.baselineRatio()
    private val CAP_VJUST_NUDGE = 1.0 - TextMetricsEstimator.capHeightRatio()
}
