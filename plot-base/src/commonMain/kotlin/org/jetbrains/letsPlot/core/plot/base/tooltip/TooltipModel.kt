/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.values.Color

class TooltipModel(
    val tooltipHint: TooltipHint,
    val title: String?,
    val blocks: List<Block>,
    val fill: Color?,
    val isSide: Boolean,
    val anchor: TooltipAnchor? = null,
    val minWidth: Double? = null,
    val isCrosshairEnabled: Boolean = false,
    val crosshairMode: CrosshairMode? = null,
) {
    val lines: List<Line> = blocks.flatMap(Block::lines)
    val marker: TooltipMarker = blocks.firstOrNull()?.marker ?: TooltipMarker.NONE

    constructor(
        tooltipHint: TooltipHint,
        title: String?,
        lines: List<Line>,
        fill: Color?,
        marker: TooltipMarker,
        isSide: Boolean,
        anchor: TooltipAnchor? = null,
        minWidth: Double? = null,
        isCrosshairEnabled: Boolean = false,
        crosshairMode: CrosshairMode? = null,
    ) : this(
        tooltipHint = tooltipHint,
        title = title,
        blocks = listOf(Block(title = null, marker = marker, lines = lines)),
        fill = fill,
        isSide = isSide,
        anchor = anchor,
        minWidth = minWidth,
        isCrosshairEnabled = isCrosshairEnabled,
        crosshairMode = crosshairMode,
    )

    override fun toString(): String {
        return "TooltipModel($tooltipHint, lines=${lines.map(Line::toString)})"
    }

    class Block(
        val title: String?,
        val marker: TooltipMarker,
        val lines: List<Line>
    )

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
