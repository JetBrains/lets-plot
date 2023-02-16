/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.placement

import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.core.Transforms
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.onEachEntity3
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.camera.ZoomLevelChangedComponent

class WorldDimension2ScreenUpdateSystem(componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        onEachEntity3<ZoomLevelChangedComponent, WorldDimensionComponent, ParentLayerComponent> { entity, _, dimensionComponent, _ ->
                dimensionComponent
                    .dimension
                    .let { world2Screen(it, context.camera.zoom.toInt()) } // becuase of ZoomLevelChangedComponent zoom expected to be an integer
                    .let { entity.provide(::ScreenDimensionComponent).dimension = it }

                ParentLayerComponent.tagDirtyParentLayer(entity)
            }
        }

    companion object {
        fun world2Screen(p: WorldPoint, zoom: Int) = Transforms.zoom<World, Client> { zoom }.apply(p)
    }
}
