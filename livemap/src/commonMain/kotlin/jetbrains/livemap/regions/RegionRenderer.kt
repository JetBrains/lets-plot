/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.regions

import jetbrains.datalore.base.typedGeometry.div
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.placement.ScreenLoopComponent
import jetbrains.livemap.rendering.Renderer
import jetbrains.livemap.rendering.Renderers.drawLines
import jetbrains.livemap.rendering.StyleComponent
import jetbrains.livemap.rendering.translate
import jetbrains.livemap.scaling.ScaleComponent

class RegionRenderer : Renderer {
    override fun render(entity: EcsEntity, ctx: Context2d) {

        val fragments = entity.get<RegionFragmentsComponent>().fragments
        if (fragments.isEmpty()) {
            return
        }

        for (fragment in fragments) {
            if (fragment.tryGet<ScreenGeometryComponent>() == null || fragment.tryGet<ScreenLoopComponent>() == null) {
                return
            }
        }

        entity.get<StyleComponent>().apply {
            ctx.setFillStyle(fillColor)
            ctx.setStrokeStyle(strokeColor)
            ctx.setLineWidth(strokeWidth)
        }

        ctx.scale(fragments.first().get<ScaleComponent>().scale)

        for (fragment in fragments) {
            val screenGeometry = fragment.tryGet<ScreenGeometryComponent>() ?: error("")
            val screenLoop = fragment.tryGet<ScreenLoopComponent>() ?: error("")

            for (origin in screenLoop.origins) {
                ctx.save()
                ctx.beginPath()
                ctx.translate(origin.div(fragments.first().get<ScaleComponent>().scale))
                drawLines(screenGeometry.geometry, ctx) { nop() }
                ctx.fill()
                ctx.restore()
            }
        }
    }

    private fun nop() {}
}