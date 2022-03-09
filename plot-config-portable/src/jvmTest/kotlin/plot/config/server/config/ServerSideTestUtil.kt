/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.MonolithicCommon
import jetbrains.datalore.plot.config.LayerConfig
import jetbrains.datalore.plot.config.Option.Geom.Choropleth.GEO_POSITIONS
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Meta.KIND
import jetbrains.datalore.plot.config.Option.Meta.Kind.PLOT
import jetbrains.datalore.plot.config.Option.Meta.MAP_DATA_META
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.parsePlotSpec


object ServerSideTestUtil {
    private val emptyList = emptyList<Any>()
    private val emptyMap = emptyMap<String, Any>()

    @JvmOverloads
    internal fun parseOptionsServerSide(spec: String, dataOption: Map<String, List<*>>? = null): Map<String, Any> {
        val opts = parsePlotSpec(spec)
        if (dataOption != null) {
            opts[DATA] = dataOption
        }
        return serverTransformWithoutEncoding(opts)
    }

    fun serverTransformWithoutEncoding(plotSpec: MutableMap<String, Any>): Map<String, Any> {
        return PlotConfigServerSide.processTransform(plotSpec)
    }

    fun createLayerConfigsWithoutEncoding(plotSpec: MutableMap<String, Any>): List<LayerConfig> {
        @Suppress("NAME_SHADOWING")
        val plotSpec = serverTransformWithoutEncoding(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            throw IllegalStateException(errorMessage)
        }

        return PlotConfigServerSide(plotSpec).layerConfigs
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
