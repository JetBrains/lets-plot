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
import org.jetbrains.letsPlot.core.plot.base.geom.repel.DoubleCircle
import org.jetbrains.letsPlot.core.plot.base.geom.repel.LabelForceLayout
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle
import org.jetbrains.letsPlot.core.plot.base.geom.repel.TransformedRectangle.Companion.savedNormalize
import org.jetbrains.letsPlot.core.plot.base.geom.util.ArrowSpec
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDouble
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

open class TextRepelGeom: TextGeom() {
    var seed: Long? = null
    var maxIter: Int? = null
    var direction: LabelForceLayout.Direction? = null
    var pointPadding: Double? = null
    var boxPadding: Double? = null
    var maxOverlaps: Int? = null
    var minSegmentLength: Double = 5.0
    var arrowSpec: ArrowSpec? = null
    var flat: Boolean = false
    var spacer: Double = 0.0 // additional space to shorten a segment by moving the start/end

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
            .setSpacer(spacer)
            .setResamplingEnabled(!coord.isLinear && !flat)
            .setArrowSpec(arrowSpec)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, ctx)
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center

        val boxes = HashMap<Int, TransformedRectangle>()
        val circles = HashMap<Int, DoubleCircle>()
        val hjusts = HashMap<Int, Double>()
        val vjusts = HashMap<Int, Double>()

        for (dp in aesthetics.dataPoints()) {
            val point = dp.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
            val loc = helper.toClient(point, dp) ?: continue
            val pointLocation = coord.toClient(point) ?: continue
            val size = dp.finiteOrNull(Aes.POINT_SIZE) ?: continue

            circles[dp.index()] = DoubleCircle(pointLocation, size * POINT_UNIT_SIZE / 2 + (pointPadding ?: 0.0))

            val text = toString(dp.label(), ctx)
            if (text.isEmpty()) continue

            val hjust = TextUtil.hAnchor(dp, loc, aesBoundsCenter).toDouble()
            val vjust = TextUtil.vAnchor(dp, loc, aesBoundsCenter).toDouble()
            val sizeUnitRatio = AesScaling.sizeUnitRatio(loc, coord, sizeUnit, BASELINE_TEXT_WIDTH)
            val box = TransformedRectangle(getRect(dp, loc, text, sizeUnitRatio, ctx, aesBoundsCenter))

            boxes[dp.index()] = box
            hjusts[dp.index()] = hjust
            vjusts[dp.index()] = vjust
        }

        val replacer = LabelForceLayout(
            boxes,
            circles,
            hjusts,
            vjusts,
            boxPadding ?: 0.0,
            bounds = DoubleRectangle(DoubleVector.ZERO, ctx.getContentBounds().dimension),
            maxOverlaps = maxOverlaps ?: 10,
            seed = seed,
            maxIter = maxIter ?: 2000,
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
            // Adapt point size to plot 'grid step' if necessary (i.e., in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(result.position, coord, sizeUnit, BASELINE_TEXT_WIDTH)
            val point = dp.finiteVectorOrNull(Aes.X, Aes.Y) ?: continue
            val pointLocation = coord.toClient(point) ?: continue
            val size = dp.finiteOrNull(Aes.POINT_SIZE) ?: continue
            val rect = TransformedRectangle(getRect(dp, result.position, text, sizeUnitRatio, ctx, aesBoundsCenter))

            val tc = buildTextComponent(dp, result.position, text, sizeUnitRatio, ctx, aesBoundsCenter)
            root.add(tc)

            val segmentLocation = getSegmentLocation(pointLocation, size, rect, hjusts[dp.index()] ?: 0.5, vjusts[dp.index()] ?: 0.5)
            val segment = getSegment(segmentLocation, coord)

            if (segment != null) {
                root.add(buildSegmentComponent(dp, segment, svgHelper))
            }

            targetCollector.addPoint(
                dp.index(),
                result.position,
                sizeUnitRatio * AesScaling.textSize(dp) / 2,
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

        val (svg, _) = svgHelper.createLine(segment, dp) { 1.0 }!!

        g.children().add(svg)

        return g
    }

    private fun getSegmentLocation(pointLocation: DoubleVector, size: Double, rect: TransformedRectangle, hjust: Double, vjust: Double): DoubleSegment? {
        val locEnd = rect.findEdgeConnectionPoint(pointLocation, hjust, vjust) ?: return null

        val locStart = pointLocation.add((locEnd.subtract(pointLocation).savedNormalize().mul(size * POINT_UNIT_SIZE / 2)))

        if (locStart.subtract(locEnd).length() < minSegmentLength) return null

        return DoubleSegment(locStart, locEnd)
    }

    private fun getSegment(segmentLocation: DoubleSegment?, coord: CoordinateSystem): DoubleSegment? {
        if (segmentLocation == null) return null
        val start = coord.fromClient(segmentLocation.start) ?: return null
        val end = coord.fromClient(segmentLocation.end) ?: return null

        return DoubleSegment(start, end)
    }
}