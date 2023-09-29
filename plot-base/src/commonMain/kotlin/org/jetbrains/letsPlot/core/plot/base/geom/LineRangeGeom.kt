/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.max

class LineRangeGeom(private val isVertical: Boolean) : GeomBase() {
    private val flipHelper = FlippableGeomHelper(isVertical)

    private fun afterRotation(aes: Aes<Double>): Aes<Double> {
        return flipHelper.getEffectiveAes(aes)
    }

    private fun afterRotation(rectangle: DoubleRectangle): DoubleRectangle {
        return flipHelper.flip(rectangle)
    }

    private fun afterRotation(vector: DoubleVector): DoubleVector {
        return flipHelper.flip(vector)
    }

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = VLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.Y, Aes.XMIN, Aes.XMAX).map(::afterRotation)
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val xAes = afterRotation(Aes.X)
        val minAes = afterRotation(Aes.YMIN)
        val maxAes = afterRotation(Aes.YMAX)
        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), xAes, minAes, maxAes)

        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.LINE_RANGE, ctx)
        for (p in dataPoints) {
            val x = p[xAes]!!
            val ymin = p[minAes]!!
            val ymax = p[maxAes]!!
            // line
            val start = afterRotation(DoubleVector(x, ymin))
            val end = afterRotation(DoubleVector(x, ymax))
            helper.createLine(start, end, p)?.let { root.add(it) }
        }
        // tooltip
        flipHelper.buildHints(
            listOf(minAes, maxAes),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper),
            { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )
    }

    private fun clientRectByDataPoint(
        ctx: GeomContext,
        geomHelper: GeomHelper
    ): (DataPointAesthetics) -> DoubleRectangle? {
        return { p ->
            val xAes = afterRotation(Aes.X)
            val minAes = afterRotation(Aes.YMIN)
            val maxAes = afterRotation(Aes.YMAX)
            if (p.defined(xAes) &&
                p.defined(minAes) &&
                p.defined(maxAes)
            ) {
                val x = p[xAes]!!
                val ymin = p[minAes]!!
                val ymax = p[maxAes]!!
                val height = ymax - ymin

                val rect = geomHelper.toClient(
                    afterRotation(DoubleRectangle(DoubleVector(x, ymax - height / 2.0), DoubleVector.ZERO)),
                    p
                )!!
                val width = max(AesScaling.strokeWidth(p), MIN_TOOLTIP_RECTANGLE_WIDTH)
                val needToFlip = when {
                    isVertical -> ctx.flipped
                    else -> !ctx.flipped
                }
                GeomUtil.extendWidth(rect, width, needToFlip)
            } else {
                null
            }
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val MIN_TOOLTIP_RECTANGLE_WIDTH = 2.0
    }
}
