/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.layers

interface LayerManager {
    fun createLayerRenderingSystem(): LayersRenderingSystem
    fun addLayer(name: String, group: LayerGroup): CanvasLayerComponent
    fun removeLayer(group: LayerGroup, canvasLayer: CanvasLayer)
    fun createLayersOrderComponent(): LayersOrderComponent
}