/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.fragment

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.chart.ChartElementComponent
import jetbrains.livemap.chart.Renderers.drawMultiPolygon
import jetbrains.livemap.chart.Renderers.setWorldTransform
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.mapengine.Renderer
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.Viewport

class RegionRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d, viewport: Viewport) {

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
                ctx.setWorldTransform(fragment.get<WorldOriginComponent>().origin, viewport.zoom)
                ctx.beginPath()
                drawMultiPolygon(geometry.multiPolygon, ctx) { nop() }
                ctx.fill()
                ctx.restore()
        }
    }

    private fun nop() {}
}