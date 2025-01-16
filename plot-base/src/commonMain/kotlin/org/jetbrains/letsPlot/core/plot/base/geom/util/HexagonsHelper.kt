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
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup

class HexagonsHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val geometryFactory: (DataPointAesthetics) -> List<DoubleVector>?
) : GeomHelper(pos, coord, ctx) {
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

        fun createSlimHexagons(): SvgSlimGroup {
            val pointCount = myAesthetics.dataPointCount()
            val group = SvgSlimElements.g(pointCount)

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

                    val slimShape = SvgSlimElements.path(SvgPathDataBuilder().lineString(simplified).build())
                    decorateSlimShape(slimShape, p)
                    slimShape.appendTo(group)
                } else {
                    val clientHex = hex.mapNotNull { toClient(it, p) }.takeIf { it.size == 6 } ?: continue

                    onGeometry(p, clientHex)

                    val slimShape = SvgSlimElements.path(SvgPathDataBuilder().lineString(clientHex).build())
                    decorateSlimShape(slimShape, p)
                    slimShape.appendTo(group)
                }

            }
            return group
        }
    }
}