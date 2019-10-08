package jetbrains.datalore.plot.builder

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Scale

object GeomLayerListUtil {
    fun containsLivemapLayer(layersByTile: List<List<jetbrains.datalore.plot.builder.GeomLayer>>): Boolean {
        return layersByTile.isNotEmpty() && jetbrains.datalore.plot.builder.GeomLayerListUtil.containsLivemapLayer2(
            layersByTile[0]
        )
    }

    fun anyBoundXScale(layersByTile: List<List<jetbrains.datalore.plot.builder.GeomLayer>>): Scale<*>? {
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

    fun anyBoundYScale(layersByTile: List<List<jetbrains.datalore.plot.builder.GeomLayer>>): Scale<*>? {
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

    fun containsLivemapLayer2(tileLayers: List<jetbrains.datalore.plot.builder.GeomLayer>): Boolean {
        for (layer in tileLayers) {
            if (layer.isLivemap) {
                return true
            }
        }
        return false
    }

    fun getLivemapLayer(tileLayers: List<jetbrains.datalore.plot.builder.GeomLayer>): jetbrains.datalore.plot.builder.GeomLayer {
        for (layer in tileLayers) {
            if (layer.isLivemap) {
                return layer
            }
        }
        throw IllegalStateException("Live Map layer not found")
    }
}
