package jetbrains.livemap.tiles

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.core.multitasking.DebugMicroTask
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.components.CellLayerKind
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.HTTP_TILE_RENDER_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.HTTP_TILE_SNAPSHOT_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LABEL_RENDER_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.LABEL_SNAPSHOT_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.WORLD_RENDER_TIME
import jetbrains.livemap.tiles.components.DebugDataComponent.Companion.WORLD_SNAPSHOT_TIME
import jetbrains.livemap.tiles.components.StatisticsComponent

internal class DebugTileDataRenderer(
    private val myStats: StatisticsComponent,
    private val mySystemTime: SystemTime,
    private val myTileDataRenderer: TileDataRenderer
) : TileDataRenderer {

    override fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: CellLayerKind
    ): MicroTask<Async<Canvas.Snapshot>> {
        val microTask = myTileDataRenderer.render(canvas, tileFeatures, cellKey, layerKind)

        val renderKey: String
        val snapshotKey: String
        when (layerKind) {
            CellLayerKind.WORLD -> {
                renderKey = WORLD_RENDER_TIME
                snapshotKey = WORLD_SNAPSHOT_TIME
            }
            CellLayerKind.LABEL -> {
                renderKey = LABEL_RENDER_TIME
                snapshotKey = LABEL_SNAPSHOT_TIME
            }
            CellLayerKind.HTTP -> {
                renderKey = HTTP_TILE_RENDER_TIME
                snapshotKey = HTTP_TILE_SNAPSHOT_TIME
            }
            CellLayerKind.DEBUG -> return microTask
        }

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