/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.geometry.ScreenGeometryComponent
import jetbrains.livemap.entities.placement.ScreenLoopComponent
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.Renderers
import jetbrains.livemap.entities.rendering.Utils
import jetbrains.livemap.entities.scaling.ScaleComponent

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

        Utils.apply(entity.get(), ctx)

        ctx.beginPath()

        val scale = fragments.first().get<ScaleComponent>().scale

        for (fragment in fragments) {
            val screenGeometry = fragment.tryGet<ScreenGeometryComponent>() ?: error("")
            val screenLoop = fragment.tryGet<ScreenLoopComponent>() ?: error("")

            for (origin in screenLoop.origins) {
                ctx.save()
                ctx.translate(origin.x, origin.y)
                ctx.scale(scale, scale)
                Renderers.drawLines(
                    screenGeometry.geometry,
                    ctx
                ) { nop() }
                ctx.restore()
            }
        }

        ctx.fill()
    }

    private fun nop() {}
}