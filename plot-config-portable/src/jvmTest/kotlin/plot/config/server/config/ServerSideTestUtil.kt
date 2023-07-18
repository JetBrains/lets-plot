/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.LayerConfig
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.PlotConfig
import demoAndTestShared.parsePlotSpec


object ServerSideTestUtil {
    private val emptyList = emptyList<Any>()
    private val emptyMap = emptyMap<String, Any>()

    @JvmOverloads
    internal fun parseOptionsServerSide(spec: String, dataOption: Map<String, List<*>>? = null): Map<String, Any> {
        val opts = parsePlotSpec(spec)
        if (dataOption != null) {
            opts[DATA] = dataOption
        }
        return backendSpecTransform(opts)
    }

    fun backendSpecTransform(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        return BackendSpecTransformUtil.processTransform(plotSpec)
    }

    /**
     * Single plot only (not GGBunch)
     */
    private fun createPlotConfig(plotSpecRaw: MutableMap<String, Any>): PlotConfigServerSide {
        val (plotSpec,
            plotConfig) = BackendSpecTransformUtil.getTransformedSpecsAndPlotConfig(plotSpecRaw)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            throw IllegalStateException(errorMessage)
        }

        return plotConfig
    }

    fun createLayerConfigs(plotSpec: MutableMap<String, Any>): List<LayerConfig> {
        return createPlotConfig(plotSpec).layerConfigs
    }

    fun createLayerConfigsBeforeDataUpdate(plotSpec: MutableMap<String, Any>): List<LayerConfig> {
        @Suppress("NAME_SHADOWING")
        val plotSpec = backendSpecTransform(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            throw IllegalStateException(errorMessage)
        }

        return PlotConfigServerSide(plotSpec).layerConfigs
    }

    internal fun geomPolygonSpec(
        mapData: Map<String, Any>,
        mapDataMeta: Map<String, Any>
    ): Map<String, Any?> {
        return mapOf(
            GEOM to GeomName.POLYGON,
            GEO_POSITIONS to mapData,
            MAP_DATA_META to mapDataMeta,
            MAPPING to emptyMap
        )
    }
}
