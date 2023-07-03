/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.base.intern.async.Async
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.livemap.core.multitasking.MicroTask
import jetbrains.livemap.mapengine.basemap.BasemapLayerKind
import jetbrains.livemap.mapengine.viewport.CellKey

internal interface TileDataRenderer {
    fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: BasemapLayerKind
    ): MicroTask<Async<Canvas.Snapshot>>
}