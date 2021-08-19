/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector.debug

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.basemap.BasemapLayerKind
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.core.multitasking.DebugMicroTask
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.basemap.DebugDataComponent.Companion.renderTimeKey
import jetbrains.livemap.basemap.DebugDataComponent.Companion.snapshotTimeKey
import jetbrains.livemap.basemap.StatisticsComponent
import jetbrains.livemap.basemap.vector.TileDataRenderer
import jetbrains.livemap.basemap.vector.TileFeature

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
    ): MicroTask<Async<Canvas.Snapshot>> {
        val microTask = myTileDataRenderer.render(canvas, tileFeatures, cellKey, layerKind)

        if (layerKind == BasemapLayerKind.DEBUG) return microTask

        val renderKey = renderTimeKey(layerKind)
        val snapshotKey = snapshotTimeKey(layerKind)

        val debugMicroTask = DebugMicroTask(mySystemTime, microTask)
        debugMicroTask.addFinishHandler {
            val start = mySystemTime.getTimeMs()
            debugMicroTask.getResult()
                .onSuccess { myStats.add(cellKey, snapshotKey, "${mySystemTime.getTimeMs() - start}ms") }
            myStats.add(
                cellKey,
                renderKey,
                "${debugMicroTask.processTime}ms (${debugMicroTask.maxResumeTime}ms)"
            )
        }
        return debugMicroTask
    }
}