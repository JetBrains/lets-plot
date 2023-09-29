/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

class ErrorBarGeom(private val isVertical: Boolean) : GeomBase() {
    private val flipHelper = FlippableGeomHelper(isVertical)

    private fun afterRotation(aes: Aes<Double>): Aes<Double> {
        return flipHelper.getEffectiveAes(aes)
    }

    private fun afterRotation(rectangle: DoubleRectangle): DoubleRectangle {
        return flipHelper.flip(rectangle)
    }

    private fun afterRotation(segment: DoubleSegment): DoubleSegment {
        return flipHelper.flip(segment)
    }

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = ErrorBarLegendKeyElementFactory()

    override val wontRender: List<Aes<*>>
        get() {
            return listOf(Aes.Y, Aes.XMIN, Aes.XMAX, Aes.HEIGHT).map(::afterRotation)
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
        val widthAes = afterRotation(Aes.WIDTH)
        val dataPoints = GeomUtil.withDefined(aesthetics.dataPoints(), xAes, minAes, maxAes, widthAes)

        val geomHelper = GeomHelper(pos, coord, ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.ERROR_BAR, ctx)

        for (p in dataPoints) {
            val x = p[xAes]!!
            val ymin = p[minAes]!!
            val ymax = p[maxAes]!!

            val width = p[widthAes]!! * ctx.getResolution(xAes)
            val height = ymax - ymin

            val rect = DoubleRectangle(x - width / 2, ymin, width, height)
            val segments = errorBarShapeSegments(rect).map(::afterRotation)
            val g = errorBarShape(segments, p, geomHelper)
            root.add(g)
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
            val widthAes = afterRotation(Aes.WIDTH)
            if (p.defined(xAes) &&
                p.defined(minAes) &&
                p.defined(maxAes) &&
                p.defined(widthAes)
            ) {
                val x = p[xAes]!!
                val ymin = p[minAes]!!
                val ymax = p[maxAes]!!
                val width = p[widthAes]!! * ctx.getResolution(xAes)
                val height = ymax - ymin
                val rect = geomHelper.toClient(
                    afterRotation(DoubleRectangle(x - width / 2.0, ymin - height / 2.0, width, 0.0)),
                    p
                )!!
                rect
            } else {
                null
            }
        }
    }

    internal class ErrorBarLegendKeyElementFactory : LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val strokeWidth = AesScaling.strokeWidth(p)

            val width = p.width()!! * (size.x - strokeWidth)
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2
            return errorBarLegendShape(
                errorBarShapeSegments(DoubleRectangle(x, y, width, height)), p
            )
        }
    }

    companion object {
        private fun errorBarLegendShape(segments: List<DoubleSegment>, p: DataPointAesthetics): SvgGElement {
            val g = SvgGElement()
            segments.forEach { segment ->
                val shapeLine = SvgLineElement(segment.start.x, segment.start.y, segment.end.x, segment.end.y)
                GeomHelper.decorate(shapeLine, p)
                g.children().add(shapeLine)
            }
            return g
        }

        private fun errorBarShapeSegments(r: DoubleRectangle): List<DoubleSegment> {
            val center = r.left + r.width / 2
            return with(r) {
                listOf(
                    DoubleSegment(DoubleVector(left, top), DoubleVector(right, top)),
                    DoubleSegment(DoubleVector(left, bottom), DoubleVector(right, bottom)),
                    DoubleSegment(DoubleVector(center, top), DoubleVector(center, bottom))
                )
            }
        }

        private fun errorBarShape(
            segments: List<DoubleSegment>,
            p: DataPointAesthetics,
            geomHelper: GeomHelper
        ): SvgGElement {
            val g = SvgGElement()
            val elementHelper = geomHelper.createSvgElementHelper()
            elementHelper.setStrokeAlphaEnabled(true)
            segments.forEach { segment ->
                g.children().add(
                    elementHelper.createLine(segment.start, segment.end, p)!!
                )
            }
            return g
        }

        const val HANDLES_GROUPS = false
    }
}
