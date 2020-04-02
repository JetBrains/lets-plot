/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.transform.encode.DataFrameEncoding
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class PlotConfigServerSideTest {

    @Test
    fun testProcessTransform() {
        val plotValues = listOf(1.0, 2.0)
        val layerValues = listOf(3.0, 4.0)


        // ====================
        val plotSpecTransformed = run {
            // top level
            val plotSpec = HashMap<String, Any>()
            plotSpec[DATA] = createDataEntry(PLOT_VAR, plotValues)

            // layers
            val layerSpec = HashMap<String, Any>()

            // data in layer
            layerSpec[DATA] = createDataEntry(LAYER_VAR, layerValues)

            val layers = listOf<Any>(layerSpec)
            plotSpec[LAYERS] = layers

            ServerSideTestUtil.serverTransformOnlyEncoding(plotSpec)
        }

        @Suppress("ConstantConditionIf")
        if (!FeatureSwitch.USE_DATA_FRAME_ENCODING) {
            return
        }

        // ====================
        // top level
        assertDataEncoded(plotValues, plotSpecTransformed[DATA], PLOT_VAR)

        // layers
        val layers = plotSpecTransformed[LAYERS] as List<*>
        for (layerSpecObject in layers) {
            val layerSpec = layerSpecObject as Map<*, *>
            assertDataEncoded(layerValues, layerSpec[DATA], LAYER_VAR)
        }
    }

    @Test
    fun autoMapping() {
        val plotSpecTransformed: Map<String, Any>
        run {
            // top level
            val plotSpec = HashMap<String, Any>()
            plotSpec[DATA] = listOf(0.0, 1.0, 2.0, 3.0, 4.0)

            // histogram layer
            val layerSpec = HashMap<String, Any>()
            layerSpec[GEOM] = GeomName.HISTOGRAM

            val layers = listOf<Any>(layerSpec)
            plotSpec[LAYERS] = layers

            // ====================
            plotSpecTransformed = PlotConfigServerSideJvm.processTransformWithEncoding(plotSpec)
        }

        // Expecting 'auto-mapping': x -> __0

        // layers
        val layers = plotSpecTransformed[LAYERS] as List<*>
        assertEquals(1, layers.size.toLong())
        for (layerSpec in layers) {
            // top level in layer
            val spec = layerSpec as Map<*, *>
            val mapping = spec[MAPPING] as Map<*, *>

            assertEquals(1, mapping.size.toLong())
            assertTrue(mapping.containsKey("x"))
            assertEquals("__0", mapping["x"])
        }
    }

    companion object {
        private const val PLOT_VAR = "plot-var"
        private const val LAYER_VAR = "layer-var"

        private fun createDataEntry(`var`: String, values: List<Double>): Map<String, List<Double>> {
            val map = HashMap<String, List<Double>>()
            map[`var`] = values
            return map
        }

        private fun assertDataEncoded(expectedValues: List<Double?>, transformedData: Any?, varName: String) {
//            assertTrue(transformedData is Map<*, *>)

            @Suppress("UNCHECKED_CAST")
            val map = transformedData as Map<String, *>

            assertTrue(DataFrameEncoding.isEncodedDataSpec(map))

            val dataFrame = DataFrameEncoding.decode1(map)
            assertTrue(dataFrame.containsKey(varName))

            @Suppress("UNCHECKED_CAST")
            val values = dataFrame[varName] as List<Double?>

            assertEquals(expectedValues, values)
        }
    }

}