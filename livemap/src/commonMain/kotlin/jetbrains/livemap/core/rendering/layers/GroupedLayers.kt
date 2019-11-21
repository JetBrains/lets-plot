/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.rendering.layers

class GroupedLayers {
    private val myGroupedLayers = HashMap<LayerGroup, MutableList<CanvasLayer>>()

    var orderedLayers: List<CanvasLayer> = emptyList()

    fun add(group: LayerGroup, layer: CanvasLayer) {
        myGroupedLayers.getOrPut(group, ::ArrayList).add(layer)
        orderedLayers = LayerGroup.values().flatMap { myGroupedLayers[it] ?: emptyList<CanvasLayer>() }
    }

    fun remove(group: LayerGroup, layer: CanvasLayer) {
        myGroupedLayers[group]?.remove(layer)
    }
}

enum class LayerGroup {
    BACKGROUND,
    FEATURES,
    FOREGROUND,
    UI
}