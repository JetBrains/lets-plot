/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.cells.CellComponent
import jetbrains.livemap.cells.CellKey
import jetbrains.livemap.cells.CellStateComponent
import jetbrains.livemap.cells.CellStateUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.rendering.LayerEntitiesComponent
import jetbrains.livemap.rendering.RendererComponent
import jetbrains.livemap.tiles.RendererCacheComponent.Companion.NULL_RENDERER

class TileRemovingSystem(private val myTileCacheLimit: Int, componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    private val myCache = ArrayList<CellKey>()

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cellState: CellStateComponent = getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS).get()

        getEntities(RendererCacheComponent::class).forEach { cellEntity ->
            val cellKey = cellEntity.get<CellComponent>().cellKey

            if (cellState.visibleCells.contains(cellKey)) {
                cellEntity.get<RendererComponent>().renderer =
                    cellEntity.get<RendererCacheComponent>().renderer
            }

            if (cellState.cellsToRemove.contains(cellKey)) {
                cellEntity.get<RendererComponent>().renderer = NULL_RENDERER
            }
        }

        val cellsToKill = HashSet<CellKey>()

        myCache.removeAll(cellState.visibleCells::contains) // do not remove visible tiles
        myCache += cellState.cellsToRemove // chance to survive or die

        // non-cacheable cells can't survive
        getEntities<TileComponent>().forEach { entity ->
            if (entity.get<TileComponent>().nonCacheable) {
                val nonCacheableCell = entity.get<CellComponent>().cellKey
                if (nonCacheableCell in cellState.cellsToRemove) {
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

        getEntities(CellComponent::class)
            .filter { cellsToKill.contains(it.get<CellComponent>().cellKey) }
            .forEach { cellEntity ->
                layers.forEach { it.get<LayerEntitiesComponent>().remove(cellEntity.id) }
                cellEntity.remove()
            }
    }
}