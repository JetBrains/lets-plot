package jetbrains.livemap.tiles

import jetbrains.datalore.base.async.Async
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.BIGGEST_LAYER
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.CELL_DATA_SIZE
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LOADING_TIME
import jetbrains.livemap.tiles.components.StatisticsComponent

internal class DebugTileDataFetcher(
    private val myStats: StatisticsComponent,
    private val myTileDataFetcher: TileDataFetcher
) : TileDataFetcher {

    private val mySystemTime = SystemTime()

    override fun fetch(cellKey: CellKey): Async<List<TileLayer>> {
        val tileDataAsync = myTileDataFetcher.fetch(cellKey)
        val start = mySystemTime.getTimeMs()
        tileDataAsync.onSuccess { tileData ->
            myStats.add(cellKey, CELL_DATA_SIZE, "${tileData.map(TileLayer::size).sum() / 1024}Kb")
            myStats.add(cellKey, LOADING_TIME,  "${mySystemTime.getTimeMs() - start}ms")

            var max = 0
            var name = ""
            for (tileLayer in tileData) {
                if (tileLayer.size > max) {
                    max = tileLayer.size
                    name = tileLayer.name
                }
            }

            myStats.add(cellKey, BIGGEST_LAYER, "$name ${max / 1024}Kb")
        }
        return tileDataAsync
    }
}