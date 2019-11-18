/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.CanvasLayerComponent
import jetbrains.livemap.core.rendering.layers.DirtyCanvasLayerComponent

class UiRenderingTaskSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val uiLayer = getSingletonEntity(UiLayerComponent::class)

        uiLayer
            .get<CanvasLayerComponent>()
            .canvasLayer
            .addRenderTask { context2d ->
                getEntities(UiRenderComponent::class).forEach {
                    val renderObject = it.get<UiRenderComponent>().renderBox
                    context.mapRenderContext.draw(context2d, renderObject.origin, renderObject)
                }
            }

        uiLayer.tag(::DirtyCanvasLayerComponent)
    }

    class UiLayerComponent : EcsComponent
}