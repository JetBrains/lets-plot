/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.mapConfig

import org.jetbrains.letsPlot.commons.values.Color

class MapConfig private constructor(
    val tileSheetBackgrounds: Map<String, Color>,
    val layerNamesByZoom: Map<Int, List<String>>,
    val layers: Map<String, LayerConfig>,
    val colors: Map<String, Color>
) {
    fun getLayersByZoom(zoom: Int): List<String> {
        return layerNamesByZoom.getValue(zoom)
    }

    fun getLayerConfig(layerName: String): LayerConfig {
        return layers.getValue(layerName)
    }

    class MapConfigBuilder {
        private lateinit var tileSheetBackgrounds: Map<String, Color>
        private lateinit var layerNamesByZoom: Map<Int, List<String>>
        private lateinit var layers: Map<String, LayerConfig>
        private lateinit var colors: Map<String, Color>

        fun tileSheetBackgrounds(tileSheetBackgrounds: Map<String, Color>) = apply { this.tileSheetBackgrounds = tileSheetBackgrounds }
        fun layerNamesByZoom(layerNamesByZoom: Map<Int, List<String>>) = apply { this.layerNamesByZoom = layerNamesByZoom }
        fun layers(layers: Map<String, LayerConfig>) = apply { this.layers = layers }
        fun colors(colors: Map<String, Color>) = apply { this.colors = colors }

        fun build() = MapConfig(tileSheetBackgrounds, layerNamesByZoom, layers, colors)
    }
}
