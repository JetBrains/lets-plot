package jetbrains.livemap.tiles

import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.http.HttpTileTransport
import jetbrains.livemap.DevParams
import jetbrains.livemap.DevParams.Companion.COMPUTATION_PROJECTION_QUANT
import jetbrains.livemap.DevParams.Companion.DEBUG_TILES
import jetbrains.livemap.DevParams.Companion.RASTER_TILES
import jetbrains.livemap.DevParams.Companion.VECTOR_TILES
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.api.internalTiles
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.raster.RasterTileLoadingSystem
import jetbrains.livemap.tiles.vector.TileLoadingSystem

interface TileLoadingSystemFactory {

    fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext>

    private class DummyTileLoadingSystemFactory : TileLoadingSystemFactory {

        override fun build(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext> {
            return object : AbstractSystem<LiveMapContext>(componentManager) {}
        }
    }

    private class RasterTileLoadingSystemFactory(
        private val myTileTransport: HttpTileTransport,
        private val myRequestFormat: String
    ) : TileLoadingSystemFactory {

        override fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return RasterTileLoadingSystem(myTileTransport, myRequestFormat, componentManager)
        }
    }

    private class VectorTileLoadingSystemFactory(
        private val myQuantumIterations: Int,
        private val myTileService: TileService
    ) : TileLoadingSystemFactory {

        override fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return TileLoadingSystem(myQuantumIterations, myTileService, componentManager)
        }
    }

    companion object {
        fun createTileLoadingFactory(devParams: DevParams): TileLoadingSystemFactory {
            if (devParams.isSet(DEBUG_TILES))
                return DummyTileLoadingSystemFactory()

            val rasterTiles = devParams.read(RASTER_TILES)
            if (rasterTiles != null)
                return RasterTileLoadingSystemFactory(
                    HttpTileTransport(rasterTiles.host, rasterTiles.port, ""),
                    rasterTiles.format
                )

            val vectorTiles = devParams.read(VECTOR_TILES)
            return VectorTileLoadingSystemFactory(
                devParams.read(COMPUTATION_PROJECTION_QUANT),
                internalTiles {
                    host = vectorTiles.host
                    port = vectorTiles.port
                    theme = vectorTiles.theme
                }
            )
        }
    }
}