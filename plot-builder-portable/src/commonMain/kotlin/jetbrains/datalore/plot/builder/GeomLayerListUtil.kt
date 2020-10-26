/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale

object GeomLayerListUtil {

    fun anyBoundXScale(layersByTile: List<List<GeomLayer>>): Scale<Double>? {
        for (layer in layersByTile[0]) {
            if (layer.hasBinding(Aes.X)) {
                @Suppress("UNCHECKED_CAST")
                return layer.getBinding(Aes.X).scale as Scale<Double>?
            }
            for (aes in layer.renderedAes()) {
                if (Aes.isPositionalX(aes)) {
                    if (layer.hasBinding(aes)) {
                        @Suppress("UNCHECKED_CAST")
                        return layer.getBinding(aes).scale as Scale<Double>?
                    }
                }
            }
        }
        return null
    }

    fun anyBoundYScale(layersByTile: List<List<GeomLayer>>): Scale<Double>? {
        for (layer in layersByTile[0]) {
            if (layer.hasBinding(Aes.Y)) {
                @Suppress("UNCHECKED_CAST")
                return layer.getBinding(Aes.Y).scale as Scale<Double>?
            }
            for (aes in layer.renderedAes()) {
                if (Aes.isPositionalY(aes)) {
                    if (layer.hasBinding(aes)) {
                        @Suppress("UNCHECKED_CAST")
                        return layer.getBinding(aes).scale as Scale<Double>?
                    }
                }
            }
        }
        return null
    }
}
