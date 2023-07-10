/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind

interface GeomTargetCollector {

    fun addPoint(
        index: Int,
        point: DoubleVector,
        radius: Double,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.VERTICAL_TOOLTIP
    )

    fun addRectangle(
        index: Int,
        rectangle: DoubleRectangle,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.HORIZONTAL_TOOLTIP
    )

    fun addPath(
        points: List<DoubleVector>,
        localToGlobalIndex: (Int) -> Int,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.HORIZONTAL_TOOLTIP
    )

    fun addPolygon(
        points: List<DoubleVector>,
        index: Int,
        tooltipParams: TooltipParams,
        tooltipKind: Kind = Kind.CURSOR_TOOLTIP
    )

    fun withFlippedAxis(): GeomTargetCollector

    fun withYOrientation(): GeomTargetCollector

    class TooltipParams(
        val tipLayoutHints: Map<Aes<*>, TipLayoutHint> = emptyMap(),
        val stemLength: TipLayoutHint.StemLength = TipLayoutHint.StemLength.NORMAL,
        val fillColorFactory: (Int) -> Color? = { null },
        val markerColorsFactory: ((Int) -> List<Color>) = { emptyList() },
    ) {
        constructor(
            tipLayoutHints: Map<Aes<*>, TipLayoutHint> = emptyMap(),
            stemLength: TipLayoutHint.StemLength = TipLayoutHint.StemLength.NORMAL,
            fillColor: Color? = null,
            markerColors: List<Color> = emptyList(),
        ) : this(
            tipLayoutHints, stemLength, { fillColor }, { markerColors }
        )
    }
}
