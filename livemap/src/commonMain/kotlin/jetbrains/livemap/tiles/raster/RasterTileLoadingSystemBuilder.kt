package jetbrains.livemap.tiles.raster

import jetbrains.gis.tileprotocol.http.HttpTileTransport
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.TileLoadingSystemBuilder

class RasterTileLoadingSystemBuilder(
    private val myTileTransport: HttpTileTransport,
    private val myRequestFormat: String
) : TileLoadingSystemBuilder {

    override fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
        return RasterTileLoadingSystem(myTileTransport, myRequestFormat, componentManager)
    }
}