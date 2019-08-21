package jetbrains.livemap.tiles

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.livemap.core.SystemTime
import jetbrains.livemap.core.multitasking.DebugMicroTask
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.LABEL_RENDER_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.LABEL_SNAPSHOT_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.WORLD_RENDER_TIME
import jetbrains.livemap.tiles.Components.DebugDataComponent.Companion.WORLD_SNAPSHOT_TIME

internal class DebugTileDataRenderer(
    private val myStats: Components.StatisticsComponent,
    private val myTileDataRenderer: TileDataRenderer
) : TileDataRenderer {

    private val mySystemTime: SystemTime = SystemTime()

    override fun render(
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: Components.CellLayerKind
    ): MicroTask<Async<Canvas.Snapshot>> {
        val microTask = myTileDataRenderer.render(tileFeatures, cellKey, layerKind)

        val renderKey: String
        val snapshotKey: String
        when (layerKind) {
            Components.CellLayerKind.WORLD -> {
                renderKey = WORLD_RENDER_TIME
                snapshotKey = WORLD_SNAPSHOT_TIME
            }
            Components.CellLayerKind.LABEL -> {
                renderKey = LABEL_RENDER_TIME
                snapshotKey = LABEL_SNAPSHOT_TIME
            }
            Components.CellLayerKind.DEBUG -> return microTask
        }

        val debugMicroTask = DebugMicroTask(microTask)
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