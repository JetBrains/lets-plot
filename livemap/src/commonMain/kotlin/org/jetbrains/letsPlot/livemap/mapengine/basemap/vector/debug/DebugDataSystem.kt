/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug

import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.layers.DirtyCanvasLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapCellComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugCellLayerComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.StatisticsComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS

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