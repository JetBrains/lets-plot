/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.builder.assemble.PlotFacets
import jetbrains.datalore.plot.builder.assemble.TypedScaleProviderMap
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Meta.DATA_META
import jetbrains.datalore.plot.config.Option.Meta.Kind
import jetbrains.datalore.plot.config.Option.Plot.COORD
import jetbrains.datalore.plot.config.Option.Plot.FACET
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.Plot.TITLE
import jetbrains.datalore.plot.config.Option.Plot.TITLE_TEXT
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING

abstract class PlotConfig(
    opts: Map<String, Any>
) : OptionsAccessor(opts, DEF_OPTIONS) {
    val layerConfigs: List<LayerConfig>
    val facets: PlotFacets
    val scaleProvidersMap: TypedScaleProviderMap
    protected val scaleConfigs: List<ScaleConfig<*>>

    protected var sharedData: DataFrame? = null
        private set

    val title: String?
        get() = getMap(TITLE)[TITLE_TEXT] as String?

    protected open val isClientSide: Boolean
        get() = false

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

        scaleConfigs = createScaleConfigs(getList(SCALES) + DataMetaUtil.createScaleSpecs(opts))
        scaleProvidersMap = PlotConfigUtil.createScaleProviders(scaleConfigs)

        layerConfigs = createLayerConfigs(sharedData, scaleProvidersMap)

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
        val mergedOpts = HashMap<Aes<*>, MutableMap<Any, Any>>()
        for (opts in scaleOptionsList) {
            val optsMap = opts as Map<*, *>
            val aes = ScaleConfig.aesOrFail(optsMap)
            if (!mergedOpts.containsKey(aes)) {
                mergedOpts[aes] = HashMap()
            }

            @Suppress("UNCHECKED_CAST")
            mergedOpts[aes]!!.putAll(optsMap as Map<Any, Any>)
        }

        val result = ArrayList<ScaleConfig<Any>>()
        for (scaleOptions in mergedOpts.values) {
            result.add(ScaleConfig(scaleOptions))
        }
        return result
    }

    private fun createLayerConfigs(
        sharedData: DataFrame?,
        scaleProviderByAes: TypedScaleProviderMap
    ): List<LayerConfig> {

        val layerConfigs = ArrayList<LayerConfig>()
        val layerOptionsList = getList(LAYERS)
        for (layerOptions in layerOptionsList) {
            checkArgument(
                layerOptions is Map<*, *>,
                "Layer options: expected Map but was " + layerOptions!!::class.simpleName
            )
            val layerConfig = createLayerConfig(
                layerOptions as Map<*, *>,
                sharedData,
                getMap(MAPPING),
                DataMetaUtil.getAsDiscreteAesSet(getMap(DATA_META)),
                scaleProviderByAes
            )
            layerConfigs.add(layerConfig)
        }
        return layerConfigs
    }

    protected abstract fun createLayerConfig(
        layerOptions: Map<*, *>,
        sharedData: DataFrame?,
        plotMappings: Map<*, *>,
        plotDiscreteAes: Set<String>,
        scaleProviderByAes: TypedScaleProviderMap
    ): LayerConfig


    protected fun replaceSharedData(plotData: DataFrame?) {
        checkState(!isClientSide)   // This class is immutable on client-side
        checkArgument(plotData != null)
        sharedData = plotData
        update(DATA, DataFrameUtil.toMap(plotData!!))
    }

    companion object {
        private const val ERROR_MESSAGE = "__error_message"
        private val DEF_OPTIONS = mapOf(
            COORD to Option.CoordName.CARTESIAN
        )
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
