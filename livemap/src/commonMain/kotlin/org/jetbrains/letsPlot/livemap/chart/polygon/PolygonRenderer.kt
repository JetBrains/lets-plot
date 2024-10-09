/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.polygon

import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.LineJoin
import org.jetbrains.letsPlot.livemap.chart.ChartElementComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.geometry.WorldGeometryComponent
import org.jetbrains.letsPlot.livemap.mapengine.RenderHelper
import org.jetbrains.letsPlot.livemap.mapengine.Renderer
import org.jetbrains.letsPlot.livemap.mapengine.drawMultiPolygon

class PolygonRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {
        val chartElement = entity.get<ChartElementComponent>()
        val geometry = entity.get<WorldGeometryComponent>().geometry.multiPolygon

        ctx.setLineJoin(LineJoin.ROUND)
        ctx.beginPath()

        ctx.save()
        ctx.scale(renderHelper.zoomFactor)
        ctx.drawMultiPolygon(geometry, Context2d::closePath)
        ctx.restore()

        if (chartElement.fillColor != null) {
            ctx.setFillStyle(chartElement.scaledFillColor())
            ctx.fill()
        }

        if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
            ctx.setStrokeStyle(chartElement.scaledStrokeColor())
            ctx.setLineDash(chartElement.scaledLineDash())
            ctx.setLineDashOffset(chartElement.scaledLineDashOffset())
            ctx.setLineWidth(chartElement.scaledStrokeWidth())
            ctx.stroke()
        }
    }
}