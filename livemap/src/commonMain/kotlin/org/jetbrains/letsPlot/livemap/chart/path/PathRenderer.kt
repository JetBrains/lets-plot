/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.path

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
        val geometry = entity.get<WorldGeometryComponent>().geometry.multiLineString
        val chartElement = entity.get<ChartElementComponent>()
        val color = chartElement.scaledStrokeColor()

        val startPadding = renderHelper.dimToWorld(chartElement.scaledStartPadding())
        val endPadding = renderHelper.dimToWorld(chartElement.scaledEndPadding())

        for (lineString in geometry) {
            val adjustedGeometry = padLineString(lineString, startPadding.value, endPadding.value)

            ctx.save()
            ctx.scale(renderHelper.zoomFactor)
            ctx.beginPath()
            drawPath(adjustedGeometry, ctx)
            ctx.restore()

            ctx.setStrokeStyle(color)
            ctx.setLineDash(chartElement.scaledLineDash())
            ctx.setLineWidth(chartElement.scaledStrokeWidth())
            ctx.stroke()

            chartElement.arrowSpec?.let {

                val (startHead, endHead) = VecUtil.createArrowHeadGeometry(
                    geometry = adjustedGeometry,
                    angle = it.angle,
                    arrowLength = renderHelper.dimToWorld(it.length * chartElement.scalingSizeFactor).value,
                    onStart = it.isOnFirstEnd,
                    onEnd = it.isOnLastEnd,
                    closed = it.type == ArrowSpec.Type.CLOSED,
                    minTailLength = renderHelper.dimToWorld(MIN_TAIL_LENGTH.px),
                    minHeadLength = renderHelper.dimToWorld(MIN_HEAD_LENGTH.px)
                )

                // set attribute `stroke-miterlimit` to avoid a bevelled corner
                val miterLimit = ArrowSupport.miterLength(it.angle, chartElement.scaledStrokeWidth())
                ctx.setStrokeMiterLimit(abs(miterLimit))

                renderArrowHead(startHead, it, color, ctx, renderHelper)
                renderArrowHead(endHead, it, color, ctx, renderHelper)
            }
        }
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
