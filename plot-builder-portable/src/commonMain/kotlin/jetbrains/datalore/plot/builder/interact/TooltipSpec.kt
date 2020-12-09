/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TooltipAnchor

class TooltipSpec(
    val layoutHint: TipLayoutHint,
    lines: List<Line>,
    val fill: Color,
    val isOutlier: Boolean,
    val anchor: TooltipAnchor? = null,
    val minWidth: Double? = null
) {
    val lines: List<Line> = ArrayList(lines)

    override fun toString(): String {
        return "TooltipSpec($layoutHint, lines=${lines.map(Line::toString)})"
    }

    class Line(val label: String?, val value: String) {
        override fun toString(): String {
            return if (label.isNullOrEmpty()) value else "${label}: $value"
        }

        companion object {
            fun withValue(value: String) = Line(label = null, value = value)
            fun withLabelAndValue(label: String?, value: String) = Line(label, value)
        }
    }
}