package jetbrains.livemap.tiles

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager

interface TileLoadingSystemBuilder {
    fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext>

    class DummyTileLoadingSystemBuilder : TileLoadingSystemBuilder {
        override fun build(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext> {
            return object : AbstractSystem<LiveMapContext>(componentManager) {}
        }
    }
}