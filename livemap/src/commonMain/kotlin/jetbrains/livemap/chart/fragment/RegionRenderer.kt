/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.fragment

import org.jetbrains.letsPlot.core.canvas.Context2d
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.RenderHelper
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.drawMultiPolygon

class RegionRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, renderHelper: RenderHelper) {

        val fragments = entity.get<RegionFragmentsComponent>().fragments
        if (fragments.isEmpty()) {
            return
        }

        for (fragment in fragments) {
            if (fragment.tryGet<WorldGeometryComponent>() == null) {
                return
            }
        }

        entity.get<ChartElementComponent>().apply {
            ctx.setFillStyle(fillColor)
            ctx.setStrokeStyle(strokeColor)
            ctx.setLineWidth(strokeWidth)
        }

        for (fragment in fragments) {
            val geometry = fragment.tryGet<WorldGeometryComponent>()?.geometry ?: error("")

                ctx.save()
                ctx.scale(renderHelper.zoomFactor)
                ctx.beginPath()
                ctx.drawMultiPolygon(geometry.multiPolygon) { nop() }
                ctx.fill()
                ctx.restore()
        }
    }

    private fun nop() {}
}