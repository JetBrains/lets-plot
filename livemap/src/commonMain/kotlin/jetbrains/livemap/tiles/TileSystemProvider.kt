/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

import jetbrains.datalore.base.values.Color
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.tiles.raster.RasterTileLoadingSystem
import jetbrains.livemap.tiles.solid.SolidColorTileSystem
import jetbrains.livemap.tiles.solid.chessBoard
import jetbrains.livemap.tiles.solid.fixed
import jetbrains.livemap.tiles.vector.TileLoadingSystem

interface TileSystemProvider {

    fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext>

    class ChessboardTileSystemProvider() : TileSystemProvider {
        override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return SolidColorTileSystem(chessBoard(Color.GRAY, Color.LIGHT_GRAY), componentManager)
        }
    }

    class SolidTileSystemProvider() : TileSystemProvider {
        override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return SolidColorTileSystem(fixed(Color.WHITE), componentManager)
        }
    }

    class RasterTileSystemProvider(
        private val myDomains: List<String>
    ) : TileSystemProvider {

        override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
            return RasterTileLoadingSystem(myDomains, componentManager)
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
