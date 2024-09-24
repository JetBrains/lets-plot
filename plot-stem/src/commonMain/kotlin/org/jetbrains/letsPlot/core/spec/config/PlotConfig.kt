/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.assemble.PlotFacets
import org.jetbrains.letsPlot.core.plot.builder.data.OrderOptionUtil
import org.jetbrains.letsPlot.core.plot.builder.scale.MapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProvider
import org.jetbrains.letsPlot.core.spec.*
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Meta.DATA_META
import org.jetbrains.letsPlot.core.spec.Option.Plot.CAPTION
import org.jetbrains.letsPlot.core.spec.Option.Plot.CAPTION_TEXT
import org.jetbrains.letsPlot.core.spec.Option.Plot.FACET
import org.jetbrains.letsPlot.core.spec.Option.Plot.LAYERS
import org.jetbrains.letsPlot.core.spec.Option.Plot.SCALES
import org.jetbrains.letsPlot.core.spec.Option.Plot.SUBTITLE_TEXT
import org.jetbrains.letsPlot.core.spec.Option.Plot.TITLE
import org.jetbrains.letsPlot.core.spec.Option.Plot.TITLE_TEXT
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import org.jetbrains.letsPlot.core.spec.conversion.ColorOptionConverter

abstract class PlotConfig(
    opts: Map<String, Any>,
    containerTheme: Theme?,
    private val isClientSide: Boolean,
) : OptionsAccessor(opts, DEF_OPTIONS) {

    val theme: Theme
    val aopConversion: AesOptionConversion
    val layerConfigs: List<LayerConfig>
    val facets: PlotFacets

    protected val scaleConfigs: List<ScaleConfig<*>>
    internal val mapperProviderByAes: Map<Aes<*>, MapperProvider<*>>
    internal val scaleProviderByAes: Map<Aes<*>, ScaleProvider>

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
        val fontFamilyRegistry = FontFamilyRegistryConfig(this).createFontFamilyRegistry()
        val ownTheme = ThemeConfig(getMap(Option.Plot.THEME), fontFamilyRegistry).theme
        theme = if (containerTheme == null || hasOwn(Option.Plot.THEME)) {
            ownTheme
        } else {
            ownTheme.toInherited(containerTheme)
        }

        aopConversion = AesOptionConversion(
            ColorOptionConverter(
                pen = theme.colors().pen(),
                paper = theme.colors().paper(),
                brush = theme.colors().brush(),
            )
        )

        sharedData = ConfigUtil.createDataFrame(get(DATA))

        layerConfigs = createLayerConfigs(sharedData, isClientSide)

        // build all scales
        val excludeStatVariables = !isClientSide
        scaleConfigs = PlotConfigUtil.createScaleConfigs(
            scaleOptionsList = DataMetaUtil.createScaleSpecs(opts) + getList(SCALES),
            aopConversion = aopConversion
        )

        mapperProviderByAes = PlotConfigMapperProviders.createMapperProviders(
            layerConfigs,
            scaleConfigs,
            excludeStatVariables
        )

        val zeroPositionalExpands = !CoordConfig.allowsDomainExpand(get(Option.Plot.COORD))

        scaleProviderByAes = PlotConfigScaleProviders.createScaleProviders(
            layerConfigs,
            scaleConfigs,
            excludeStatVariables,
            zeroPositionalExpands,
            theme.exponentFormat
        )

        facets = if (has(FACET)) {
            val facetOptions = getMap(FACET)
            val facetConfig = FacetConfig(facetOptions, theme.exponentFormat)
            val dataByLayer = ArrayList<DataFrame>()
            for (layerConfig in layerConfigs) {
                dataByLayer.add(layerConfig.combinedData)
            }
            facetConfig.createFacets(dataByLayer)
        } else {
            PlotFacets.UNDEFINED
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
                plotMappings = getMap(MAPPING).mapValues { (_, variable) -> variable as String },
                plotDataMeta = getMap(DATA_META),
                plotOrderOptions = DataMetaUtil.getOrderOptions(this.toMap(), getMap(MAPPING), isClientSide),
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
        plotMappings: Map<String, String>,
        plotDataMeta: Map<String, Any>,
        plotOrderOptions: List<OrderOptionUtil.OrderOption>,
        isClientSide: Boolean,
        isMapPlot: Boolean
    ): LayerConfig {
        val geomName = layerOptions[Option.Layer.GEOM] as String
        val geomKind = Option.GeomName.toGeomKind(geomName)

        val geomProto = GeomProto(geomKind)
        return LayerConfig(
            layerOptions,
            sharedData,
            plotMappings,
            plotDataMeta,
            plotOrderOptions,
            geomProto,
            aopConversion = aopConversion,
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
