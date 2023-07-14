/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil.ALPHA_CONTROLS_BOTH
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

open class GeomHelper(
    protected val pos: PositionAdjustment,
    protected val coord: CoordinateSystem,
    internal val ctx: GeomContext
) {
    fun toClient(location: DoubleVector, p: DataPointAesthetics): DoubleVector? {
        return coord.toClient(adjust(location, p, pos, ctx))
    }

    fun toClient(x: Double, y: Double, p: DataPointAesthetics): DoubleVector? {
        val location = DoubleVector(x, y)
        return coord.toClient(adjust(location, p, pos, ctx))
    }

    fun toClient(r: DoubleRectangle, p: DataPointAesthetics): DoubleRectangle? {
        var clientRect = coord.toClient(adjust(r, p, pos, ctx))
        if (clientRect == null) return null

        // do not allow zero height or width (shape becomes invisible)
        if (clientRect.width == 0.0) {
            clientRect = DoubleRectangle(clientRect.origin.x, clientRect.origin.y, 0.1, clientRect.height)
        }
        if (clientRect.height == 0.0) {
            clientRect = DoubleRectangle(clientRect.origin.x, clientRect.origin.y, clientRect.width, 0.1)
        }
        return clientRect
    }

    private fun adjust(
        location: DoubleVector,
        p: DataPointAesthetics,
        pos: PositionAdjustment,
        ctx: GeomContext
    ): DoubleVector {
        return pos.translate(location, p, ctx)
    }

    fun toClientPoint(
        p: DataPointAesthetics,
        aesMapper: (DataPointAesthetics) -> DoubleVector?
    ): DoubleVector? {
        val location = aesMapper(p) ?: return null
        return toClient(location, p)
    }

    internal fun toClientRect(
        p: DataPointAesthetics,
        aesMapper: (DataPointAesthetics) -> DoubleRectangle?
    ): DoubleRectangle? {
        val r = aesMapper(p) ?: return null
        return toClient(r, p)
    }

    private fun adjust(
        r: DoubleRectangle,
        p: DataPointAesthetics,
        pos: PositionAdjustment,
        ctx: GeomContext
    ): DoubleRectangle {
        val leftTop = pos.translate(r.origin, p, ctx)
        val rightBottom = pos.translate(r.origin.add(r.dimension), p, ctx)
        return DoubleRectangle.span(leftTop, rightBottom)
    }

    protected fun project(
        dataPoints: Iterable<DataPointAesthetics>,
        projection: (DataPointAesthetics) -> DoubleVector?
    ): List<DoubleVector> {
        val points = ArrayList<DoubleVector>()
        for (p in dataPoints) {
            val location = projection(p)
            if (location != null) {
                val pp = toClient(location, p)
                if (pp != null) {
                    points.add(pp)
                }
            }
        }
        return points
    }

    internal fun toClientLocation(aesMapper: (DataPointAesthetics) -> DoubleVector?): (DataPointAesthetics) -> DoubleVector? {
        return { aes ->
            aesMapper(aes)?.let { location -> toClient(location, aes) }
        }
    }

    fun createSvgElementHelper(): SvgElementHelper {
        return SvgElementHelper()
    }

    inner class SvgElementHelper {
        private var myStrokeAlphaEnabled = false

        fun setStrokeAlphaEnabled(b: Boolean) {
            myStrokeAlphaEnabled = b
        }

        fun createLine(
            start: DoubleVector, end: DoubleVector,
            p: DataPointAesthetics,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
        ): SvgLineElement? {
            @Suppress("NAME_SHADOWING")
            val start = toClient(start, p)
            if (start == null) return null
            @Suppress("NAME_SHADOWING")
            val end = toClient(end, p)
            if (end == null) return null

            val line = SvgLineElement(
                start.x, start.y,
                end.x, end.y
            )
            decorate(line, p, myStrokeAlphaEnabled, strokeScaler)
            return line
        }
    }

    companion object {

        fun decorate(
            node: SvgNode,
            p: DataPointAesthetics,
            applyAlphaToAll: Boolean = ALPHA_CONTROLS_BOTH,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
        ) {
            if (node is SvgShape) {
                decorateShape(
                    node as SvgShape,
                    p,
                    applyAlphaToAll,
                    strokeScaler
                )
            }

            if (node is SvgElement) {
                val lineType = p.lineType()
                if (!(lineType.isBlank || lineType.isSolid)) {
                    StrokeDashArraySupport.apply(node, strokeScaler(p), lineType.dashArray)
                }
            }
        }

        private fun decorateShape(
            shape: SvgShape,
            p: DataPointAesthetics,
            applyAlphaToAll: Boolean,
            strokeScaler: (DataPointAesthetics) -> Double
        ) {
            AestheticsUtil.updateStroke(shape, p, applyAlphaToAll)
            AestheticsUtil.updateFill(shape, p)
            shape.strokeWidth().set(strokeScaler(p))
        }

        internal fun decorateSlimShape(shape: SvgSlimShape, p: DataPointAesthetics) {
            val stroke = p.color()!!
            val strokeAlpha = AestheticsUtil.alpha(stroke, p)

            val fill = p.fill()!!
            val fillAlpha = AestheticsUtil.alpha(fill, p)

            shape.setFill(fill, fillAlpha)
            shape.setStroke(stroke, strokeAlpha)
            shape.setStrokeWidth(AesScaling.strokeWidth(p))
        }
    }
}