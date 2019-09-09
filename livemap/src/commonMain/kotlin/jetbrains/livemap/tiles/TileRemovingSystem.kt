package jetbrains.livemap.tiles

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.projections.CellKey
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
            val cellKey = CellComponent.getCellKey(cellEntity)

            if (cellState.visibleCells.contains(cellKey)) {
                val renderer = RendererCacheComponent.getRenderer(cellEntity)
                RendererComponent.setRenderer(cellEntity, renderer)
            }

            if (cellState.cellsToRemove.contains(cellKey)) {
                RendererComponent.setRenderer(cellEntity, NULL_RENDERER)
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
            .filter { tilesToRemove.contains(CellComponent.getCellKey(it)) }
            .forEach { cellEntity ->
                layers.forEach { LayerEntitiesComponent[it].remove(cellEntity.id) }
                cellEntity.remove()
            }
    }
}