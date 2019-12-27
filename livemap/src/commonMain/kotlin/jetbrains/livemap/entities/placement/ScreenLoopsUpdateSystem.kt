/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.placement

import jetbrains.datalore.base.typedGeometry.plus
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.projections.Coordinates.ZERO_CLIENT_POINT

class ScreenLoopsUpdateSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewport = context.mapRenderContext.viewport

        getEntities(COMPONENT_TYPES).forEach { entity ->
            val origin = entity
                .run { tryGet<ScreenOffsetComponent>()?.offset ?: ZERO_CLIENT_POINT }
                .run { entity.get<ScreenOriginComponent>().origin + this }

            val dimension = entity.get<ScreenDimensionComponent>().dimension

            entity.get<ScreenLoopComponent>().origins = viewport.getOrigins(origin, dimension)
        }
    }

    companion object {
        private val COMPONENT_TYPES = listOf(
            CenterChangedComponent::class,
            ScreenOriginComponent::class,
            ScreenDimensionComponent::class,
            ScreenLoopComponent::class
        )
    }
}