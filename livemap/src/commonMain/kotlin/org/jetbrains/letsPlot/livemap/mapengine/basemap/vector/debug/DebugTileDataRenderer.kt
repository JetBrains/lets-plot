/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.debug

import org.jetbrains.letsPlot.commons.SystemTime
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.livemap.core.multitasking.DebugMicroTask
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapLayerKind
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.renderTimeKey
import org.jetbrains.letsPlot.livemap.mapengine.basemap.DebugDataComponent.Companion.snapshotTimeKey
import org.jetbrains.letsPlot.livemap.mapengine.basemap.StatisticsComponent
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileDataRenderer
import org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.TileFeature
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey

internal class DebugTileDataRenderer(
    private val myStats: StatisticsComponent,
    private val mySystemTime: SystemTime,
    private val myTileDataRenderer: TileDataRenderer
) : TileDataRenderer {

    override fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: BasemapLayerKind
    ): MicroTask<Unit> {
        val microTask = myTileDataRenderer.render(canvas, tileFeatures, cellKey, layerKind)

        if (layerKind == BasemapLayerKind.DEBUG) return microTask

        val renderKey = renderTimeKey(layerKind)
        val snapshotKey = snapshotTimeKey(layerKind)

        val debugMicroTask = DebugMicroTask(mySystemTime, microTask)
        debugMicroTask.addFinishHandler {
            val start = mySystemTime.getTimeMs()
            myStats.add(cellKey, snapshotKey, "${mySystemTime.getTimeMs() - start}ms")
            myStats.add(
                cellKey,
                renderKey,
                "${debugMicroTask.processTime}ms (${debugMicroTask.maxResumeTime}ms)"
            )
        }
        return debugMicroTask
    }
}