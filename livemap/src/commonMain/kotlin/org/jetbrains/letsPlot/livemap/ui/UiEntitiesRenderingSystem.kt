/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.ui

import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.layers.CanvasLayerComponent
import org.jetbrains.letsPlot.livemap.core.layers.DirtyCanvasLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext

class UiEntitiesRenderingSystem(
    componentManager: EcsComponentManager
) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val uiLayer = getSingletonEntity(UiLayerComponent::class)

        if (uiLayer.contains<DirtyCanvasLayerComponent>()) {
            uiLayer
                .get<CanvasLayerComponent>()
                .canvasLayer
                .addRenderTask { context2d ->
                    getEntities(UiRenderComponent::class).toList().forEach {
                        val renderObject = it.get<UiRenderComponent>().renderBox
                        context.mapRenderContext.draw(context2d, renderObject.origin, renderObject)
                    }
                }
        }
    }

    class UiLayerComponent : EcsComponent
}