/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.FlippableGeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.toLocation
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
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
        val yMinAes = afterRotation(Aes.YMIN)
        val yMaxAes = afterRotation(Aes.YMAX)

        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.LINE_RANGE, ctx)
        for (p in aesthetics.dataPoints()) {
            val start = p.toLocation(xAes, yMinAes)?.let(::afterRotation) ?: continue
            val end = p.toLocation(xAes, yMaxAes)?.let(::afterRotation) ?: continue

            helper.createLine(start, end, p)?.let { (svgElement, _) -> root.add(svgElement) }
        }
        // tooltip
        flipHelper.buildHints(
            listOf(yMinAes, yMaxAes),
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
        fun factory(p: DataPointAesthetics): DoubleRectangle? {
            val xAes = afterRotation(Aes.X)
            val yMinAes = afterRotation(Aes.YMIN)
            val yMaxAes = afterRotation(Aes.YMAX)

            val x = p.finiteOrNull(xAes) ?: return null
            val ymin = p.finiteOrNull(yMinAes) ?: return null
            val ymax = p.finiteOrNull(yMaxAes) ?: return null

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

            return GeomUtil.extendWidth(rect, width, needToFlip)
        }

        return ::factory
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val MIN_TOOLTIP_RECTANGLE_WIDTH = 2.0
    }
}
