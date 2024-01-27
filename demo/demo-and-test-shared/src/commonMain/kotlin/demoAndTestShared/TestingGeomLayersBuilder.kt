/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared

import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.back.SpecTransformBackendUtil
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil

object TestingGeomLayersBuilder {
    fun createMultiTileGeomLayers(plotSpec: MutableMap<String, Any>): List<List<GeomLayer>> {
        val transformed = SpecTransformBackendUtil.processTransform(plotSpec)
        require(!PlotConfig.isFailure(transformed)) { PlotConfig.getErrorMessage(transformed) }
        val config = PlotConfigFrontend.create(transformed) {}
        return PlotConfigFrontendUtil.createPlotGeomTiles(config).coreLayersByTile()
    }

    fun createSingleTileGeomLayers(plotSpec: MutableMap<String, Any>): List<GeomLayer> {
        val coreLayersByTile = createMultiTileGeomLayers(plotSpec)
        return coreLayersByTile.single()
    }

    fun getSingleGeomLayer(plotSpec: MutableMap<String, Any>): GeomLayer {
        val geomLayers = createSingleTileGeomLayers(plotSpec)
        require(geomLayers.isNotEmpty())
        return geomLayers.single()
    }

    fun getSingleGeomLayer(spec: String): GeomLayer = getSingleGeomLayer(parsePlotSpec(spec))

    fun buildGeomLayer(
        geom: String,
        data: Map<String, Any?>,
        mapping: Map<String, Any>,
        tooltips: Any? = null,
        scales: List<Map<String, Any?>> = emptyList(),
        orientationY: Boolean = false
    ): GeomLayer {
        val plotOpts = mutableMapOf(
            Option.Meta.KIND to Option.Meta.Kind.PLOT,
            Option.PlotBase.DATA to data,
            Option.PlotBase.MAPPING to mapping,
            Option.Plot.LAYERS to listOf(
                mutableMapOf<String, Any?>().apply {
                    put(Option.Layer.GEOM, geom)
                    put(Option.Layer.TOOLTIPS, tooltips)
                    if (orientationY) {
                        put(Option.Layer.ORIENTATION, "Y")
                    }
                }
            ),
            Option.Plot.SCALES to scales,
        )
        return getSingleGeomLayer(plotOpts)
    }
}