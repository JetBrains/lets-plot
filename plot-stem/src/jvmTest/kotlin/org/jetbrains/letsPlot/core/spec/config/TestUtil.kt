/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Plot.LAYERS
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy
import kotlin.test.assertEquals
import kotlin.test.fail

object TestUtil {
    fun assertPlotWontFail(plotSpecProcessed: Map<String, Any>) {
        val buildResult = MonolithicCommon.buildPlotsFromProcessedSpecs(
            plotSpecProcessed,
            containerSize = DoubleVector(600, 400),
            sizingPolicy = SizingPolicy.notebookCell()
        )

        if (buildResult.isError) {
            val errorMessage = (buildResult as MonolithicCommon.PlotsBuildResult.Error).error
            fail("Error when building plot: $errorMessage")
        }
    }

    fun createPlotConfigFrontend(plotSpecProcessed: Map<String, Any>): PlotConfigFrontend {
        return PlotConfigFrontend.create(plotSpecProcessed) {}
    }

    fun getPlotData(plotSpec: Map<String, Any>): Map<String, Any> {
        return getMap(plotSpec, DATA)
    }

    fun getLayerData(plotSpec: Map<String, Any>, layer: Int): Map<String, Any> {
        return layerDataList(plotSpec)[layer]
    }

    private fun layerDataList(plotSpec: Map<String, Any>): List<Map<String, Any>> {
        @Suppress("UNCHECKED_CAST")
        val layers = plotSpec[LAYERS] as List<Map<String, Any>>

        val result = ArrayList<Map<String, Any>>()
        for (layer in layers) {
            val layerData = HashMap(getMap(layer, DATA))
            result.add(layerData)
        }
        return result
    }

    private fun getMap(opts: Map<String, Any>, key: String): Map<String, Any> {
        val map = opts[key]
        @Suppress("UNCHECKED_CAST")
        return map as? Map<String, Any> ?: emptyMap()
    }

    fun checkOptionsFrontend(opts: Map<String, Any>, expectedNumLayers: Int) {
        val plotConfigFrontend = createPlotConfigFrontend(opts)
        assertEquals(expectedNumLayers, plotConfigFrontend.layerConfigs.size)
    }

//    internal fun createMultiTileGeomLayers(plotSpec: MutableMap<String, Any>): List<List<GeomLayer>> {
//        val transformed = SpecTransformBackendUtil.processTransform(plotSpec)
//        require(!PlotConfig.isFailure(transformed)) { PlotConfig.getErrorMessage(transformed) }
//        val config = PlotConfigFrontend.create(transformed) {}
//        return PlotConfigFrontendUtil.createPlotAssembler(config).coreLayersByTile
//    }
//
//    internal fun createSingleTileGeomLayers(plotSpec: MutableMap<String, Any>): List<GeomLayer> {
//        val coreLayersByTile = createMultiTileGeomLayers(plotSpec)
//        return coreLayersByTile.single()
//    }
//
//    internal fun getSingleGeomLayer(plotSpec: MutableMap<String, Any>): GeomLayer {
//        val geomLayers = createSingleTileGeomLayers(plotSpec)
//        require(geomLayers.isNotEmpty())
//        return geomLayers.single()
//    }
//
//    internal fun getSingleGeomLayer(spec: String): GeomLayer = getSingleGeomLayer(parsePlotSpec(spec))
//
//    internal fun buildGeomLayer(
//        geom: String,
//        data: Map<String, Any?>,
//        mapping: Map<String, Any>,
//        tooltips: Any? = null,
//        scales: List<Map<String, Any?>> = emptyList(),
//        orientationY: Boolean = false
//    ): GeomLayer {
//        val plotOpts = mutableMapOf(
//            KIND to PLOT,
//            DATA to data,
//            MAPPING to mapping,
//            LAYERS to listOf(
//                mutableMapOf<String, Any?>().apply {
//                    put(GEOM, geom)
//                    put(TOOLTIPS, tooltips)
//                    if (orientationY) {
//                        put(ORIENTATION, "Y")
//                    }
//                }
//            ),
//            SCALES to scales,
//        )
//        return getSingleGeomLayer(plotOpts)
//    }

    internal fun buildPointLayer(
        data: Map<String, Any?>,
        mapping: Map<String, Any>,
        tooltips: Any? = null,
        scales: List<Map<String, Any?>> = emptyList(),
    ): GeomLayer {
        return TestingGeomLayersBuilder.buildGeomLayer(Option.GeomName.POINT, data, mapping, tooltips, scales)
    }
}