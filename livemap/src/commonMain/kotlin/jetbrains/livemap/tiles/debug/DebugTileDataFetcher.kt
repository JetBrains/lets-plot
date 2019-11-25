/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.debug

import jetbrains.datalore.base.async.Async
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.tiles.CellKey
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.BIGGEST_LAYER
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.CELL_DATA_SIZE
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LOADING_TIME
import jetbrains.livemap.tiles.components.StatisticsComponent
import jetbrains.livemap.tiles.vector.TileDataFetcher

internal class DebugTileDataFetcher(
    private val myStats: StatisticsComponent,
    private val mySystemTime: SystemTime,
    private val myTileDataFetcher: TileDataFetcher
) : TileDataFetcher {

    override fun fetch(cellKey: CellKey): Async<List<TileLayer>> {
        val tileDataAsync = myTileDataFetcher.fetch(cellKey)
        val start = mySystemTime.getTimeMs()

        tileDataAsync.onSuccess { tileLayers ->
            myStats.add(cellKey, CELL_DATA_SIZE, "${tileLayers.sumBy { it.size } / 1024}Kb")
            myStats.add(cellKey, LOADING_TIME,  "${mySystemTime.getTimeMs() - start}ms")

            val biggest: TileLayer? = tileLayers.maxBy { it.size }

            myStats.add(cellKey, BIGGEST_LAYER, "${biggest?.name} ${(biggest?.size ?: 0) / 1024}Kb")
        }
        return tileDataAsync
    }
}