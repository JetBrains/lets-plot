package jetbrains.datalore.visualization.plot.server.config

import jetbrains.datalore.visualization.plot.config.LayerConfig
import jetbrains.datalore.visualization.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.visualization.plot.config.Option.GeomName
import jetbrains.datalore.visualization.plot.config.Option.Layer.DATA
import jetbrains.datalore.visualization.plot.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.config.Option.Layer.MAPPING
import jetbrains.datalore.visualization.plot.config.Option.Layer.POS
import jetbrains.datalore.visualization.plot.config.Option.Layer.STAT
import jetbrains.datalore.visualization.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.visualization.plot.config.Option.Meta.KIND
import jetbrains.datalore.visualization.plot.config.Option.Meta.Kind.PLOT
import jetbrains.datalore.visualization.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.visualization.plot.config.Option.Plot
import jetbrains.datalore.visualization.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.visualization.plot.config.Option.Plot.SCALES
import jetbrains.datalore.visualization.plot.config.transform.encode.DataSpecEncodeTransforms
import jetbrains.datalore.visualization.plot.parsePlotSpec


object ServerSideTestUtil {
    private val emptyList = emptyList<Any>()
    private val emptyMap = emptyMap<String, Any>()

    @JvmOverloads
    internal fun parseOptionsServerSide(spec: String, dataOption: Map<String, List<*>>? = null): Map<String, Any> {
        val opts = parsePlotSpec(spec)
        if (dataOption != null) {
            opts[Plot.DATA] = dataOption
        }
        return serverTransformWithoutEncoding(opts)
    }

    fun serverTransformWithoutEncoding(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        return PlotConfigServerSide.processTransformWithoutEncoding(plotSpec)
    }

    fun serverTransformOnlyEncoding(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        return DataSpecEncodeTransforms.serverSideEncode(true).apply(plotSpec)
    }

    fun createLayerConfigsWithoutEncoding(plotSpec: MutableMap<String, Any>): List<LayerConfig> {
        @Suppress("NAME_SHADOWING")
        val plotSpec = serverTransformWithoutEncoding(plotSpec)
        return PlotConfigServerSide.createLayerConfigsWithoutEncoding(plotSpec)
    }

    internal fun createLayerConfigsByLayerSpec(layerSpec: Map<String, Any?>): List<LayerConfig> {
        return createLayerConfigsWithoutEncoding(
                mutableMapOf(
                        KIND to PLOT,
                        SCALES to emptyList,
                        LAYERS to listOf(
                                layerSpec
                        )
                )
        )
    }

    internal fun geoPositionsDict(geoReference: Map<String, Any>, geoReferenceMeta: Map<String, Any>): Map<String, Any?> {
        return mapOf(
                GEOM to GeomName.POLYGON,
                DATA to null,
                DATA_META to null,
                GEO_POSITIONS to geoReference,
                MAP_DATA_META to geoReferenceMeta,
                MAPPING to emptyMap,
                STAT to null,
                POS to null
        )
    }
}
