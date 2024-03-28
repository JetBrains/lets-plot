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
import org.jetbrains.letsPlot.core.plot.base.geom.util.FlippableGeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
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

        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(xAes) ?: continue
            val y = p.finiteOrNull(yAes) ?: continue
            val ymin = p.finiteOrNull(minAes) ?: continue
            val ymax = p.finiteOrNull(maxAes) ?: continue
            val shape = p.shape() ?: continue

            // vertical line
            val start = afterRotation(DoubleVector(x, ymin))
            val end = afterRotation(DoubleVector(x, ymax))
            val (svg) = helper.createLine(start, end, p, strokeScaler = AesScaling::lineWidth) ?: continue
            root.add(svg)

            // mid-point
            val location = geomHelper.toClient(afterRotation(DoubleVector(x, y)), p)!!
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
        fun factory(p: DataPointAesthetics) : DoubleRectangle? {
            val xAes = afterRotation(Aes.X)
            val yAes = afterRotation(Aes.Y)

            val x = p.finiteOrNull(xAes) ?: return null
            val y = p.finiteOrNull(yAes) ?: return null
            val shape = p.shape() ?: return null

            val rect = DoubleRectangle(DoubleVector(x, y), DoubleVector.ZERO)
                .let { afterRotation(it) }
                .let { geomHelper.toClient(it, p) }!!

            val shapeSize = shape.size(p, fatten)
            val strokeWidth = shape.strokeWidth(p)
            val width = shapeSize + strokeWidth
            val needToFlip = when {
                isVertical -> ctx.flipped
                else -> !ctx.flipped
            }
            return GeomUtil.extendWidth(rect, width, needToFlip)
        }

        return ::factory
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val DEF_FATTEN = 5.0
    }
}
