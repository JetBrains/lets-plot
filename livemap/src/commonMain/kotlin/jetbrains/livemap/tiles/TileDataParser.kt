package jetbrains.livemap.tiles

import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.CellKey

internal interface TileDataParser {
    fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>>
}