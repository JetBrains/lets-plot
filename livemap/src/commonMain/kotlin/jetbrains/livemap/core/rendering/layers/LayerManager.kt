/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.layers

interface LayerManager {
    fun createLayerRenderingSystem(): LayersRenderingSystem
    fun addLayer(name: String, group: LayerGroup): RenderLayerComponent
    fun createLayersOrderComponent(): LayersOrderComponent
}