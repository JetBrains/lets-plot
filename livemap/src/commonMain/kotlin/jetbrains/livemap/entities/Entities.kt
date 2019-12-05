/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities

import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraListenerComponent
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.ecs.addComponents
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent

object Entities {

    fun mapEntity(
        componentManager: EcsComponentManager,
        parentLayerComponent: ParentLayerComponent,
        name: String
    ): EcsEntity {
        return componentManager
            .createEntity(name)
            .addComponents {
                + parentLayerComponent
                + CameraListenerComponent()
                + CenterChangedComponent()
                + ZoomChangedComponent()
            }
    }

    fun camera(componentManager: EcsComponentManager): EcsEntity {
        return componentManager.getEntity(CameraComponent::class)
    }

    fun animation(componentManager: EcsComponentManager, name: String): EcsEntity {
        return componentManager.createEntity(name).addComponents { + AnimationComponent() }
    }

    class MapEntityFactory(layerEntity: EcsEntity) {
        private val myComponentManager: EcsComponentManager = layerEntity.componentManager
        private val myParentLayerComponent: ParentLayerComponent = ParentLayerComponent(layerEntity.id)
        private val myLayerEntityComponent: LayerEntitiesComponent = layerEntity.get()

        fun createMapEntity(name: String): EcsEntity {
            return mapEntity(myComponentManager, myParentLayerComponent, name)
                .also { myLayerEntityComponent.add(it.id) }
        }
    }
}
