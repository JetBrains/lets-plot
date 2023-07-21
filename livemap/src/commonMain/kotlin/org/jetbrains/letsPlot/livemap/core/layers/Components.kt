/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.layers

import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity

class DirtyCanvasLayerComponent : EcsComponent

class CanvasLayerComponent(
    val canvasLayer: CanvasLayer
) : EcsComponent

class ParentLayerComponent(val layerId: Int) : EcsComponent {

    companion object {

        fun tagDirtyParentLayer(entity: EcsEntity) {
            val parentLayer = entity.get<ParentLayerComponent>()
            val layer = entity.componentManager.getEntityById(parentLayer.layerId)
            layer.tag(::DirtyCanvasLayerComponent)
        }
    }
}
