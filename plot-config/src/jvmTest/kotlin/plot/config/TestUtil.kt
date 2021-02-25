/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import kotlin.test.assertEquals

object TestUtil {
    fun contourData(): Map<String, List<*>> {
        return jetbrains.datalore.plot.DemoAndTest.contourDemoData()
    }


    fun assertClientWontFail(opts: Map<String, Any>): PlotConfigClientSide {
        return PlotConfigClientSide.create(opts) {}
    }

    fun getPlotData(plotSpec: Map<String, Any>): Map<String, Any> {
        return getMap(plotSpec, DATA)
    }

    fun getLayerData(plotSpec: Map<String, Any>, layer: Int): Map<String, Any> {
        return layerDataList(plotSpec)[layer]
    }

    private fun layerDataList(plotSpec: Map<String, Any>): List<Map<String, Any>> {
        @Suppress("UNCHECKED_CAST")
        val layers = plotSpec[Option.Plot.LAYERS] as List<Map<String, Any>>

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
}
