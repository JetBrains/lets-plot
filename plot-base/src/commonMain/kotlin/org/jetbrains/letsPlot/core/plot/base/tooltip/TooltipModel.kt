/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color

class TooltipModel(
    val placement: TooltipHint.Placement,
    val stemLength: TooltipHint.StemLength,
    val targets: List<Target>,
    val fill: Color?,
    val isSide: Boolean,
    val anchor: TooltipAnchor? = null,
    val minWidth: Double? = null,
    val isCrosshairEnabled: Boolean = false,
    val crosshairMode: CrosshairMode? = null,
) {
    val lines: List<Line> = targets.flatMap(Target::lines)
    val isMultiTarget: Boolean get() = targets.size > 1

    override fun toString(): String {
        return "TooltipModel($placement, lines=${lines.map(Line::toString)})"
    }

    class Target(
        val title: String?,
        val marker: TooltipMarker,
        val lines: List<Line>,
        val coord: DoubleVector,
        val radius: Double = 0.0
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

    companion object {
        fun forTarget(
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
        ): TooltipModel {
            return TooltipModel(
                placement = tooltipHint.placement,
                stemLength = tooltipHint.stemLength,
                targets = listOf(
                    Target(
                        title = title,
                        marker = marker,
                        lines = lines,
                        coord = tooltipHint.coord,
                        radius = tooltipHint.objectRadius
                    )
                ),
                fill = fill,
                isSide = isSide,
                anchor = anchor,
                minWidth = minWidth,
                isCrosshairEnabled = isCrosshairEnabled,
                crosshairMode = crosshairMode,
            )
        }
    }
}
