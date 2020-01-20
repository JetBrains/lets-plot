/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.gis.tileprotocol.TileService
import jetbrains.gis.tileprotocol.http.HttpTileTransport
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.api.liveMapTiles
import jetbrains.livemap.config.TileParameters
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

    class RasterTileLoadingSystemFactory(
        private val myTileTransport: HttpTileTransport,
        private val myRequestFormat: String
    ) : TileLoadingSystemFactory {

        override fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return RasterTileLoadingSystem(myTileTransport, myRequestFormat, componentManager)
        }
    }

     class VectorTileLoadingSystemFactory(
        private val myQuantumIterations: Int,
        private val myTileService: TileService
    ) : TileLoadingSystemFactory {

        override fun build(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return TileLoadingSystem(myQuantumIterations, myTileService, componentManager)
        }
    }

    companion object {
        fun createTileLoadingFactory(tiles: TileParameters, debug: Boolean, quant: Int): TileLoadingSystemFactory {
            if (debug)
                return DummyTileLoadingSystemFactory()

            return tiles.raster?.let { rasterUrl ->
                RasterTileLoadingSystemFactory(
                    HttpTileTransport(),
                    rasterUrl
                )
            } ?: VectorTileLoadingSystemFactory(
                quant,
                liveMapTiles {
                    host = tiles.vector.host
                    port = tiles.vector.port
                    theme = tiles.vector.theme
                }
            )
        }
    }
}