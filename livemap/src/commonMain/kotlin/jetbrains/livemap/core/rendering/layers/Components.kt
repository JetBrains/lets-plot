package jetbrains.livemap.core.rendering.layers

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class DirtyRenderLayerComponent : EcsComponent
class LayersOrderComponent(val renderLayers: List<RenderLayer>) : EcsComponent
class RenderLayerComponent(val renderLayer: RenderLayer) : EcsComponent
class ParentLayerComponent(val layerId: Int) : EcsComponent {

    companion object {

        fun tagDirtyParentLayer(entity: EcsEntity) {
            val parentLayer = entity.get<ParentLayerComponent>()
            val layer = entity.componentManager.getEntityById(parentLayer.layerId)
            layer.tag(::DirtyRenderLayerComponent)
        }
    }
}