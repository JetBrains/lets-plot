package jetbrains.datalore.plot.builder

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Scale

object GeomLayerListUtil {

    fun anyBoundXScale(layersByTile: List<List<GeomLayer>>): Scale<*>? {
        for (layer in layersByTile[0]) {
            if (layer.hasBinding(Aes.X)) {
                return layer.getBinding(Aes.X).scale
            }
            for (aes in layer.renderedAes()) {
                if (Aes.isPositionalX(aes)) {
                    if (layer.hasBinding(aes)) {
                        return layer.getBinding(aes).scale
                    }
                }
            }
        }
        return null
    }

    fun anyBoundYScale(layersByTile: List<List<GeomLayer>>): Scale<*>? {
        for (layer in layersByTile[0]) {
            if (layer.hasBinding(Aes.Y)) {
                return layer.getBinding(Aes.Y).scale
            }
            for (aes in layer.renderedAes()) {
                if (Aes.isPositionalY(aes)) {
                    if (layer.hasBinding(aes)) {
                        return layer.getBinding(aes).scale
                    }
                }
            }
        }
        return null
    }
}
