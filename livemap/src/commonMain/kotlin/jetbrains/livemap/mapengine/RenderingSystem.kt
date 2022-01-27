/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine

import jetbrains.datalore.base.typedGeometry.unaryMinus
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.graphics.RenderObject
import jetbrains.livemap.core.layers.CanvasLayerComponent
import jetbrains.livemap.core.layers.DirtyCanvasLayerComponent
import jetbrains.livemap.mapengine.camera.CameraComponent
import jetbrains.livemap.mapengine.camera.CameraScale.CameraScaleEffectComponent
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent

// Common rendering data - used for lines, polygons, pies, bars, points.
class RenderableComponent : EcsComponent {
    lateinit var renderer: Renderer
}

internal class RenderingSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val scaleEffect = getSingletonEntity<CameraComponent>().tryGet<CameraScaleEffectComponent>()

        for (layer in getEntities(DIRTY_LAYERS)) {
            layer.get<CanvasLayerComponent>().canvasLayer.addRenderTask { layerCtx ->
                layerCtx.save()

                scaleEffect?.apply {
                    layerCtx.translate(scaleOrigin)
                    layerCtx.scale(currentScale, currentScale)
                    layerCtx.translate(-scaleOrigin)
                }
                    ?: layerCtx.scale(1.0, 1.0)

                for (chartElementEntity in getLayerEntities(layer).filter { it.tryGet<ScreenLoopComponent>() != null }) {
                    val renderer = chartElementEntity.get<RenderableComponent>().renderer
                    chartElementEntity.get<ScreenLoopComponent>().origins.forEach { origin ->
                        context.mapRenderContext.draw(
                            layerCtx,
                            origin,
                            object : RenderObject {
                                override fun render(ctx: Context2d) {
                                    renderer.render(chartElementEntity, ctx)
                                }
                            }
                        )
                    }
                }

                layerCtx.restore()
            }
        }
    }

    private fun getLayerEntities(entity: EcsEntity): Sequence<EcsEntity> {
        return getEntitiesById(entity.get<LayerEntitiesComponent>().entities)
    }

    companion object {

        private val DIRTY_LAYERS = listOf(
            DirtyCanvasLayerComponent::class,
            LayerEntitiesComponent::class,
            CanvasLayerComponent::class
        )
    }
}