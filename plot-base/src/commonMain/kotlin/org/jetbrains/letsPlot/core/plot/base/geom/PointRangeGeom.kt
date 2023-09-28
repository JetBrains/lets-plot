/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.legend.CompositeLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.legend.VLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg

class PointRangeGeom(private val isVertical: Boolean) : GeomBase() {
    var fattenMidPoint: Double = DEF_FATTEN
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
        get() = CompositeLegendKeyElementFactory(
            VLineLegendKeyElementFactory(),
            PointLegendKeyElementFactory(DEF_FATTEN)
        )

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.XMIN, Aes.XMAX).map(::afterRotation)
        }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.POINT_RANGE, ctx)

        val xAes = afterRotation(Aes.X)
        val yAes = afterRotation(Aes.Y)
        val minAes = afterRotation(Aes.YMIN)
        val maxAes = afterRotation(Aes.YMAX)

        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), xAes, yAes, minAes, maxAes)
        for (p in dataPoints) {
            val x = p[xAes]!!
            val y = p[yAes]!!
            val ymin = p[minAes]!!
            val ymax = p[maxAes]!!

            // vertical line
            val start = afterRotation(DoubleVector(x, ymin))
            val end = afterRotation(DoubleVector(x, ymax))
            helper.createLine(start, end, p, strokeScaler = AesScaling::lineWidth)?.let { root.add(it) }

            // mid-point
            val location = geomHelper.toClient(afterRotation(DoubleVector(x, y)), p)!!
            val shape = p.shape()!!
            val o = PointShapeSvg.create(shape, location, p, fattenMidPoint)
            root.add(wrap(o))
        }

        flipHelper.buildHints(
            listOf(minAes, maxAes),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, fattenMidPoint),
            { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )
    }

    private fun clientRectByDataPoint(
        ctx: GeomContext,
        geomHelper: GeomHelper,
        fatten: Double
    ): (DataPointAesthetics) -> DoubleRectangle? {
        return { p ->
            val xAes = afterRotation(Aes.X)
            val yAes = afterRotation(Aes.Y)
            if (p.defined(xAes) &&
                p.defined(yAes)
            ) {
                val x = p[xAes]!!
                val y = p[yAes]!!
                val shape = p.shape()!!

                val rect = geomHelper.toClient(
                    afterRotation(DoubleRectangle(DoubleVector(x, y), DoubleVector.ZERO)),
                    p
                )!!

                val shapeSize = shape.size(p, fatten)
                val strokeWidth = shape.strokeWidth(p)
                val width = shapeSize + strokeWidth
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
        const val DEF_FATTEN = 5.0
    }
}
