/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.layers

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class DirtyCanvasLayerComponent : EcsComponent

class LayersOrderComponent(private val myGroupedLayers: GroupedLayers ) : EcsComponent {
    val canvasLayers: List<CanvasLayer>
        get() = myGroupedLayers.orderedLayers
}
class CanvasLayerComponent(val canvasLayer: CanvasLayer) : EcsComponent
class ParentLayerComponent(val layerId: Int) : EcsComponent {

    companion object {

        fun tagDirtyParentLayer(entity: EcsEntity) {
            val parentLayer = entity.get<ParentLayerComponent>()
            val layer = entity.componentManager.getEntityById(parentLayer.layerId)
            layer.tag(::DirtyCanvasLayerComponent)
        }
    }
}
