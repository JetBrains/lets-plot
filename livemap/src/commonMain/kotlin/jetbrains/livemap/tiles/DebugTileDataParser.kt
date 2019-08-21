package jetbrains.livemap.tiles

import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.core.multitasking.DebugMicroTask
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.PARSING_TIME

internal class DebugTileDataParser(
    private val myStats: Components.StatisticsComponent,
    private val myTileDataParser: TileDataParser
) : TileDataParser {

    override fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>> {
        val debugMicroTask = DebugMicroTask(myTileDataParser.parse(cellKey, tileData))
        debugMicroTask.addFinishHandler {
            myStats.add(
                cellKey,
                PARSING_TIME,
                "${debugMicroTask.processTime}ms (${debugMicroTask.maxResumeTime}ms)"
            )
        }
        return debugMicroTask
    }
}