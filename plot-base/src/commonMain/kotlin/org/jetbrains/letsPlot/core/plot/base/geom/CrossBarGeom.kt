/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsDefaults
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape

class CrossBarGeom(
    private val isVertical: Boolean
) : GeomBase() {

    private val flipHelper = FlippableGeomHelper(isVertical)
    var fattenMidline: Double = 2.5

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.XMIN, Aes.XMAX).map(::afterRotation)
        }

    override fun updateAestheticsDefaults(aestheticDefaults: AestheticsDefaults): AestheticsDefaults {
        return if (isVertical) {
            aestheticDefaults.with(Aes.Y, Double.NaN) // The middle bar is optional
        } else {
            aestheticDefaults.with(Aes.X, Double.NaN)
        }
    }

    private fun afterRotation(aes: Aes<Double>): Aes<Double> {
        return flipHelper.getEffectiveAes(aes)
    }

    private fun afterRotation(rectangle: DoubleRectangle): DoubleRectangle {
        return flipHelper.flip(rectangle)
    }

    private fun afterRotation(vector: DoubleVector): DoubleVector {
        return flipHelper.flip(vector)
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        BoxHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            rectFactory = clientRectByDataPoint(ctx, geomHelper, isHintRect = false)
        )
        buildMidlines(root, aesthetics, ctx, geomHelper, fatten = fattenMidline)
        // tooltip
        flipHelper.buildHints(
            listOf(Aes.YMIN, Aes.YMAX).map(::afterRotation),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = true),
            { HintColorUtil.colorWithAlpha(it) }
        )
    }

    private fun clientRectByDataPoint(
        ctx: GeomContext,
        geomHelper: GeomHelper,
        isHintRect: Boolean
    ): (DataPointAesthetics) -> DoubleRectangle? {
        return { p ->
            val xAes = afterRotation(Aes.X)
            val yAes = afterRotation(Aes.Y)
            val minAes = afterRotation(Aes.YMIN)
            val maxAes = afterRotation(Aes.YMAX)
            val sizeAes = Aes.WIDTH // do not flip as height is not defined for CrossBarGeom
            val rect = if (!isHintRect &&
                p.defined(xAes) &&
                p.defined(minAes) &&
                p.defined(maxAes) &&
                p.defined(sizeAes)
            ) {
                val x = p[xAes]!!
                val ymin = p[minAes]!!
                val ymax = p[maxAes]!!
                val width = p[sizeAes]!! * ctx.getResolution(xAes)
                val origin = DoubleVector(x - width / 2, ymin)
                val dimensions = DoubleVector(width, ymax - ymin)
                DoubleRectangle(origin, dimensions)
            } else if (isHintRect &&
                p.defined(xAes) &&
                p.defined(yAes) &&
                p.defined(sizeAes)
            ) {
                val x = p[xAes]!!
                val y = p[yAes]!!
                val width = p[sizeAes]!! * ctx.getResolution(xAes)
                val origin = DoubleVector(x - width / 2, y)
                val dimensions = DoubleVector(width, 0.0)
                DoubleRectangle(origin, dimensions)
            } else {
                null
            }
            rect?.let { geomHelper.toClient(afterRotation(it), p) }
        }
    }

    private fun buildMidlines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        fatten: Double
    ) {
        val elementHelper = geomHelper.createSvgElementHelper()
        val xAes = afterRotation(Aes.X)
        val yAes = afterRotation(Aes.Y)
        val sizeAes = Aes.WIDTH // do not flip as height is not defined for CrossBarGeom
        for (p in GeomUtil.withDefined(
            aesthetics.dataPoints(),
            xAes,
            yAes,
            sizeAes
        )) {
            val x = p[xAes]!!
            val middle = p[yAes]!!
            val width = p[sizeAes]!! * ctx.getResolution(xAes)

            val line = elementHelper.createLine(
                afterRotation(DoubleVector(x - width / 2, middle)),
                afterRotation(DoubleVector(x + width / 2, middle)),
                p
            )!!

            // TODO: use strokeScale in createLine() function
            // adjust thickness
            require(line is SvgShape)
            val thickness = line.strokeWidth().get()!!
            line.strokeWidth().set(thickness * fatten)

            root.add(line)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
        private val LEGEND_FACTORY = BoxHelper.legendFactory(false)
    }
}
