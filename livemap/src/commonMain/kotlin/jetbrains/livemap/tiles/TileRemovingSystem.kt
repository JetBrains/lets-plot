package jetbrains.livemap.tiles

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.entities.rendering.LayerEntitiesComponent
import jetbrains.livemap.entities.rendering.RendererComponent
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.Components.RendererCacheComponent.Companion.NULL_RENDERER

class TileRemovingSystem(private val myTileCacheLimit: Int, componentManager: EcsComponentManager) :
    AbstractSystem<LiveMapContext>(componentManager) {

    private val myTileCacheList = ArrayList<CellKey>()

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val cellStateComponent = Components.CellStateComponent[getSingletonEntity(Components.CellStateComponent::class)]
        val visibleCells = cellStateComponent.visibleCells
        val cellsToRemove = cellStateComponent.cellsToRemove

        getEntities(Components.RendererCacheComponent::class).forEach { cellEntity ->
            val cellKey = Components.CellComponent.getCellKey(cellEntity)

            if (visibleCells.contains(cellKey)) {
                val renderer = Components.RendererCacheComponent.getRenderer(cellEntity)
                RendererComponent.setRenderer(cellEntity, renderer)
            }

            if (cellsToRemove.contains(cellKey)) {
                RendererComponent.setRenderer(cellEntity, NULL_RENDERER)
            }
        }

        myTileCacheList.removeAll(visibleCells::contains)
        cellsToRemove.forEach { myTileCacheList.add(it) }

        removeTiles()
    }

    private fun removeTiles() {
        val tilesToRemove = HashSet<CellKey>()

        while (!myTileCacheList.isEmpty() && myTileCacheList.size > myTileCacheLimit) {
            tilesToRemove.add(myTileCacheList.removeAt(0))
        }

        val layers = getEntities(LayerEntitiesComponent::class)

        getEntities(Components.CellComponent::class)
            .filter { tilesToRemove.contains(Components.CellComponent.getCellKey(it)) }
            .forEach { cellEntity ->
                layers.forEach { LayerEntitiesComponent[it].remove(cellEntity.id) }
                cellEntity.remove()
            }
    }
}