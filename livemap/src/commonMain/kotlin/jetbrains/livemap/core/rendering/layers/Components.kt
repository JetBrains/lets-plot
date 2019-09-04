package jetbrains.livemap.core.rendering.layers

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class DirtyRenderLayerComponent : EcsComponent {
    companion object {
        fun untag(entity: EcsEntity) {
            entity.removeComponent(DirtyRenderLayerComponent::class)
        }

        fun tag(entity: EcsEntity) {
            entity.provide(::DirtyRenderLayerComponent)
        }

        fun tagParentLayer(entity: EcsEntity) {
            val layerId = ParentLayerComponent.getLayerId(entity)
            val layer = entity.componentManager.getEntityById(layerId)
            tag(layer)
        }
    }
}

class LayersOrderComponent(val renderLayers: List<RenderLayer>) : EcsComponent

class RenderLayerComponent(val renderLayer: RenderLayer) : EcsComponent {
    companion object {
        fun getRenderLayer(entity: EcsEntity): RenderLayer {
            return entity.getComponent<RenderLayerComponent>().renderLayer
        }
    }
}

class ParentLayerComponent(val layerId: Int) : EcsComponent {

    companion object {

        fun getLayerId(mapEntity: EcsEntity): Int {
            return get(mapEntity).layerId
        }

        fun tagDirtyParentLayer(entity: EcsEntity) {
            val parentLayer = get(entity)
            val layer = entity.componentManager.getEntityById(parentLayer.layerId)
            DirtyRenderLayerComponent.tag(layer)
        }


        operator fun get(entity: EcsEntity): ParentLayerComponent {
            return entity.getComponent()
        }
    }
}