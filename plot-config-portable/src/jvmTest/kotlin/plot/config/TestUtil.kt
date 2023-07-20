/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import demoAndTestShared.parsePlotSpec
import jetbrains.datalore.plot.DemoAndTest
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Layer.GEOM
import org.jetbrains.letsPlot.core.spec.Option.Layer.ORIENTATION
import org.jetbrains.letsPlot.core.spec.Option.Layer.TOOLTIPS
import org.jetbrains.letsPlot.core.spec.Option.Meta.KIND
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.PLOT
import org.jetbrains.letsPlot.core.spec.Option.Plot.LAYERS
import org.jetbrains.letsPlot.core.spec.Option.Plot.SCALES
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil
import kotlin.test.assertEquals

object TestUtil {
    fun contourData(): Map<String, List<*>> {
        return DemoAndTest.contourDemoData()
    }

    fun assertClientWontFail(opts: Map<String, Any>): PlotConfigFrontend {
        return PlotConfigFrontend.create(opts) {}
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

    fun checkOptionsClientSide(opts: Map<String, Any>, expectedNumLayers: Int) {
        val plotConfigClientSide = assertClientWontFail(opts)
        assertEquals(expectedNumLayers, plotConfigClientSide.layerConfigs.size)
    }

    internal fun createMultiTileGeomLayers(plotSpec: MutableMap<String, Any>): List<List<GeomLayer>> {
        val transformed = SpecTransformBackendUtil.processTransform(plotSpec)
        require(!PlotConfig.isFailure(transformed)) { PlotConfig.getErrorMessage(transformed) }
        val config = PlotConfigFrontend.create(transformed) {}
        return PlotConfigFrontendUtil.createPlotAssembler(config).coreLayersByTile
    }

    internal fun createSingleTileGeomLayers(plotSpec: MutableMap<String, Any>): List<GeomLayer> {
        val coreLayersByTile = createMultiTileGeomLayers(plotSpec)
        return coreLayersByTile.single()
    }

    internal fun getSingleGeomLayer(plotSpec: MutableMap<String, Any>): GeomLayer {
        val geomLayers = createSingleTileGeomLayers(plotSpec)
        require(geomLayers.isNotEmpty())
        return geomLayers.single()
    }

    internal fun getSingleGeomLayer(spec: String): GeomLayer = getSingleGeomLayer(parsePlotSpec(spec))

    internal fun buildGeomLayer(
        geom: String,
        data: Map<String, Any?>,
        mapping: Map<String, Any>,
        tooltips: Any? = null,
        scales: List<Map<String, Any?>> = emptyList(),
        orientationY: Boolean = false
    ): GeomLayer {
        val plotOpts = mutableMapOf(
            KIND to PLOT,
            DATA to data,
            MAPPING to mapping,
            LAYERS to listOf(
                mutableMapOf<String, Any?>().apply {
                    put(GEOM, geom)
                    put(TOOLTIPS, tooltips)
                    if (orientationY) {
                        put(ORIENTATION, "Y")
                    }
                }
            ),
            SCALES to scales,
        )
        return getSingleGeomLayer(plotOpts)
    }

    internal fun buildPointLayer(
        data: Map<String, Any?>,
        mapping: Map<String, Any>,
        tooltips: Any? = null,
        scales: List<Map<String, Any?>> = emptyList(),
    ): GeomLayer {
        return buildGeomLayer(Option.GeomName.POINT, data, mapping, tooltips, scales)
    }
}