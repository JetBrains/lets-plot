/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap

import jetbrains.datalore.base.values.Color
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.mapengine.LiveMapContext
import jetbrains.livemap.mapengine.basemap.raster.RasterTileLoadingSystem
import jetbrains.livemap.mapengine.basemap.solid.SolidColorTileSystem
import jetbrains.livemap.mapengine.basemap.solid.chessBoard
import jetbrains.livemap.mapengine.basemap.solid.fixed
import jetbrains.livemap.mapengine.basemap.vector.TileLoadingSystem

@kotlinx.coroutines.ObsoleteCoroutinesApi
object Tilesets {
    fun chessboard(black: Color = Color.GRAY, white: Color = Color.LIGHT_GRAY) : BasemapTileSystemProvider {
        return object : BasemapTileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return SolidColorTileSystem(chessBoard(black, white), componentManager)
            }

            override val isVector: Boolean = false
        }
    }

    fun solid(color: Color): BasemapTileSystemProvider {
        return object : BasemapTileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return SolidColorTileSystem(fixed(color), componentManager)
            }

            override val isVector: Boolean = false
        }
    }

    fun raster(domains: List<String>) : BasemapTileSystemProvider {
        return object : BasemapTileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return RasterTileLoadingSystem(domains, componentManager)
            }

            override val isVector: Boolean = false
        }
    }

    fun letsPlot(tileService: TileService, quantumIterations: Int = 1_000) : BasemapTileSystemProvider {
        return object : BasemapTileSystemProvider {
            override fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext> {
                return TileLoadingSystem(quantumIterations, tileService, componentManager)
            }

            override val isVector: Boolean = true
        }
    }
}

interface BasemapTileSystemProvider {
    fun create(componentManager: EcsComponentManager): AbstractSystem<LiveMapContext>
    val isVector: Boolean
}
