package jetbrains.livemap.tiles.vector

import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.TileLoadingSystemBuilder

class VectorTileLoadingSystemBuilder(
    private val myQuantumIterations: Int,
    private val myTileService: TileService
    ) : TileLoadingSystemBuilder {

    override fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
        return TileLoadingSystem(myQuantumIterations, myTileService, componentManager)
    }
}