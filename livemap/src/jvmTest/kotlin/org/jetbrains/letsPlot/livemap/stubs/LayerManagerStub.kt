/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.stubs

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.core.layers.CanvasLayer
import org.jetbrains.letsPlot.livemap.core.layers.CanvasLayerComponent
import org.jetbrains.letsPlot.livemap.core.layers.LayerKind
import org.jetbrains.letsPlot.livemap.core.layers.LayerManager

class LayerManagerStub: LayerManager() {
    override fun addLayer(name: String, layerKind: LayerKind): CanvasLayerComponent {
        return CanvasLayerComponent(CanvasLayer(CanvasStub(), name, layerKind))
    }

    override fun removeLayer(canvasLayer: CanvasLayer): Unit = TODO("Not yet implemented")
    override fun pan(offset: Vec<org.jetbrains.letsPlot.livemap.Client>, dirtyLayers: List<CanvasLayer>): Unit = TODO("Not yet implemented")
    override fun repaint(dirtyLayers: List<CanvasLayer>): Unit = TODO("Not yet implemented")
    override fun repaintLayer(layer: CanvasLayer, offset: Vec<org.jetbrains.letsPlot.livemap.Client>): Unit = TODO("Not yet implemented")
}

