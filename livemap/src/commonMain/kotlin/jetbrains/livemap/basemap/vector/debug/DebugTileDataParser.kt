/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector.debug

import jetbrains.gis.tileprotocol.TileLayer
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.core.multitasking.DebugMicroTask
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.basemap.DebugDataComponent.Companion.PARSING_TIME
import jetbrains.livemap.basemap.StatisticsComponent
import jetbrains.livemap.basemap.vector.TileDataParser
import jetbrains.livemap.basemap.vector.TileFeature

internal class DebugTileDataParser(
    private val myStats: StatisticsComponent,
    private val mySystemTime: SystemTime,
    private val myTileDataParser: TileDataParser
) : TileDataParser {

    override fun parse(cellKey: CellKey, tileData: List<TileLayer>): MicroTask<Map<String, List<TileFeature>>> {
        val debugMicroTask = DebugMicroTask(mySystemTime, myTileDataParser.parse(cellKey, tileData))
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