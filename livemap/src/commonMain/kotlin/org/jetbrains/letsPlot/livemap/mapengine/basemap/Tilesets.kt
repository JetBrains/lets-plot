/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.gis.tileprotocol.TileService
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.mapengine.LiveMapContext
import org.jetbrains.letsPlot.livemap.mapengine.basemap.raster.RasterTileLoadingSystem
import org.jetbrains.letsPlot.livemap.mapengine.basemap.solid.SolidColorTileSystem
import org.jetbrains.letsPlot.livemap.mapengine.basemap.solid.chessBoard
import org.jetbrains.letsPlot.livemap.mapengine.basemap.solid.fixed
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileLoadingSystem

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
