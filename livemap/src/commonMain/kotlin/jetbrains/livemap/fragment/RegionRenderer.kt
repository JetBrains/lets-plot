/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragment

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.Renderers.drawClientMultiPolygon
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.viewport.Viewport

class RegionRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, viewport: Viewport) {

        val fragments = entity.get<RegionFragmentsComponent>().fragments
        if (fragments.isEmpty()) {
            return
        }

        for (fragment in fragments) {
            if (fragment.tryGet<ScreenGeometryComponent>() == null) {
                return
            }
        }

        entity.get<ChartElementComponent>().apply {
            ctx.setFillStyle(fillColor)
            ctx.setStrokeStyle(strokeColor)
            ctx.setLineWidth(strokeWidth)
        }

        for (fragment in fragments) {
            val geometry = fragment.tryGet<ScreenGeometryComponent>()?.geometry ?: error("")

                ctx.save()
                ctx.beginPath()
                ctx.drawClientMultiPolygon(geometry.multiPolygon) { nop() }
                ctx.fill()
                ctx.restore()
        }
    }

    private fun nop() {}
}