/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.basemap.vector

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.livemap.viewport.CellKey
import jetbrains.livemap.basemap.BasemapLayerKind
import jetbrains.livemap.core.multitasking.MicroTask

internal interface TileDataRenderer {
    fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: BasemapLayerKind
    ): MicroTask<Async<Canvas.Snapshot>>
}