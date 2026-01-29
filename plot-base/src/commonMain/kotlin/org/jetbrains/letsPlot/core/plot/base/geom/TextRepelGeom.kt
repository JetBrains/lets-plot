/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling.POINT_UNIT_SIZE
import org.jetbrains.letsPlot.core.plot.base.geom.legend.CompositeLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.legend.TextRepelSegmentLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleCircle
import org.jetbrains.letsPlot.core.plot.base.geom.repel.LabelForceLayout
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle.Companion.savedNormalize
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDouble
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

open class TextRepelGeom: TextGeom() {
    var seed: Long? = null
    var maxIter: Int? = null
    var maxTime: Double? = null
    var direction: LabelForceLayout.Direction? = null
    var pointPadding: Double? = null
    var boxPadding: Double? = null
    var maxOverlaps: Int? = null
    var minSegmentLength: Double? = null
    var arrowSpec: ArrowSpec? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = CompositeLegendKeyElementFactory(
            TextLegendKeyElementFactory(),
            TextRepelSegmentLegendKeyElementFactory()
        )

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {

        fun toClient(point: DoubleVector, @Suppress("UNUSED_PARAMETER") dp: DataPointAesthetics): DoubleVector? {
            return coord.toClient(point)
        }

        val textHelper = TextHelper(aesthetics, pos, coord, ctx, formatter, naValue, sizeUnit, checkOverlap, ::coordOrNull, ::objectRectangle, ::componentFactory)
        val svgHelper = GeomHelper.SvgElementHelper(::toClient)
            .setStrokeAlphaEnabled(true)
            .setArrowSpec(arrowSpec)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, ctx)
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center
        val bounds = DoubleRectangle(DoubleVector.ZERO, ctx.getContentBounds().dimension)

        val boxes = HashMap<Int, TransformedRectangle>()
        val circles = HashMap<Int, DoubleCircle>()
        val hjusts = HashMap<Int, Double>()
        val vjusts = HashMap<Int, Double>()
        val boxPaddings = HashMap<Int, Double>()

        for (dp in aesthetics.dataPoints()) {
            val point = dp.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
            val loc = textHelper.toClient(point, dp) ?: continue

            val pointLocation = coord.toClient(point) ?: continue
            if (!bounds.contains(pointLocation)) continue

            val text = textHelper.toString(dp.label())
            if (text.isEmpty()) continue

            val pointDp = toPointAes(dp)
            val shape = pointDp.shape()!!
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, POINT_UNIT_SIZE)
            val pointRadius = (shape.size(pointDp, sizeUnitRatio) + shape.strokeWidth(pointDp)) / 2
            circles[dp.index()] = DoubleCircle(pointLocation, pointRadius + pointPadding(sizeUnitRatio))

            val hjust = TextUtil.hAnchor(dp, loc, aesBoundsCenter).toDouble()
            val vjust = TextUtil.vAnchor(dp, loc, aesBoundsCenter).toDouble()

            val box = TransformedRectangle(textHelper.getRect(dp, loc, text, 1.0 , ctx, aesBoundsCenter))

            boxes[dp.index()] = box
            hjusts[dp.index()] = hjust
            vjusts[dp.index()] = vjust
            boxPaddings[dp.index()] = boxPadding(sizeUnitRatio)
        }

        val replacer = LabelForceLayout(
            boxes,
            circles,
            hjusts,
            vjusts,
            boxPaddings,
            bounds = bounds,
            maxOverlaps = maxOverlaps ?: 10,
            seed = seed,
            maxIter = maxIter ?: 2000,
            maxTime = maxTime ?: 5000.0,
            direction = direction ?: LabelForceLayout.Direction.BOTH
        )

        val results = replacer.doLayout()

        for (i in results.indices) {
            val result = results[i]

            if (result.hidden) {
                continue
            }

            val dp = aesthetics.dataPointAt(result.dpIndex)
            val point = dp.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
            val pointLocation = coord.toClient(point) ?: continue
            val text = textHelper.toString(dp.label())

            val tc = componentFactory(toLabelAes(dp), result.position, text, 1.0, ctx, aesBoundsCenter)
            root.add(tc)

            val pointDp = toPointAes(dp)
            val shape = pointDp.shape()!!
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, POINT_UNIT_SIZE)
            val pointRadius = (shape.size(pointDp, sizeUnitRatio) + shape.strokeWidth(pointDp)) / 2

            val segmentLocation = getSegmentLocation(pointLocation, pointRadius, result.box, sizeUnitRatio)
            val segment = getSegment(segmentLocation, coord)

            if (segment != null) {
                root.add(buildSegmentComponent(TextHelper.toSegmentAes(dp), segment, svgHelper))
            }

            targetCollector.addPoint(
                dp.index(),
                result.position,
                 AesScaling.textSize(dp) / 2,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(dp)
                ),
                TipLayoutHint.Kind.CURSOR_TOOLTIP
            )
        }
    }

    private fun buildSegmentComponent(
        dp: DataPointAesthetics,
        segment: DoubleSegment,
        svgHelper: GeomHelper.SvgElementHelper
    ): SvgGElement {
        val g = SvgGElement()

        val (svg, _) = svgHelper.createLine(segment, dp)!!

        g.children().add(svg)

        return g
    }

    private fun getSegmentLocation(pointLocation: DoubleVector, pointRadius: Double, rect: TransformedRectangle, scale: Double): DoubleSegment? {
        val locEnd = rect.shortestSegmentToRectangleEdgeCenter(pointLocation)?.end ?: return null

        val locStart = pointLocation.add((locEnd.subtract(pointLocation).savedNormalize().mul(pointRadius)))

        if (locStart.subtract(locEnd).length() < minSegmentLength(scale)) return null

        return DoubleSegment(locStart, locEnd)
    }

    private fun getSegment(segmentLocation: DoubleSegment?, coord: CoordinateSystem): DoubleSegment? {
        if (segmentLocation == null) return null
        val start = coord.fromClient(segmentLocation.start) ?: return null
        val end = coord.fromClient(segmentLocation.end) ?: return null

        return DoubleSegment(start, end)
    }

    private fun boxPadding(scale: Double): Double {
        return (boxPadding ?: 0.0) * POINT_UNIT_SIZE * scale
    }

    private fun pointPadding(scale: Double): Double {
        return (pointPadding ?: 0.0) * POINT_UNIT_SIZE * scale
    }

    private fun minSegmentLength(scale: Double): Double {
        return (minSegmentLength ?: 0.0) * POINT_UNIT_SIZE * scale
    }

    companion object {
        internal fun toPointAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.SIZE -> super.get(Aes.POINT_SIZE)
                        Aes.STROKE -> super.get(Aes.POINT_STROKE)
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }


        internal fun toLabelAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.LINETYPE -> NamedLineType.SOLID
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }
    }
}