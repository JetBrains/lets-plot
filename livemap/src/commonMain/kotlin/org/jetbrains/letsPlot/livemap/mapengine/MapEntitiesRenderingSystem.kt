/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine

import org.jetbrains.letsPlot.commons.intern.typedGeometry.unaryMinus
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.onEachEntity2
import org.jetbrains.letsPlot.livemap.core.graphics.RenderObject
import org.jetbrains.letsPlot.livemap.core.layers.CanvasLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraComponent
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraScale.CameraScaleEffectComponent

// Common rendering data - used for lines, polygons, pies, bars, points.
class RenderableComponent : EcsComponent {
    lateinit var renderer: Renderer
}

internal class MapEntitiesRenderingSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        onEachEntity2<LayerEntitiesComponent, CanvasLayerComponent> { _, children, layer ->
            layer.canvasLayer.clearRenderTaskss()
            addRenderTasks(layer, children, context)
        }
    }

    private fun addRenderTasks(
        layer: CanvasLayerComponent,
        children: LayerEntitiesComponent,
        context: LiveMapContext,
    ) {
        val scaleEffect = getSingletonEntity<CameraComponent>().tryGet<CameraScaleEffectComponent>()

        layer.canvasLayer.addRenderTask { layerCtx ->
            layerCtx.save()

            scaleEffect?.apply {
                layerCtx.translate(scaleOrigin)
                layerCtx.scale(currentScale, currentScale)
                layerCtx.translate(-scaleOrigin)
            }

            val renderHelper = RenderHelper(context.mapRenderContext.viewport)

            for (mapEntity in getEntitiesById(children.entities)) {
                val renderer = mapEntity.get<RenderableComponent>().renderer

                run {
                    val calculatedOrigins = context.mapRenderContext.viewport.getMapOrigins()
                    calculatedOrigins.forEach {

                        context.mapRenderContext.draw(
                            layerCtx,
                            it,
                            object : RenderObject {
                                override fun render(ctx: Context2d) {
                                    ctx.save()
                                    renderer.render(mapEntity, ctx, renderHelper)
                                    ctx.restore()
                                }
                            }
                        )

                    }
                }
            }

            layerCtx.restore()
        }
    }
}
