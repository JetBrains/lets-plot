package jetbrains.livemap.tiles.vector

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.CellKey
import jetbrains.livemap.tiles.components.CellLayerKind

internal interface TileDataRenderer {
    fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: CellLayerKind
    ): MicroTask<Async<Canvas.Snapshot>>
}