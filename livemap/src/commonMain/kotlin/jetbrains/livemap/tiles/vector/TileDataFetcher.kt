package jetbrains.livemap.tiles.vector

import jetbrains.datalore.base.async.Async
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.projections.CellKey

internal interface TileDataFetcher {
    fun fetch(cellKey: CellKey): Async<List<TileLayer>>
}