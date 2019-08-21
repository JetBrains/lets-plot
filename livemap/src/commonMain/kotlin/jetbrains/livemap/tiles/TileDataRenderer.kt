package jetbrains.livemap.tiles

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.gis.tileprotocol.TileFeature
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.projections.CellKey

internal interface TileDataRenderer {
    fun render(
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: Components.CellLayerKind
    ): MicroTask<Async<Canvas.Snapshot>>
}