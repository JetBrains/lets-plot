/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.aes.AestheticsUtil.ALPHA_CONTROLS_BOTH
import jetbrains.datalore.plot.base.render.svg.StrokeDashArraySupport
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgShape
import jetbrains.datalore.vis.svg.slim.SvgSlimShape

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
            val location = aesMapper(aes)
            if (location != null) {
                toClient(location, aes)
            } else {
                null
            }
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

        fun createLine(start: DoubleVector, end: DoubleVector, p: DataPointAesthetics): SvgLineElement? {
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
            decorate(line, p, myStrokeAlphaEnabled)
            return line
        }
    }

    companion object {

        fun decorate(node: SvgNode, p: DataPointAesthetics, applyAlphaToAll: Boolean = ALPHA_CONTROLS_BOTH) {
            if (node is SvgShape) {
                decorateShape(
                    node as SvgShape,
                    p,
                    applyAlphaToAll
                )
            }

            if (node is SvgElement) {
                val lineType = p.lineType()
                if (!(lineType.isBlank || lineType.isSolid)) {
                    StrokeDashArraySupport.apply(node, AesScaling.strokeWidth(p), lineType.dashArray)
                }
            }
        }

        private fun decorateShape(shape: SvgShape, p: DataPointAesthetics, applyAlphaToAll: Boolean) {
            AestheticsUtil.updateStroke(shape, p, applyAlphaToAll)
            AestheticsUtil.updateFill(shape, p)
            shape.strokeWidth().set(AesScaling.strokeWidth(p))
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