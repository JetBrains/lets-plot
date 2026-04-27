/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement

interface GeomTargetCollector {

    fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: TooltipParams,
        tooltipPlacement: Placement = Placement.VERTICAL
    )

    fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: TooltipParams,
        tooltipPlacement: Placement = Placement.HORIZONTAL,
        tooltipAnchor: DoubleVector? = null
    )

    fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipPlacement: Placement = Placement.HORIZONTAL
    )

    fun addPolygon(
        points: List<DoubleVector>,
        index: Int,
        tooltipParams: TooltipParams,
        tooltipPlacement: Placement = Placement.CURSOR
    )

    fun withFlippedAxis(): GeomTargetCollector

    fun withYOrientation(): GeomTargetCollector

    class TooltipParams(
        val tooltipHints: Map<Aes<*>, TooltipHint> = emptyMap(),
        val stemLength: TooltipHint.StemLength = TooltipHint.StemLength.NORMAL,
        val fillColorFactory: (Int) -> Color? = { null },
        val markerColorsFactory: ((Int) -> List<Color>) = { emptyList() },
    ) {
        constructor(
            tooltipHints: Map<Aes<*>, TooltipHint> = emptyMap(),
            stemLength: TooltipHint.StemLength = TooltipHint.StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList(),
        ) : this(
            tooltipHints, stemLength, { fillColor }, { markerColors }
        )
    }
}
