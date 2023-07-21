/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug

import org.jetbrains.letsPlot.gis.tileprotocol.TileLayer
import org.jetbrains.letsPlot.livemap.core.SystemTime
import org.jetbrains.letsPlot.livemap.core.multitasking.DebugMicroTask
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.PARSING_TIME
import org.jetbrains.letsPlot.livemap.mapengine.basemap.StatisticsComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileDataParser
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey

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