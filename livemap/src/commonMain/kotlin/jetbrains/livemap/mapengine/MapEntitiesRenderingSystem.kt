/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import jetbrains.datalore.base.typedGeometry.unaryMinus
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.onEachEntity2
import jetbrains.livemap.core.graphics.RenderObject
import jetbrains.livemap.core.layers.CanvasLayerComponent
import jetbrains.livemap.mapengine.camera.CameraComponent
import jetbrains.livemap.mapengine.camera.CameraScale.CameraScaleEffectComponent
import jetbrains.livemap.mapengine.placement.ScreenDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOriginComponent

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

            for (mapEntity in getEntitiesById(children.entities)) {
                val renderer = mapEntity.get<RenderableComponent>().renderer
                //val origins = mapEntity.get<ScreenLoopComponent>().origins

                run {
                    val worldOrigin = mapEntity.tryGet<WorldOriginComponent>()?.origin ?: return@run
                    val screenDimension = mapEntity.tryGet<ScreenDimensionComponent>()?.dimension ?: Client.ZERO_VEC
                    val screenOrigin = context.mapRenderContext.viewport.getViewCoord(worldOrigin)
                    val calculatedOrigins = context.mapRenderContext.viewport.getOrigins(screenOrigin, screenDimension)

                    calculatedOrigins.forEach {

                        context.mapRenderContext.draw(
                            layerCtx,
                            it,
                            object : RenderObject {
                                override fun render(ctx: Context2d) {
                                    renderer.render(mapEntity, ctx)
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
