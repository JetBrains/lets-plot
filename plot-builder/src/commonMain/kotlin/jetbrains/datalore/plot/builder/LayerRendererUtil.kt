package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.geom.LiveMapGeom
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess

internal object LayerRendererUtil {
    fun createLivemapLayerRenderer(
        livemapLayer: jetbrains.datalore.plot.builder.GeomLayer,
        allLayers: List<jetbrains.datalore.plot.builder.GeomLayer>
    ): jetbrains.datalore.plot.builder.LivemapLayerRenderer {
        val noSharedNumericMappers = emptyMap<Aes<Double>, (Double?) -> Double>() // dummy maps
        val noOverallNumericDomains = emptyMap<Aes<Double>, ClosedRange<Double>>()

        var rendererData = jetbrains.datalore.plot.builder.LayerRendererUtil.createLayerRendererData(
            livemapLayer,
            noSharedNumericMappers,
            noOverallNumericDomains
        )
        val livemapRenderer = jetbrains.datalore.plot.builder.LivemapLayerRenderer(
            rendererData.aesthetics,
            rendererData.geom as LiveMapGeom,
            rendererData.dataAccess
        )

        for (layer in allLayers) {
            if (!layer.isLivemap) {
                rendererData = jetbrains.datalore.plot.builder.LayerRendererUtil.createLayerRendererData(
                    layer,
                    noSharedNumericMappers,
                    noOverallNumericDomains
                )
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

    fun createLayerRendererData(layer: jetbrains.datalore.plot.builder.GeomLayer,
                                sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
                                overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>): jetbrains.datalore.plot.builder.LayerRendererUtil.LayerRendererData {

        val aestheticMappers =
            jetbrains.datalore.plot.builder.PlotUtil.prepareLayerAestheticMappers(layer, sharedNumericMappers)
        val aesthetics = jetbrains.datalore.plot.builder.PlotUtil.createLayerAesthetics(
            layer,
            aestheticMappers,
            overallNumericDomains
        )
        val pos = jetbrains.datalore.plot.builder.PlotUtil.createLayerPos(layer, aesthetics)
        return jetbrains.datalore.plot.builder.LayerRendererUtil.LayerRendererData(
            layer.geom,
            layer.geomKind,
            aesthetics,
            aestheticMappers,
            pos,
            layer.dataAccess
        )
    }

    internal class LayerRendererData(
            val geom: Geom,
            val geomKind: GeomKind,
            val aesthetics: Aesthetics,
            val aestheticMappers: Map<Aes<*>, (Double?) -> Any?>,
            val pos: PositionAdjustment,
            val dataAccess: MappedDataAccess)
}
