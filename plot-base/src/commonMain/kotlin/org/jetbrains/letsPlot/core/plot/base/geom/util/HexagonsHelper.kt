/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.core.commons.geometry.PolylineSimplifier
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil.createColorMarkerMapper
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.CURSOR_TOOLTIP

class HexagonsHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val geometryFactory: (DataPointAesthetics) -> List<DoubleVector>?
) : LinesHelper(pos, coord, ctx) {
    private var myResamplingPrecision = AdaptiveResampler.PIXEL_PRECISION

    fun createHexagons(): List<LinePath> {
        val pointCount = myAesthetics.dataPointCount()
        val hexagons: MutableList<LinePath> = mutableListOf()

        for (index in 0 until pointCount) {
            val p = myAesthetics.dataPointAt(index)
            val hex = geometryFactory(p) ?: continue

            if (myResamplingEnabled) {
                val polyHex = resample(
                    precision = myResamplingPrecision,
                    points = hex
                ) { toClient(it, p) }

                // Resampling of a tiny hexagon still can produce a very small polygon - simplify it.
                val simplified = PolylineSimplifier.douglasPeucker(polyHex).setWeightLimit(PolylineSimplifier.DOUGLAS_PEUCKER_PIXEL_THRESHOLD).points.let {
                    if (it.size != 1) {
                        println("HexagonsHelper: expected a single path, but got ${it.size}")
                    }

                    it.firstOrNull() ?: emptyList()
                }

                val element = LinePath.polygon(simplified)
                decorate(element, p, true)
                hexagons.add(element)

                createTooltips(p, simplified)
            } else {
                // Correct hexagon should have 7 points, including the closing one.
                val clientHex = hex.mapNotNull { toClient(it, p) }.takeIf { it.size == 7 } ?: continue

                val element = LinePath.polygon(clientHex)
                decorate(element, p, true)
                hexagons.add(element)

                createTooltips(p, clientHex)
            }

        }
        return hexagons
    }

    private fun createTooltips(p: DataPointAesthetics, hex: List<DoubleVector>) {
        ctx.targetCollector.addPolygon(
            hex,
            p.index(),
            GeomTargetCollector.TooltipParams(
                markerColors = createColorMarkerMapper(null, ctx)(p)
            ),
            tooltipKind = CURSOR_TOOLTIP
        )
    }
}