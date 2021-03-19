/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap

object GeomLayerListUtil {

    fun anyBoundXScale(
        scaleMap: TypedScaleMap,
        layersByTile: List<List<GeomLayer>>
    ): Scale<Double>? {
        for (layer in layersByTile[0]) {
            if (layer.hasBinding(Aes.X)) {
                return scaleMap[Aes.X]
            }
        }
        return null
    }

    fun anyBoundYScale(
        scaleByAes: TypedScaleMap,
        layersByTile: List<List<GeomLayer>>
    ): Scale<Double>? {
        for (layer in layersByTile[0]) {
            if (layer.hasBinding(Aes.Y)) {
                return scaleByAes[Aes.Y]
            }
        }
        return null
    }
}
