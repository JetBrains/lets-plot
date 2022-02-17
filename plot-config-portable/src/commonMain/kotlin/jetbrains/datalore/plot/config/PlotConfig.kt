/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.data.OrderOptionUtil
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.ScaleProvider
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.Kind
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
    protected val scaleProviderByAes: Map<Aes<*>, ScaleProvider<*>>
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
            commonDiscreteAes = emptySet<Any>(),
            commonMappings = emptyMap<Any, Any>(),
            isClientSide = isClientSide
        )

        sharedData = plotData

        if (!isClientSide) {
            update(MAPPING, plotMappings)
        }

        layerConfigs = createLayerConfigs(sharedData)

        // build all scales
        val excludeStatVariables = !isClientSide

        scaleConfigs = createScaleConfigs(getList(SCALES) + DataMetaUtil.createScaleSpecs(opts))

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

    fun createScaleConfigs(scaleOptionsList: List<*>): List<ScaleConfig<Any>> {
        // merge options by 'aes'
        val mergedOpts = HashMap<Aes<Any>, MutableMap<String, Any>>()
        for (opts in scaleOptionsList) {
            @Suppress("UNCHECKED_CAST")
            val optsMap = opts as Map<String, Any>

            @Suppress("UNCHECKED_CAST")
            val aes = ScaleConfig.aesOrFail(optsMap) as Aes<Any>
            if (!mergedOpts.containsKey(aes)) {
                mergedOpts[aes] = HashMap()
            }

            mergedOpts[aes]!!.putAll(optsMap)
        }

        return mergedOpts.map { (aes, options) ->
            ScaleConfig(aes, options)
        }
    }

    private fun createLayerConfigs(sharedData: DataFrame): List<LayerConfig> {

        val layerConfigs = ArrayList<LayerConfig>()
        val layerOptionsList = getList(LAYERS)
        for (layerOptions in layerOptionsList) {
            require(layerOptions is Map<*, *>) { "Layer options: expected Map but was ${layerOptions!!::class.simpleName}" }
            @Suppress("UNCHECKED_CAST")
            layerOptions as Map<String, Any>

            val layerConfig = createLayerConfig(
                layerOptions,
                sharedData,
                getMap(MAPPING),
                getMap(DATA_META),
                DataMetaUtil.getOrderOptions(this.mergedOptions, getMap(MAPPING))
            )
            layerConfigs.add(layerConfig)
        }
        return layerConfigs
    }

    protected abstract fun createLayerConfig(
        layerOptions: Map<String, Any>,
        sharedData: DataFrame,
        plotMappings: Map<*, *>,
        plotDataMeta: Map<*, *>,
        plotOrderOptions: List<OrderOptionUtil.OrderOption>
    ): LayerConfig


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

        fun assertPlotSpecOrErrorMessage(opts: Map<String, Any>) {
            val identified = isFailure(opts) ||
                    isPlotSpec(opts) ||
                    isGGBunchSpec(opts)

            if (!identified) {
                throw IllegalArgumentException("Invalid root feature kind: absent or unsupported  `kind` key")
            }
        }

        fun assertPlotSpec(opts: Map<String, Any>) {
            val identified = isPlotSpec(opts) || isGGBunchSpec(opts)
            if (!identified) {
                throw IllegalArgumentException("Invalid root feature kind: absent or unsupported  `kind` key")
            }
        }

        fun isFailure(opts: Map<String, Any>): Boolean {
            return opts.containsKey(ERROR_MESSAGE)
        }

        fun getErrorMessage(opts: Map<String, Any>): String {
            return opts[ERROR_MESSAGE].toString()
        }

        fun isPlotSpec(opts: Map<*, *>): Boolean {
            return Kind.PLOT == specKind(opts)
        }

        fun isGGBunchSpec(opts: Map<*, *>): Boolean {
            return Kind.GG_BUNCH == specKind(opts)
        }

        fun specKind(opts: Map<*, *>): Any? {
            return opts[Meta.KIND]
        }
    }
}
