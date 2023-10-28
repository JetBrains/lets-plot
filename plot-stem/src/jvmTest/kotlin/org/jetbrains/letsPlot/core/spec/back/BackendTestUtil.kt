/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.Option.Geom.Choropleth.GEO_POSITIONS
import org.jetbrains.letsPlot.core.spec.Option.GeomName
import org.jetbrains.letsPlot.core.spec.Option.Layer.GEOM
import org.jetbrains.letsPlot.core.spec.Option.Meta.MAP_DATA_META
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
import org.jetbrains.letsPlot.core.spec.config.LayerConfig
import org.jetbrains.letsPlot.core.spec.config.PlotConfig


object BackendTestUtil {
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
        return SpecTransformBackendUtil.processTransform(plotSpec)
    }

    /**
     * Single plot only (not GGBunch)
     */
    private fun createPlotConfig(plotSpecRaw: MutableMap<String, Any>): PlotConfigBackend {
        val (plotSpec,
            plotConfig) = SpecTransformBackendUtil.getTransformedSpecsAndPlotConfig(plotSpecRaw)
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

        return PlotConfigBackend(plotSpec, containerTheme = null).layerConfigs
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
