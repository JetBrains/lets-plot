/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.layers

import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext
import jetbrains.livemap.core.ecs.EcsEntity

class LayersRenderingSystem internal constructor(
    componentManager: EcsComponentManager,
    private val myRenderingStrategy: RenderingStrategy
) : AbstractSystem<EcsContext>(componentManager) {
    private val myDirtyLayers = ArrayList<Int>()

    val dirtyLayers: List<Int>
        get() = myDirtyLayers

    override fun updateImpl(context: EcsContext, dt: Double) {
        val canvasLayers = getSingleton<LayersOrderComponent>().canvasLayers

        val layerEntities = getEntities(CanvasLayerComponent::class).toList()
        val dirtyEntities = getEntities(DirtyCanvasLayerComponent::class).toList()

        myDirtyLayers.clear()
        dirtyEntities.forEach { myDirtyLayers.add(it.id) }

        myRenderingStrategy.render(canvasLayers, layerEntities, dirtyEntities)
    }

    interface RenderingStrategy {
        fun render(
            renderingOrder: List<CanvasLayer>,
            layerEntities: Iterable<EcsEntity>,
            dirtyLayerEntities: Iterable<EcsEntity>
        )
    }
}