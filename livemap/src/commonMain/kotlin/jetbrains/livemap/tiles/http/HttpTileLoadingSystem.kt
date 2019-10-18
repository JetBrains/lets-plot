package jetbrains.livemap.tiles.http

import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.components.RequestTilesComponent

class HttpTileLoadingSystem(componentManager: EcsComponentManager)
    : AbstractSystem<LiveMapContext>(componentManager) {


    override fun updateImpl(context: LiveMapContext, dt: Double) {
        getSingletonComponent<RequestTilesComponent>().requestTiles.forEach { cellKey ->

        }
    }
}