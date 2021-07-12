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

object Tilesets {
    fun chessboard(black: Color = Color.GRAY, white: Color = Color.LIGHT_GRAY) : TileSystemProvider {
        return object : TileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return SolidColorTileSystem(chessBoard(black, white), componentManager)
            }

            override val isVector: Boolean = false
        }
    }

    fun solid(color: Color): TileSystemProvider {
        return object : TileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return SolidColorTileSystem(fixed(color), componentManager)
            }

            override val isVector: Boolean = false
        }
    }

    fun raster(domains: List<String>) : TileSystemProvider {
        return object : TileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return RasterTileLoadingSystem(domains, componentManager)
            }

            override val isVector: Boolean = false
        }
    }

    fun letsPlot(tileService: TileService, quantumIterations: Int = 1_000) : TileSystemProvider {
        return object : TileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return TileLoadingSystem(quantumIterations, tileService, componentManager)
            }

            override val isVector: Boolean = true
        }
    }
}
