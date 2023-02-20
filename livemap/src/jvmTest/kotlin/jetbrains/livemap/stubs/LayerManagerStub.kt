/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.stubs

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.livemap.Client
import jetbrains.livemap.core.layers.CanvasLayer
import jetbrains.livemap.core.layers.CanvasLayerComponent
import jetbrains.livemap.core.layers.LayerKind
import jetbrains.livemap.core.layers.LayerManager

class LayerManagerStub: LayerManager() {
    override fun addLayer(name: String, layerKind: LayerKind): CanvasLayerComponent {
        return CanvasLayerComponent(CanvasLayer(CanvasStub(), name, layerKind))
    }

    override fun removeLayer(canvasLayer: CanvasLayer): Unit = TODO("Not yet implemented")
    override fun pan(offset: Vec<Client>, dirtyLayers: List<CanvasLayer>): Unit = TODO("Not yet implemented")
    override fun repaint(dirtyLayers: List<CanvasLayer>): Unit = TODO("Not yet implemented")
    override fun repaintLayer(layer: CanvasLayer, offset: Vec<Client>): Unit = TODO("Not yet implemented")
}

