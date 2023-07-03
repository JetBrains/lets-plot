/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector.debug

import org.jetbrains.letsPlot.base.intern.async.Async
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.mapengine.basemap.DebugDataComponent.Companion.BIGGEST_LAYER
import jetbrains.livemap.mapengine.basemap.DebugDataComponent.Companion.CELL_DATA_SIZE
import jetbrains.livemap.mapengine.basemap.DebugDataComponent.Companion.LOADING_TIME
import jetbrains.livemap.mapengine.basemap.StatisticsComponent
import jetbrains.livemap.mapengine.basemap.vector.TileDataFetcher
import jetbrains.livemap.mapengine.viewport.CellKey

internal class DebugTileDataFetcher(
    private val myStats: StatisticsComponent,
    private val mySystemTime: SystemTime,
    private val myTileDataFetcher: TileDataFetcher
) : TileDataFetcher {

    override fun fetch(cellKey: CellKey): Async<List<TileLayer>> {
        val tileDataAsync = myTileDataFetcher.fetch(cellKey)
        val start = mySystemTime.getTimeMs()

        tileDataAsync.onSuccess { tileLayers ->
            myStats.add(cellKey, CELL_DATA_SIZE, "${tileLayers.sumOf { it.size } / 1024}Kb")
            myStats.add(cellKey, LOADING_TIME, "${mySystemTime.getTimeMs() - start}ms")

            val biggest: TileLayer? = tileLayers.maxByOrNull(TileLayer::size)

            myStats.add(cellKey, BIGGEST_LAYER, "${biggest?.name} ${(biggest?.size ?: 0) / 1024}Kb")
        }
        return tileDataAsync
    }
}