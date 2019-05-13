package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import kotlin.test.assertEquals

object TestUtil {
    fun contourData(): Map<String, List<*>> {
        return DemoAndTest.contourDemoData()
    }


    fun assertClientWontFail(opts: Map<String, Any>): PlotConfigClientSide {
        return PlotConfigClientSide.create(opts)
    }

    fun getPlotData(plotSpec: Map<String, Any>): Map<String, Any> {
        return getMap(plotSpec, Option.Plot.DATA)
    }

    fun getLayerData(plotSpec: Map<String, Any>, layer: Int): Map<String, Any> {
        return layerDataList(plotSpec)[layer]
    }

    private fun layerDataList(plotSpec: Map<String, Any>): List<Map<String, Any>> {
        val layers = plotSpec[Option.Plot.LAYERS] as List<Map<String, Any>> ?: return emptyList()

        val result = ArrayList<Map<String, Any>>()
        for (layer in layers) {
            val layerData = HashMap(getMap(layer, Option.Layer.DATA))
            result.add(layerData)
        }
        return result
    }

    private fun getMap(opts: Map<String, Any>, key: String): Map<String, Any> {
        val map = opts[key]
        return map as?  Map<String, Any> ?: emptyMap()
    }

    fun checkOptionsClientSide(opts: Map<String, Any>, expectedNumLayers: Int) {
        val plotConfigClientSide = assertClientWontFail(opts)
        assertEquals(expectedNumLayers, plotConfigClientSide.layerConfigs.size)
    }
}
