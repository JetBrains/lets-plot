/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.commons.intern.util.ArrowSupport
import org.jetbrains.letsPlot.commons.intern.util.curve
import org.jetbrains.letsPlot.commons.intern.util.padLineString
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil.ALPHA_CONTROLS_BOTH
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec.Companion.toArrowAes
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec.Type.CLOSED
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.core.plot.base.render.svg.lineString
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder.Interpolation
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape
import kotlin.math.cos
import kotlin.math.sin

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


    internal fun toClientLocation(aesMapper: (DataPointAesthetics) -> DoubleVector?): (DataPointAesthetics) -> DoubleVector? {
        return { aes ->
            aesMapper(aes)?.let { location -> toClient(location, aes) }
        }
    }

    fun createSvgElementHelper(): SvgElementHelper {
        return SvgElementHelper(::toClient)
    }

    class SvgElementHelper(
        private val toClient: (DoubleVector, DataPointAesthetics) -> DoubleVector? = { v, _ -> v }
    ) {
        private var myGeometryWithPadding: Boolean = true
        private var myNoSvg: Boolean = false
        private var myInterpolation: Interpolation? = null
        private var myArrowSpec: ArrowSpec? = null
        private var myStrokeAlphaEnabled = false
        private var myResamplingEnabled = false
        private var myResamplingPrecision = AdaptiveResampler.PIXEL_PRECISION
        private var mySpacer: Double = 0.0
        private var myDebugRendering = false

        fun setStrokeAlphaEnabled(b: Boolean) = apply { myStrokeAlphaEnabled = b }
        fun setResamplingEnabled(b: Boolean) = apply { myResamplingEnabled = b }
        fun setArrowSpec(arrowSpec: ArrowSpec?) = apply { myArrowSpec = arrowSpec }
        fun setSpacer(spacer: Double) = apply { mySpacer = spacer }
        fun setInterpolation(interpolation: Interpolation) = apply { myInterpolation = interpolation }
        fun setResamplingPrecision(precision: Double) = apply { myResamplingPrecision = precision }
        fun noSvg() = apply { myNoSvg = true }
        fun debugRendering(value: Boolean) = apply { myDebugRendering = value }
        fun geometryWithPadding(value: Boolean) = apply { myGeometryWithPadding = value }

        fun createCurve(
            start: DoubleVector,
            end: DoubleVector,
            curvature: Double,
            angle: Double,
            ncp: Int,
            p: DataPointAesthetics,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
        ): Pair<SvgNode, List<DoubleVector>>? {
            if (start == end) {
                return null
            }
            @Suppress("NAME_SHADOWING")
            val start = toClient(start, p) ?: return null

            @Suppress("NAME_SHADOWING")
            val end = toClient(end, p) ?: return null

            val lineString = curve(start, end, curvature, angle, ncp)

            val svgElement = renderSvgElement(p, lineString, strokeScaler) ?: return null
            val geometry = takeGeometry(lineString, p)

            return svgElement to geometry
        }

        fun createLine(
            start: DoubleVector,
            end: DoubleVector,
            p: DataPointAesthetics,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
        ): Pair<SvgNode, List<DoubleVector>>? {
            val lineString = createLineGeometry(start, end, p) ?: return null
            val svgElement = renderSvgElement(p, lineString, strokeScaler) ?: return null
            val geometry = takeGeometry(lineString, p)

            return svgElement to geometry
        }

        fun createSpoke(
            base: DoubleVector,
            angle: Double,
            radius: Double,
            pivot: Double,
            p: DataPointAesthetics,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth
        ): Pair<SvgNode, List<DoubleVector>>? {
            val spoke = DoubleVector(radius * cos(angle), radius * sin(angle))
            val start = base.subtract(spoke.mul(pivot))
            val end = base.add(spoke.mul(1 - pivot))

            return createLine(start, end, p, strokeScaler)
        }

        private fun createLineGeometry(
            start: DoubleVector,
            end: DoubleVector,
            aes: DataPointAesthetics,
        ): List<DoubleVector>? {
            if (myResamplingEnabled) {
                return resample(listOf(start, end), myResamplingPrecision) { toClient(it, aes) }
            } else {
                val from = toClient(start, aes) ?: return null
                val to = toClient(end, aes) ?: return null

                return listOf(from, to)
            }
        }

        private fun takeGeometry(lineString: List<DoubleVector>, p: DataPointAesthetics): List<DoubleVector> {
            return if (myGeometryWithPadding) padLineString(lineString, p, padArrow = false) else lineString
        }

        private fun renderSvgElement(
            p: DataPointAesthetics,
            lineString: List<DoubleVector>,
            strokeScaler: (DataPointAesthetics) -> Double
        ): SvgNode? {
            if (myNoSvg) return SvgGElement()

            if (lineString.isEmpty() || lineString.size == 1) return null

            val lineStringAfterPadding = padLineString(lineString, p, padArrow = true)

            val lineElement = if (lineStringAfterPadding.size == 2) {
                // Simple SvgLineElement is enough for a straight line without arrow
                SvgLineElement().apply {
                    x1().set(lineStringAfterPadding.first().x)
                    y1().set(lineStringAfterPadding.first().y)
                    x2().set(lineStringAfterPadding.last().x)
                    y2().set(lineStringAfterPadding.last().y)
                }
            } else {
                SvgPathElement().apply {
                    d().set(
                        if (myInterpolation != null) {
                            SvgPathDataBuilder()
                                .moveTo(lineStringAfterPadding.first())
                                .interpolatePoints(lineStringAfterPadding, myInterpolation!!)
                                .build()
                        } else {
                            SvgPathDataBuilder().lineString(lineStringAfterPadding).build()
                        }
                    )
                }
            }
            decorate(lineElement, p, myStrokeAlphaEnabled, strokeScaler, filled = false)

            val arrowElements = myArrowSpec?.let { arrowSpec ->
                val (startHead, endHead) = ArrowSupport.createArrowHeads(
                    lineString = lineStringAfterPadding,
                    angle = arrowSpec.angle,
                    arrowLength = arrowSpec.length,
                    onStart = arrowSpec.isOnFirstEnd,
                    onEnd = arrowSpec.isOnLastEnd,
                    closed = arrowSpec.type == CLOSED,
                    minTailLength = ArrowSupport.MIN_TAIL_LENGTH,
                    minHeadLength = ArrowSupport.MIN_HEAD_LENGTH
                )
                val startHeadSvg = renderArrowHead(startHead, p, strokeScaler)
                val endHeadSvg = renderArrowHead(endHead, p, strokeScaler)
                listOfNotNull(startHeadSvg, endHeadSvg)
            } ?: emptyList()

            val debugPoints = if (myDebugRendering) {
                lineStringAfterPadding.map {
                    SvgCircleElement(it.x, it.y, 1.0).apply {
                        fillColor().set(Color.LIGHT_GREEN)
                        strokeColor().set(Color.GREEN)
                    }
                }
            } else {
                emptyList()
            }

            return if (arrowElements.isEmpty() && debugPoints.isEmpty()) {
                lineElement
            } else {
                SvgGElement().apply {
                    children().add(lineElement)
                    children().addAll(arrowElements)
                    children().addAll(debugPoints)
                }
            }
        }

        private fun renderArrowHead(
            points: List<DoubleVector>,
            p: DataPointAesthetics,
            strokeScaler: (DataPointAesthetics) -> Double
        ): SvgNode? {
            if (points.size < 2) return null
            val arrowSpec = myArrowSpec ?: return null

            val arrowSvg = SvgPathElement().apply {
                strokeMiterLimit().set(ArrowSupport.miterLength(arrowSpec.angle, AesScaling.strokeWidth(p)))
                d().set(SvgPathDataBuilder()
                    .lineString(points)
                    .also { if (arrowSpec.type == CLOSED) it.closePath() }
                    .build()
                )
            }

            decorate(
                arrowSvg,
                arrowSpec.toArrowAes(p),
                myStrokeAlphaEnabled,
                strokeScaler,
                filled = arrowSpec.type == CLOSED
            )

            return arrowSvg
        }

        private fun padLineString(lineString: List<DoubleVector>, p: DataPointAesthetics, padArrow: Boolean): List<DoubleVector> {
            val startPadding = mySpacer + AesScaling.targetStartSize(p) + if (padArrow) arrowPadding(p, atStart = true) else 0.0
            val endPadding = mySpacer + AesScaling.targetEndSize(p) + if (padArrow) arrowPadding(p, atStart = false) else 0.0

            return padLineString(lineString, startPadding, endPadding)
        }

        private fun arrowPadding(
            aes: DataPointAesthetics,
            atStart: Boolean
        ): Double {
            val arrowSpec = myArrowSpec ?: return 0.0
            val newVer = ArrowSupport.arrowPadding(
                angle = arrowSpec.angle,
                onStart = arrowSpec.isOnFirstEnd,
                onEnd = arrowSpec.isOnLastEnd,
                atStart = atStart,
                strokeSize = AesScaling.strokeWidth(aes)
            )

            val hasArrow = if (atStart) arrowSpec.isOnFirstEnd else arrowSpec.isOnLastEnd
            if (!hasArrow) return 0.0

            return newVer
        }
    }

    companion object {
        fun decorate(
            shape: SvgShape,
            p: DataPointAesthetics,
            applyAlphaToAll: Boolean = ALPHA_CONTROLS_BOTH,
            strokeScaler: (DataPointAesthetics) -> Double = AesScaling::strokeWidth,
            filled: Boolean = true
        ) {
            AestheticsUtil.updateStroke(shape, p, applyAlphaToAll)
            if (filled) {
                AestheticsUtil.updateFill(shape, p)
            } else {
                shape.fill().set(SvgColors.NONE)
            }
            val strokeWidth = strokeScaler(p)
            shape.strokeWidth().set(strokeWidth)
            StrokeDashArraySupport.apply(shape, strokeWidth, p.lineType())
        }

        internal fun decorateSlimShape(
            shape: SvgSlimShape,
            p: DataPointAesthetics,
            applyAlphaToAll: Boolean = ALPHA_CONTROLS_BOTH
        ) {
            val stroke = p.color()!!
            val strokeAlpha = if (applyAlphaToAll) {
                // apply alpha aes
                AestheticsUtil.alpha(stroke, p)
            } else {
                // keep color's alpha
                SvgUtils.alpha2opacity(stroke.alpha)
            }

            val fill = p.fill()!!
            val fillAlpha = AestheticsUtil.alpha(fill, p)

            shape.setFill(fill, fillAlpha)
            shape.setStroke(stroke, strokeAlpha)
            shape.setStrokeWidth(AesScaling.strokeWidth(p))
        }
    }
}