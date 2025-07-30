/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar.Companion.ZERO
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.times
import org.jetbrains.letsPlot.commons.intern.util.ArrowSupport
import org.jetbrains.letsPlot.commons.intern.util.ArrowSupport.MIN_HEAD_LENGTH
import org.jetbrains.letsPlot.commons.intern.util.ArrowSupport.MIN_TAIL_LENGTH
import org.jetbrains.letsPlot.commons.intern.util.VecUtil
import org.jetbrains.letsPlot.commons.intern.util.VecUtil.padLineString
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.lineTo
import org.jetbrains.letsPlot.livemap.mapengine.moveTo
import kotlin.math.abs


open class PathRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val chartElement = entity.get<ChartElementComponent>()
        val color = chartElement.scaledStrokeColor()

        ctx.setStrokeStyle(color)
        ctx.setLineDash(chartElement.scaledLineDash())
        ctx.setLineDashOffset(chartElement.scaledLineDashOffset())
        ctx.setLineWidth(chartElement.scaledStrokeWidth())

        val startPadding = renderHelper.dimToWorld(chartElement.scaledStartPadding())
        val endPadding = renderHelper.dimToWorld(chartElement.scaledEndPadding())

        val geometry = entity.get<WorldGeometryComponent>().geometry.multiLineString
        val (startSegment, endSegment) = when (geometry.size) {
            0 -> return // No geometry to render
            1 -> padLineString(geometry[0], startPadding, endPadding) to null
            else -> padLineString(geometry[0], startPadding, ZERO) to padLineString(geometry.last(), ZERO, endPadding)
        }

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)
        ctx.beginPath()
        drawPath(startSegment, ctx)
        geometry.drop(1).dropLast(1).forEach { drawPath(it, ctx) } // mid segments
        endSegment?.let { drawPath(it, ctx) }
        ctx.restore()
        ctx.stroke()

        val arrowSpec = chartElement.arrowSpec ?: return

        val arrows = createArrows(startSegment, endSegment, arrowSpec, chartElement.scalingSizeFactor, renderHelper)

        // set attribute `stroke-miterlimit` to avoid a bevelled corner
        val miterLimit = ArrowSupport.miterLength(arrowSpec.angle, chartElement.scaledStrokeWidth()) * 2
        ctx.setStrokeMiterLimit(abs(miterLimit))

        renderArrowHead(arrows.first, arrowSpec, color, ctx, renderHelper)
        renderArrowHead(arrows.second, arrowSpec, color, ctx, renderHelper)
    }

    private fun createArrows(
        startSegment: List<Vec<World>>,
        endSegment: List<Vec<World>>?,
        arrowSpec: ArrowSpec,
        scalingSizeFactor: Double,
        renderHelper: RenderHelper
    ): Pair<List<Vec<World>>, List<Vec<World>>> {
        val arrowLength = renderHelper.dimToWorld(arrowSpec.length * scalingSizeFactor).value
        val minTailLength = renderHelper.dimToWorld(MIN_TAIL_LENGTH.px)
        val minHeadLength = renderHelper.dimToWorld(MIN_HEAD_LENGTH.px)

        if (endSegment == null) {
            // Only one segment, both arrow heads should be created on the start segment.
            return VecUtil.createArrowHeadGeometry(
                geometry = startSegment,
                angle = arrowSpec.angle,
                arrowLength = arrowLength,
                onStart = arrowSpec.isOnFirstEnd,
                onEnd = arrowSpec.isOnLastEnd,
                closed = arrowSpec.type == ArrowSpec.Type.CLOSED,
                minTailLength = minTailLength,
                minHeadLength = minHeadLength
            )
        }

        val (startHead, _) = VecUtil.createArrowHeadGeometry(
            geometry = startSegment,
            angle = arrowSpec.angle,
            arrowLength = arrowLength,
            onStart = arrowSpec.isOnFirstEnd,
            onEnd = false, // it's a two-segment path, arrow head onEnd of the first segment will appear in the middle of the overall path
            closed = arrowSpec.type == ArrowSpec.Type.CLOSED,
            minTailLength = minTailLength,
            minHeadLength = minHeadLength
        )

        val (_, endHead) = VecUtil.createArrowHeadGeometry(
            geometry = endSegment,
            angle = arrowSpec.angle,
            arrowLength = arrowLength,
            onStart = false, // it's a two-segment path, arrow head onStart of the second segment will appear in the middle of the overall path
            onEnd = arrowSpec.isOnFirstEnd,
            closed = arrowSpec.type == ArrowSpec.Type.CLOSED,
            minTailLength = minTailLength,
            minHeadLength = minHeadLength
        )

        return startHead to endHead
    }

    private fun renderArrowHead(
        points: List<Vec<World>>,
        arrowSpec: ArrowSpec,
        color: Color,
        ctx: Context2d,
        renderHelper: RenderHelper
    ) {
        if (points.size < 2) return

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)
        ctx.beginPath()
        ctx.moveTo(points[0])
        points.asSequence().drop(1).forEach(ctx::lineTo)
        ctx.restore()

        ctx.setLineDash(doubleArrayOf())
        if (arrowSpec.type == ArrowSpec.Type.CLOSED) {
            ctx.closePath()
            ctx.setFillStyle(color)
            ctx.fill()
        }
        ctx.stroke()
    }

    open fun drawPath(points: List<WorldPoint>, ctx: Context2d) {
        points[0].let(ctx::moveTo)
        points.asSequence().drop(1).forEach(ctx::lineTo)
    }
}
