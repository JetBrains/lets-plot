/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.placement

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.CenterChangedComponent

class WorldOrigin2ScreenUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewport = context.mapRenderContext.viewport

        for (entity in getEntities(COMPONENT_TYPES)) {
            entity.get<WorldOriginComponent>()
                .origin
                .let(viewport::getViewCoord)
                .let { entity.provide(::ScreenOriginComponent).origin = it }

            ParentLayerComponent.tagDirtyParentLayer(entity)
        }
    }

    companion object {

        private val COMPONENT_TYPES = listOf(
            CenterChangedComponent::class,
            WorldOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}
