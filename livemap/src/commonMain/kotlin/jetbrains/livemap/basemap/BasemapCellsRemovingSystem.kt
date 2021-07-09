/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.viewport.ViewportGridStateComponent
import jetbrains.livemap.viewport.ViewportGridUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.rendering.LayerEntitiesComponent
import jetbrains.livemap.rendering.RendererComponent
import jetbrains.livemap.basemap.BasemapCellRendererComponent.Companion.NULL_RENDERER

class BasemapCellsRemovingSystem(
    private val myTileCacheLimit: Int,
    componentManager: EcsComponentManager
) :
    AbstractSystem<LiveMapContext>(componentManager) {

    private val myCache = ArrayList<CellKey>()

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val viewportGridState: ViewportGridStateComponent = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS).get()

        getEntities<BasemapCellRendererComponent>().forEach { cellEntity ->
            val cellKey = cellEntity.get<BasemapCellComponent>().cellKey

            if (viewportGridState.visibleCells.contains(cellKey)) {
                cellEntity.get<RendererComponent>().renderer =
                    cellEntity.get<BasemapCellRendererComponent>().renderer
            }

            if (viewportGridState.cellsToRemove.contains(cellKey)) {
                cellEntity.get<RendererComponent>().renderer = NULL_RENDERER
            }
        }

        val cellsToKill = HashSet<CellKey>()

        myCache.removeAll(viewportGridState.visibleCells::contains) // do not remove visible tiles
        myCache += viewportGridState.cellsToRemove // chance to survive or die

        // non-cacheable cells can't survive
        getEntities<BasemapTileComponent>().forEach { entity ->
            if (entity.get<BasemapTileComponent>().nonCacheable) {
                val nonCacheableCell = entity.get<BasemapCellComponent>().cellKey
                if (nonCacheableCell in viewportGridState.cellsToRemove) {
                    cellsToKill += nonCacheableCell // no chance
                    myCache -= nonCacheableCell // sync items
                }
            }
        }

        while (!myCache.isEmpty() && myCache.size > myTileCacheLimit) {
            cellsToKill.add(myCache.removeAt(0))
        }

        removeCells(cellsToKill)
    }

    private fun removeCells(cellsToKill: Set<CellKey>) {
        val layers = getEntities(LayerEntitiesComponent::class).toList()

        getEntities(BasemapCellComponent::class)
            .filter { cellsToKill.contains(it.get<BasemapCellComponent>().cellKey) }
            .forEach { cellEntity ->
                layers.forEach { it.get<LayerEntitiesComponent>().remove(cellEntity.id) }
                cellEntity.remove()
            }
    }
}
