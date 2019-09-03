package jetbrains.datalore.maps.livemap.entities.rendering

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.rendering.layers.DirtyRenderLayerComponent


class ParentLayerComponent : EcsComponent {
    var layerId: Int = 0
}

fun tagDirtyParentLayer(entity: EcsEntity) {
    val parentLayer = entity.get<ParentLayerComponent>()
    val layerEntity = entity.componentManager.getEntityById(parentLayer.layerId)
    DirtyRenderLayerComponent.tag(layerEntity)
}
