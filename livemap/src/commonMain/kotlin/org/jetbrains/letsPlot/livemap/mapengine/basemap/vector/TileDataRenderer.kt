/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector

import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.livemap.core.multitasking.MicroTask
import org.jetbrains.letsPlot.livemap.mapengine.basemap.BasemapLayerKind
import org.jetbrains.letsPlot.livemap.mapengine.viewport.CellKey

internal interface TileDataRenderer {
    fun render(
        canvas: Canvas,
        tileFeatures: Map<String, List<TileFeature>>,
        cellKey: CellKey,
        layerKind: BasemapLayerKind
    ): MicroTask<Unit>
}