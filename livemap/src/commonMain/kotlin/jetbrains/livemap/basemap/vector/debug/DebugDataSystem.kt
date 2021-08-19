/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector.debug

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.basemap.BasemapCellComponent
import jetbrains.livemap.viewport.ViewportGridUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.basemap.DebugCellLayerComponent
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.DirtyCanvasLayerComponent
import jetbrains.livemap.basemap.DebugDataComponent
import jetbrains.livemap.basemap.StatisticsComponent

class DebugDataSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (!containsEntity(DebugCellLayerComponent::class)) {
            return
        }

        val debugLayer = getSingletonEntity(DebugCellLayerComponent::class)
        val statistics: StatisticsComponent = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS).get()

        getEntities(DEBUG_REQUIRED_COMPONENTS).forEach { cellEntity ->
            val cellKey = cellEntity.get<BasemapCellComponent>().cellKey
            val debug: DebugDataComponent = cellEntity.get()

            statistics.stats.remove(cellKey)?.let {
                debug.addData(it)
                debugLayer.tag(::DirtyCanvasLayerComponent)
            }
        }
    }

    companion object {
        private val DEBUG_REQUIRED_COMPONENTS = listOf(
            BasemapCellComponent::class,
            DebugDataComponent::class
        )
    }
}