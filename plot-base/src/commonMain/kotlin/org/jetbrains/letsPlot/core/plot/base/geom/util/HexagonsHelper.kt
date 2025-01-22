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
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath

class HexagonsHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val geometryFactory: (DataPointAesthetics) -> List<DoubleVector>?
) : LinesHelper(pos, coord, ctx) {
    fun createSvgHexHelper(): SvgHexHelper {
        return SvgHexHelper()
    }

    inner class SvgHexHelper {
        private var onGeometry: (DataPointAesthetics, List<DoubleVector>?) -> Unit = { _, _ -> }
        private var myResamplingEnabled = false
        private var myResamplingPrecision = AdaptiveResampler.PIXEL_PRECISION

        fun setResamplingEnabled(b: Boolean) {
            myResamplingEnabled = b
        }

        fun onGeometry(handler: (DataPointAesthetics, List<DoubleVector>?) -> Unit) {
            onGeometry = handler
        }

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

                    onGeometry(p, simplified)

                    val element = LinePath.polygon(simplified)
                    decorate(element, p, true)
                    hexagons.add(element)
                } else {
                    // Correct hexagon should have 7 points, including the closing one.
                    val clientHex = hex.mapNotNull { toClient(it, p) }.takeIf { it.size == 7 } ?: continue

                    onGeometry(p, clientHex)

                    val element = LinePath.polygon(clientHex)
                    decorate(element, p, true)
                    hexagons.add(element)
                }

            }
            return hexagons
        }
    }
}