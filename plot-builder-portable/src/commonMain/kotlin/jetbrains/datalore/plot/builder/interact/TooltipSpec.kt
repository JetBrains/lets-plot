/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.interact.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.interact.TooltipAnchor

class TooltipSpec(
    val layoutHint: TipLayoutHint,
    val title: String?,
    val lines: List<Line>,
    val fill: Color?,
    val markerColors: List<Color>,
    val isSide: Boolean,
    val anchor: TooltipAnchor? = null,
    val minWidth: Double? = null,
    val isCrosshairEnabled: Boolean = false
) {
    override fun toString(): String {
        return "TooltipSpec($layoutHint, lines=${lines.map(Line::toString)})"
    }

    class Line private constructor(val label: String?, val value: String) {
        override fun toString(): String {
            return if (label.isNullOrEmpty()) value else "${label}: $value"
        }

        companion object {
            fun withValue(value: String) = Line(label = null, value)
            fun withLabelAndValue(label: String?, value: String) = Line(label, value)
        }
    }
}