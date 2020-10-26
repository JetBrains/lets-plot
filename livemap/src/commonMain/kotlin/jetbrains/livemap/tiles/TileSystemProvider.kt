/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.raster.RasterTileLoadingSystem
import jetbrains.livemap.tiles.vector.TileLoadingSystem

interface TileSystemProvider {

    fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext>

    class EmptyTileSystemProvider : TileSystemProvider {

        override fun create(componentManager: EcsComponentManager) : AbstractSystem<LiveMapContext> {
            UNSUPPORTED("Tile system provider is not set")
        }
    }

    class RasterTileSystemProvider(
        private val myRequestFormat: String
    ) : TileSystemProvider {

        override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return RasterTileLoadingSystem(myRequestFormat, componentManager)
        }
    }

     class VectorTileSystemProvider(
         private val myTileService: TileService,
         private val myQuantumIterations: Int = 1_000
     ) : TileSystemProvider {

        override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return TileLoadingSystem(myQuantumIterations, myTileService, componentManager)
        }
    }
}
