/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

internal object MarginalLayerUtil {
    fun marginalLayersByMargin(marginalLayers: List<GeomLayer>): Map<MarginSide, List<GeomLayer>> {
        return marginalLayers
            .fold(LinkedHashMap<MarginSide, MutableList<GeomLayer>>()) { map, layer ->
                map.getOrPut(layer.marginalSide, ::ArrayList).add(layer)
                map
            }
    }
}