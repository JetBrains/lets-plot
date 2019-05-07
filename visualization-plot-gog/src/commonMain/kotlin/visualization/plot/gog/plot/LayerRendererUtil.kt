package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.Geom
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapGeom

internal object LayerRendererUtil {
    fun createLivemapLayerRenderer(
            livemapLayer: GeomLayer,
            allLayers: List<GeomLayer>
    ): LivemapLayerRenderer {
        val noSharedNumericMappers = emptyMap<Aes<Double>, (Double?) -> Double>() // dummy maps
        val noOverallNumericDomains = emptyMap<Aes<Double>, ClosedRange<Double>>()

        var rendererData = createLayerRendererData(livemapLayer, noSharedNumericMappers, noOverallNumericDomains)
        val livemapRenderer = LivemapLayerRenderer(
                rendererData.aesthetics,
                rendererData.geom as LivemapGeom,
                rendererData.dataAccess
        )

        for (layer in allLayers) {
            if (!layer.isLivemap) {
                rendererData = createLayerRendererData(layer, noSharedNumericMappers, noOverallNumericDomains)
                livemapRenderer.addDataLayer(
                        rendererData.geom,
                        rendererData.geomKind,
                        rendererData.aesthetics,
                        rendererData.dataAccess
                )
            }
        }

        return livemapRenderer
    }

    fun createLayerRendererData(layer: GeomLayer,
                                sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
                                overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>): LayerRendererData {

        val aestheticMappers = PlotUtil.prepareLayerAestheticMappers(layer, sharedNumericMappers)
        val aesthetics = PlotUtil.createLayerAesthetics(layer, aestheticMappers, overallNumericDomains)
        val pos = PlotUtil.createLayerPos(layer, aesthetics)
        return LayerRendererData(layer.geom, layer.geomKind, aesthetics, aestheticMappers, pos, layer.dataAccess)
    }

    internal class LayerRendererData(
            val geom: Geom,
            val geomKind: GeomKind,
            val aesthetics: Aesthetics,
            val aestheticMappers: Map<Aes<*>, (Double?) -> Any?>,
            val pos: PositionAdjustment,
            val dataAccess: MappedDataAccess)
}
