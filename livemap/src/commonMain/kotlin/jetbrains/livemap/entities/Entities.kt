package jetbrains.livemap.entities

import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.camera.CameraListenerComponent
import jetbrains.livemap.camera.CenterChangedComponent
import jetbrains.livemap.camera.ZoomChangedComponent
import jetbrains.livemap.core.animation.Animation
import jetbrains.livemap.core.ecs.AnimationComponent
import jetbrains.livemap.core.ecs.AnimationObjectComponent
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.ParentLayerComponent
import jetbrains.livemap.entities.placement.Components
import jetbrains.livemap.entities.rendering.Renderer
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.projections.ClientPoint
import jetbrains.livemap.projections.WorldPoint

object Entities {

    fun mapEntity(
        componentManager: EcsComponentManager, worldPlacement: WorldPoint,
        parentLayerComponent: ParentLayerComponent, renderer: Renderer, name: String
    ): EcsEntity {
        return componentManager
            .createEntity(name)
            .addComponent(parentLayerComponent)
            .addComponent(RendererComponent(renderer))
            .addComponent(Components.WorldOriginComponent(worldPlacement))
            .addComponent(CameraListenerComponent())
            .addComponent(CenterChangedComponent())
            .addComponent(ZoomChangedComponent())
            .addComponent(Components.ScreenLoopComponent())
            .addComponent(Components.ScreenOriginComponent())
    }

    fun dynamicMapEntity(
        componentManager: EcsComponentManager, parentLayerComponent: ParentLayerComponent,
        renderer: Renderer, name: String
    ): EcsEntity {
        return componentManager
            .createEntity(name)
            .addComponent(parentLayerComponent)
            .addComponent(RendererComponent(renderer))
            .addComponent(CameraListenerComponent())
            .addComponent(CenterChangedComponent())
            .addComponent(ZoomChangedComponent())
            .addComponent(Components.ScreenLoopComponent())
            .addComponent(Components.ScreenOriginComponent())
    }

    fun camera(componentManager: EcsComponentManager): EcsEntity {
        return componentManager.getEntity(CameraComponent::class)
    }

    fun createScreenEntity(
        componentManager: EcsComponentManager,
        screenPlacement: ClientPoint,
        name: String
    ): EcsEntity {
        return componentManager
            .createEntity(name)
            .addComponent(CameraListenerComponent())
            .addComponent(CenterChangedComponent())
            .addComponent(ZoomChangedComponent())
            .addComponent(
                Components.ScreenOriginComponent()
                    .apply { origin = screenPlacement }
            )
    }

    fun animationObject(componentManager: EcsComponentManager, animation: Animation, name: String): EcsEntity {
        return componentManager.createEntity(name).addComponent(AnimationObjectComponent(animation))
    }

    fun animation(componentManager: EcsComponentManager, name: String): EcsEntity {
        return componentManager.createEntity(name).addComponent(AnimationComponent())
    }

    class MapEntityFactory(layerEntity: EcsEntity) {
        private val myComponentManager: EcsComponentManager = layerEntity.componentManager
        private val myParentLayerComponent: ParentLayerComponent = ParentLayerComponent(layerEntity.id)

        fun createMapEntity(worldPlacement: WorldPoint, renderer: Renderer, name: String): EcsEntity {
            return Entities.mapEntity(myComponentManager, worldPlacement, myParentLayerComponent, renderer, name)
        }

        fun createDynamicMapEntity(name: String, renderer: Renderer): EcsEntity {
            return Entities.dynamicMapEntity(myComponentManager, myParentLayerComponent, renderer, name)
        }
    }
}
