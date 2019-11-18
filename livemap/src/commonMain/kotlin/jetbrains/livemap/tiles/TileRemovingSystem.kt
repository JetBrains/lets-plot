/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.tiles.CellStateUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.tiles.components.CellComponent
import jetbrains.livemap.tiles.components.CellStateComponent
import jetbrains.livemap.tiles.components.RendererCacheComponent
import jetbrains.livemap.tiles.components.RendererCacheComponent.Companion.NULL_RENDERER

class TileRemovingSystem(private val myTileCacheLimit: Int, componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    private val myTileCacheList = ArrayList<CellKey>()

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

        myTileCacheList.removeAll(cellState.visibleCells::contains)
        cellState.cellsToRemove.forEach { myTileCacheList.add(it) }

        removeTiles()
    }

    private fun removeTiles() {
        val tilesToRemove = HashSet<CellKey>()

        while (!myTileCacheList.isEmpty() && myTileCacheList.size > myTileCacheLimit) {
            tilesToRemove.add(myTileCacheList.removeAt(0))
        }

        val layers = getEntities(LayerEntitiesComponent::class).toList()

        getEntities(CellComponent::class)
            .filter { tilesToRemove.contains(it.get<CellComponent>().cellKey) }
            .forEach { cellEntity ->
                layers.forEach { it.get<LayerEntitiesComponent>().remove(cellEntity.id) }
                cellEntity.remove()
            }
    }
}