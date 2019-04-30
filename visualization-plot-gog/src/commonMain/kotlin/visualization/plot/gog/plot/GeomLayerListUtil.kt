package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2

object GeomLayerListUtil {
    fun containsLivemapLayer(layersByTile: List<List<GeomLayer>>): Boolean {
        return !layersByTile.isEmpty() && containsLivemapLayer2(layersByTile[0])
    }

    fun anyBoundXScale(layersByTile: List<List<GeomLayer>>): Scale2<*>? {
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

    fun anyBoundYScale(layersByTile: List<List<GeomLayer>>): Scale2<*>? {
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

    fun containsLivemapLayer2(tileLayers: List<GeomLayer>): Boolean {
        for (layer in tileLayers) {
            if (layer.isLivemap) {
                return true
            }
        }
        return false
    }

    fun getLivemapLayer(tileLayers: List<GeomLayer>): GeomLayer {
        for (layer in tileLayers) {
            if (layer.isLivemap) {
                return layer
            }
        }
        throw IllegalStateException("Live Map layer not found")
    }
}
