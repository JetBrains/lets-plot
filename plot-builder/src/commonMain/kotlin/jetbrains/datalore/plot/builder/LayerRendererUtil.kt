package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.geom.LiveMapGeom
import jetbrains.datalore.visualization.plot.base.geom.LiveMapLayerData
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess

internal object LayerRendererUtil {
    fun createLiveMapFigure(
        layers: List<GeomLayer>,
        dimension: DoubleVector
    ): CanvasFigure {

        require(layers.isNotEmpty())
        require(layers.first().isLiveMap) { "geom_livemap have to be the very first geom after ggplot()"}

        // liveMap uses raw positions, so no mappings needed
        val newLiveMapLayerRendererData = { layer: GeomLayer -> createLayerRendererData(layer, emptyMap(), emptyMap()) }

        val liveMapRendererData = newLiveMapLayerRendererData(layers.first())
        val layersRendererData = layers
            .drop(1) // skip geom_livemap
            .map(newLiveMapLayerRendererData)
            .map { with(it) { LiveMapLayerData(geom, geomKind, aesthetics, dataAccess) } }


        return (liveMapRendererData.geom as LiveMapGeom).createCanvasFigure(
            liveMapRendererData.aesthetics,
            liveMapRendererData.dataAccess,
            dimension,
            layersRendererData
        )
    }

    fun createLayerRendererData(layer: GeomLayer,
                                sharedNumericMappers: Map<Aes<Double>, (Double?) -> Double?>,
                                overallNumericDomains: Map<Aes<Double>, ClosedRange<Double>>
    ): LayerRendererData {

        val aestheticMappers =
            PlotUtil.prepareLayerAestheticMappers(layer, sharedNumericMappers)
        val aesthetics = PlotUtil.createLayerAesthetics(
            layer,
            aestheticMappers,
            overallNumericDomains
        )
        val pos = PlotUtil.createLayerPos(layer, aesthetics)
        return LayerRendererData(
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
