/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_ALPHA
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_SEGMENT_COLOR
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling.POINT_UNIT_SIZE
import org.jetbrains.letsPlot.core.plot.base.geom.legend.CompositeLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.legend.TextRepelSegmentLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleCircle
import org.jetbrains.letsPlot.core.plot.base.geom.repel.LabelForceLayout
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle.Companion.savedNormalize
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.DataPointAestheticsDelegate
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
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

        fun toClient(point: DoubleVector, dp: DataPointAesthetics): DoubleVector? {
            return coord.toClient(point)
        }

        val helper = GeomHelper(pos, coord, ctx)
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
            val loc = helper.toClient(point, dp) ?: continue
            val pointLocation = coord.toClient(point) ?: continue
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, POINT_UNIT_SIZE)
            val size = pointSize(dp, sizeUnitRatio) ?: continue

            if (!bounds.contains(pointLocation)) continue

            circles[dp.index()] = DoubleCircle(pointLocation, size + pointPadding(sizeUnitRatio))
            val text = toString(dp.label(), ctx)
            if (text.isEmpty()) continue

            val hjust = TextUtil.hAnchor(dp, loc, aesBoundsCenter).toDouble()
            val vjust = TextUtil.vAnchor(dp, loc, aesBoundsCenter).toDouble()

            val box = TransformedRectangle(getRect(dp, loc, text, 1.0 , ctx, aesBoundsCenter))

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
            val text = toString(dp.label(), ctx)
            val point = dp.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, POINT_UNIT_SIZE)
            val pointLocation = coord.toClient(point) ?: continue
            val size = pointSize(dp, sizeUnitRatio) ?: continue

            val tc = buildTextComponent(toLabelAes(dp), result.position, text, 1.0, ctx, aesBoundsCenter)
            root.add(tc)

            val segmentLocation = getSegmentLocation(pointLocation, size, result.box, sizeUnitRatio)
            val segment = getSegment(segmentLocation, coord)

            if (segment != null) {
                root.add(buildSegmentComponent(toSegmentAes(dp), segment, svgHelper))
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

    private fun getSegmentLocation(pointLocation: DoubleVector, size: Double, rect: TransformedRectangle, scale: Double): DoubleSegment? {
        val locEnd = rect.shortestSegmentToRectangleEdgeCenter(pointLocation)?.end ?: return null

        val locStart = pointLocation.add((locEnd.subtract(pointLocation).savedNormalize().mul(size / 2)))

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

    private fun pointSize(p: DataPointAesthetics, scale: Double): Double? {
        return p.pointSize()?.let {
            it * POINT_UNIT_SIZE * scale
        }
    }

    companion object {
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


        internal fun toSegmentAes(p: DataPointAesthetics): DataPointAesthetics {
            return object : DataPointAestheticsDelegate(p) {

                override operator fun <T> get(aes: Aes<T>): T? {
                    val value: Any? = when (aes) {
                        Aes.COLOR -> if (super.get(Aes.SEGMENT_COLOR) == DEFAULT_SEGMENT_COLOR) super.get<T>(Aes.COLOR) else super.get(Aes.SEGMENT_COLOR)
                        Aes.SIZE -> super.get(Aes.SEGMENT_SIZE)
                        Aes.ALPHA -> if (super.get(Aes.SEGMENT_ALPHA) == DEFAULT_ALPHA) super.get<T>(Aes.ALPHA) else super.get(Aes.SEGMENT_ALPHA)
                        else -> super.get(aes)
                    }
                    @Suppress("UNCHECKED_CAST")
                    return value as T?
                }
            }
        }
    }
}