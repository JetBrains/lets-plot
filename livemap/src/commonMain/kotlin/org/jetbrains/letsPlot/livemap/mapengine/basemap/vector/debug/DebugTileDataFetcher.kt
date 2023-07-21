/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import org.jetbrains.letsPlot.livemap.core.SystemTime
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.BIGGEST_LAYER
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.CELL_DATA_SIZE
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.LOADING_TIME
import org.jetbrains.letsPlot.livemap.mapengine.basemap.StatisticsComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileDataFetcher
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey

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