/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.polygon

import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.LineJoin
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.changeAlphaWithMin
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.drawMultiPolygon

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
            ctx.setFillStyle(changeAlphaWithMin(chartElement.fillColor!!, chartElement.scalingAlphaValue))
            ctx.fill()
        }

        if (chartElement.strokeColor != null && chartElement.strokeWidth != 0.0) {
            ctx.setStrokeStyle(changeAlphaWithMin(chartElement.strokeColor!!, chartElement.scalingAlphaValue))
            ctx.setLineWidth(chartElement.strokeWidth * chartElement.scalingSizeFactor)
            ctx.stroke()
        }
    }
}