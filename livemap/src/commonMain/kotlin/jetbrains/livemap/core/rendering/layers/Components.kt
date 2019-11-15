/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.layers

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

class DirtyRenderLayerComponent : EcsComponent

class LayersOrderComponent(private val myGroupedLayers: GroupedLayers ) : EcsComponent {
    val renderLayers: List<RenderLayer>
        get() = myGroupedLayers.orderedLayers
}
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
