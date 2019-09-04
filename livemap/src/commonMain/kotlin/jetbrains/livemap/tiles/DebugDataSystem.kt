package jetbrains.livemap.tiles

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.rendering.layers.DirtyRenderLayerComponent
import jetbrains.livemap.tiles.CellStateUpdateSystem.Companion.CELL_STATE_REQUIRED_COMPONENTS
import jetbrains.livemap.tiles.Components.CellComponent
import jetbrains.livemap.tiles.Components.DebugCellLayerComponent
import jetbrains.livemap.tiles.Components.DebugDataComponent
import jetbrains.livemap.tiles.Components.StatisticsComponent

class DebugDataSystem(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext>(componentManager) {

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        if (!containsSingletonEntity(DebugCellLayerComponent::class)) {
            return
        }

        val debugLayer = getSingletonEntity(DebugCellLayerComponent::class)
        val stats = StatisticsComponent.get(getSingletonEntity(CELL_STATE_REQUIRED_COMPONENTS))

        getEntities(DEBUG_REQUIRED_COMPONENTS).forEach { cellEntity ->
            val cellKey = CellComponent.getCellKey(cellEntity)

            stats[cellKey]?.let {
                DebugDataComponent.addData(cellEntity, it)
                stats.remove(cellKey)
                DirtyRenderLayerComponent.tag(debugLayer)
            }
        }
    }

    companion object {
        private val DEBUG_REQUIRED_COMPONENTS = listOf(
            CellComponent::class,
            DebugDataComponent::class
        )
    }
}