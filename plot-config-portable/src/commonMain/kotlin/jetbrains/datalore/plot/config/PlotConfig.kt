/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Plot.CAPTION
import jetbrains.datalore.plot.config.Option.Plot.CAPTION_TEXT
import jetbrains.datalore.plot.config.Option.Plot.FACET
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.Plot.SUBTITLE_TEXT
import jetbrains.datalore.plot.config.Option.Plot.TITLE
import jetbrains.datalore.plot.config.Option.Plot.TITLE_TEXT
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING

abstract class PlotConfig(
    opts: Map<String, Any>,
    private val isClientSide: Boolean
) : OptionsAccessor(opts, DEF_OPTIONS) {

    val layerConfigs: List<LayerConfig>
    val facets: PlotFacets

    protected val scaleConfigs: List<ScaleConfig<*>>
    protected val mapperProviderByAes: Map<Aes<*>, MapperProvider<*>>
    protected val scaleProviderByAes: Map<Aes<*>, ScaleProvider>
    protected val transformByAes: Map<Aes<*>, Transform>

    protected var sharedData: DataFrame
        private set

    val title: String?
        get() = getMap(TITLE)[TITLE_TEXT] as String?
    val subtitle: String?
        get() = getMap(TITLE)[SUBTITLE_TEXT] as String?
    val caption: String?
        get() = getMap(CAPTION)[CAPTION_TEXT] as String?

    val containsLiveMap: Boolean
        get() = layerConfigs.any(LayerConfig::isLiveMap)

    init {

        val (plotMappings, plotData) = DataMetaUtil.createDataFrame(
            options = this,
            commonData = DataFrame.Builder.emptyFrame(),
            commonDiscreteAes = emptySet(),
            commonMappings = emptyMap<Any, Any>(),
            isClientSide = isClientSide
        )

        sharedData = plotData

        if (!isClientSide) {
            update(MAPPING, plotMappings)
        }

        layerConfigs = createLayerConfigs(sharedData, isClientSide)

        // build all scales
        val excludeStatVariables = !isClientSide

        scaleConfigs = PlotConfigUtil.createScaleConfigs(DataMetaUtil.createScaleSpecs(opts) + getList(SCALES))

        mapperProviderByAes = PlotConfigMapperProviders.createMapperProviders(
            layerConfigs,
            scaleConfigs,
            excludeStatVariables
        )

        scaleProviderByAes = PlotConfigScaleProviders.createScaleProviders(
            layerConfigs,
            scaleConfigs,
            excludeStatVariables
        )

        transformByAes = PlotConfigTransforms.createTransforms(
            layerConfigs,
            scaleProviderByAes,
            mapperProviderByAes,
            excludeStatVariables
        )

        facets = if (has(FACET)) {
            val facetOptions = getMap(FACET)
            val facetConfig = FacetConfig(facetOptions)
            val dataByLayer = ArrayList<DataFrame>()
            for (layerConfig in layerConfigs) {
                dataByLayer.add(layerConfig.combinedData)
            }
            facetConfig.createFacets(dataByLayer)
        } else {
            PlotFacets.undefined()
        }
    }

    private fun createLayerConfigs(sharedData: DataFrame, isClientSide: Boolean): List<LayerConfig> {

        val layerConfigs = ArrayList<LayerConfig>()
        val layerOptionsList = getList(LAYERS)

        val isMapPlot = layerOptionsList
            .mapNotNull { layerOptions -> (layerOptions as? Map<*, *>)?.getString(Option.Layer.GEOM) }
            .map(Option.GeomName::toGeomKind)
            .any { it in listOf(GeomKind.LIVE_MAP, GeomKind.MAP) }

        for (layerOptions in layerOptionsList) {
            require(layerOptions is Map<*, *>) { "Layer options: expected Map but was ${layerOptions!!::class.simpleName}" }
            @Suppress("UNCHECKED_CAST")
            layerOptions as Map<String, Any>

            val layerConfig = createLayerConfig(
                layerOptions,
                sharedData,
                getMap(MAPPING),
                getMap(DATA_META),
                DataMetaUtil.getOrderOptions(this.mergedOptions, getMap(MAPPING)),
                isClientSide,
                isMapPlot
            )
            layerConfigs.add(layerConfig)
        }
        return layerConfigs
    }

    private fun createLayerConfig(
        layerOptions: Map<String, Any>,
        sharedData: DataFrame,
        plotMappings: Map<*, *>,
        plotDataMeta: Map<*, *>,
        plotOrderOptions: List<OrderOptionUtil.OrderOption>,
        isClientSide: Boolean,
        isMapPlot: Boolean
    ): LayerConfig {
        val geomName = layerOptions[Option.Layer.GEOM] as String
        val geomKind = Option.GeomName.toGeomKind(geomName)

        val geomProto = if (isClientSide) {
            GeomProtoClientSide(geomKind)
        } else {
            GeomProto(geomKind)
        }

        return LayerConfig(
            layerOptions,
            sharedData,
            plotMappings,
            plotDataMeta,
            plotOrderOptions,
            geomProto,
            clientSide = isClientSide,
            isMapPlot
        )
    }


    protected fun replaceSharedData(plotData: DataFrame) {
        check(!isClientSide)   // This class is immutable on client-side
        sharedData = plotData
        update(DATA, DataFrameUtil.toMap(plotData))
    }

    companion object {
        private const val ERROR_MESSAGE = "__error_message"
        private val DEF_OPTIONS: Map<String, Any> = emptyMap()
        internal const val PLOT_COMPUTATION_MESSAGES = "computation_messages"

        fun failure(message: String): Map<String, Any> {
            return mapOf(ERROR_MESSAGE to message)
        }

        fun assertFigSpecOrErrorMessage(opts: Map<String, Any>) {
            if (!isFailure(opts)) {
                assertFigSpec(opts)
            }
        }

        private fun assertFigSpec(opts: Map<String, Any>) {
            // Will throw an IllegalArgumentException is something is wrong.
            figSpecKind(opts)
        }

        fun isFailure(opts: Map<String, Any>): Boolean {
            return opts.containsKey(ERROR_MESSAGE)
        }

        fun getErrorMessage(opts: Map<String, Any>): String {
            return opts[ERROR_MESSAGE].toString()
        }

        fun figSpecKind(opts: Map<*, *>): FigKind {
            return FigKind.fromOption(opts[Meta.KIND]?.toString())
        }

        fun figSpecKind(opts: OptionsAccessor): FigKind {
            return FigKind.fromOption(opts.getStringSafe(Meta.KIND))
        }
    }
}
