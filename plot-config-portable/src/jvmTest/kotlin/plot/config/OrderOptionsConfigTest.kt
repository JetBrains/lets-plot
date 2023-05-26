/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.LAYER
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.PLOT
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class OrderOptionsConfigTest {

    private val myData = """{ "foo" : [0], "bar": ["a"] }"""
    private val myMapping = """{ "x": "foo", "fill": "bar", "color" : "bar" }"""

    @Test
    // fill = as_discrete("bar", order=1)
    fun `set variable to order by itself with specified direction`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = null, order = 1)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "fill.bar", 1)
    }

    @Test
    // fill = as_discrete("bar", order_by="foo")
    fun `set variable to order by another field with default direction`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = "foo", order = null)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "foo", -1)
    }

    @Test
    // x = as_discrete("foo", order_by="foo", order=1),  fill = as_discrete("bar, order_by="foo")
    fun `two variables with different options`() {
        val orderingSettings =
            makeOrderingSettings(aes = "x", orderBy = "foo", order = 1) + "," +
                    makeOrderingSettings(aes = "fill", orderBy = "foo", order = null)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(2)
            .assertOrderOptions(0, "x.foo", "foo", 1)
            .assertOrderOptions(1, "fill.bar", "foo", -1)
    }

    @Test
    // fill = as_discrete("bar", order_by="foo", order=1), color = as_discrete("bar")
    fun `one variable without conflicts`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = "foo", order = null) + "," +
                    makeOrderingSettings(aes = "color", orderBy = null, order = null)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "foo", -1)
    }

    @Test
    // fill = as_discrete("bar", order_by="foo"), color = as_discrete("bar", order_by="foo")
    fun `one variable with the same options`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = "foo", order = null) + "," +
                    makeOrderingSettings(aes = "color", orderBy = "foo", order = null)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(2)
            .assertOrderOptions(0, "fill.bar", "foo", -1)
            .assertOrderOptions(1, "color.bar", "foo", -1)
    }


    @Test
    fun `fill = as_discrete('bar', order_by='foo'), color = as_discrete('bar', order=1)`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = "foo", order = null) + "," +
                    makeOrderingSettings(aes = "color", orderBy = null, order = 1)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(2)
            .assertOrderOptions(0, "fill.bar", "foo", -1)
            .assertOrderOptions(1, "color.bar", "color.bar", 1)
    }

    @Test
    fun `fill = as_discrete('bar', order=1), color = as_discrete('bar', order_by='foo', order=1)`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = null, order = 1) + "," +
                    makeOrderingSettings(aes = "color", orderBy = "foo", order = 1)

        transformToClientPlotConfig(makePlotSpec(orderingSettings))
            .assertOrderOptionsSize(2)
            .assertOrderOptions(0, "fill.bar", "fill.bar", 1)
            .assertOrderOptions(1, "color.bar", "foo", 1)
    }

    // Conflicting options error

    @Test
    // fill = as_discrete("bar", order_by="foo"), color = as_discrete("bar", order_by="bar")
    fun `conflicting options - different 'order_by' variable`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = "foo", order = null) + "," +
                    makeOrderingSettings(aes = "color", orderBy = "bar", order = null)

        assertFailed(
            makePlotSpec(orderingSettings),
            expectedMessage = "Multiple ordering options for the variable 'bar' with different non-empty 'order_by' fields: 'foo' and 'bar'"
        )
    }

    @Test
    // fill = as_discrete("bar", order=1), color = as_discrete("bar", order=-1)
    fun `conflicting options - different 'order direction' parameter`() {
        val orderingSettings =
            makeOrderingSettings(aes = "fill", orderBy = null, order = 1) + "," +
                    makeOrderingSettings(aes = "color", orderBy = null, order = -1)

        assertFailed(
            makePlotSpec(orderingSettings),
            expectedMessage =
            "Multiple ordering options for the variable 'bar' with different order direction: '1' and '-1'"
        )
    }

    @Test
    // in plot: color = as_discrete("bar", order_by="foo"),
    // in layer: color = as_discrete("bar", order_by="bar")
    fun `conflicting options for color - different 'order_by' variable`() {
        val plotOrderingSettings =
            makeOrderingSettings(aes = "color", orderBy = "foo", order = null)
        val layerOrderingSettings =
            makeOrderingSettings(aes = "color", orderBy = "bar", order = null)

        val spec = """{
            "kind": "plot",
            "data": { "foo" : [0, 1, 2], "bar": [4, 5, 3] },
            "mapping": { "x": "foo",  "color" : "bar" },
            "data_meta": { "mapping_annotations": [ $plotOrderingSettings ] },            
            "layers": [
                {
                    "data_meta": { "mapping_annotations": [ $layerOrderingSettings ] },
                    "mapping": { "x": "foo", "color" : "bar" },
                    "geom": "point"
                }
              ]
            }
        """.trimIndent()

        assertFailed(
            spec,
            expectedMessage = "Multiple ordering options for the variable 'bar' with different non-empty 'order_by' fields: 'foo' and 'bar'"
        )
    }

    @Test
    // in plot: color = as_discrete("bar", order=1),
    // in layer: color = as_discrete("bar", order=-1)
    fun `conflicting options for color -  different 'order direction' parameter`() {
        val plotOrderingSettings =
            makeOrderingSettings(aes = "color", orderBy = null, order = 1)
        val layerOrderingSettings =
            makeOrderingSettings(aes = "color", orderBy = null, order = -1)

        val spec = """{
            "kind": "plot",
            "data": { "foo" : [0, 1, 2], "bar": [4, 5, 3] },
            "mapping": { "x": "foo",  "color" : "bar" },
            "data_meta": { "mapping_annotations": [ $plotOrderingSettings ] },            
            "layers": [
                {
                    "data_meta": { "mapping_annotations": [ $layerOrderingSettings ] },
                    "mapping": { "x": "foo", "color" : "bar" },
                    "geom": "point"
                }
              ]
            }
        """.trimIndent()

        assertFailed(
            spec,
            expectedMessage =  "Multiple ordering options for the variable 'bar' with different order direction: '1' and '-1'"
        )
    }

    // plot/layer settings

    private val myOrderSetting = makeOrderingSettings(aes = "fill", orderBy = null, order = 1)

    @Test
    fun plot_LayerDataMapping() {
        val spec = makePlotSpec(
            myOrderSetting,
            dataStorage = LAYER,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "fill.bar", 1)
    }

    @Test
    fun plotDataMapping_Layer() {
        val spec = makePlotSpec(
            myOrderSetting,
            dataStorage = PLOT,
            mappingStorage = PLOT
        )
        transformToClientPlotConfig(spec)
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "fill.bar", 1)
    }

    @Test
    fun plotData_LayerMapping() {
        val spec = makePlotSpec(
            myOrderSetting,
            dataStorage = PLOT,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "fill.bar", 1)
    }

    @Test
    fun plotMapping_LayerData() {
        val spec = makePlotSpec(
            myOrderSetting,
            dataStorage = LAYER,
            mappingStorage = PLOT
        )
        transformToClientPlotConfig(spec)
            .assertOrderOptionsSize(1)
            .assertOrderOption("fill.bar", "fill.bar", 1)
    }


    private fun makeOrderingSettings(aes: String, orderBy: String?, order: Int?): String {
        val orderByVar = if (orderBy != null) {
            "\"" + "$orderBy" + "\""
        } else {
            null
        }
        return """{
                "aes": "$aes",
                "annotation": "as_discrete",
                "parameters": {
                    "order" : $order,
                    "order_by" : $orderByVar
                 }
            }""".trimIndent()
    }

    private fun makePlotSpec(
        annotations: String,
        dataStorage: AsDiscreteTest.Storage = LAYER,
        mappingStorage: AsDiscreteTest.Storage = LAYER,
    ): String {
        val data = "\"data\": $myData,"
        val mapping = "\"mapping\": $myMapping,"
        val annotation = """"data_meta": { "mapping_annotations": [ $annotations ] },"""
        return """{
              "kind": "plot",
              ${data.takeIf { dataStorage == PLOT } ?: ""}
              ${annotation.takeIf { mappingStorage == PLOT } ?: ""}
              ${mapping.takeIf { mappingStorage == PLOT } ?: ""}
              "layers": [
                {
                    ${data.takeIf { dataStorage == LAYER } ?: ""}
                    ${annotation.takeIf { mappingStorage == LAYER } ?: ""}
                    ${mapping.takeIf { mappingStorage == LAYER } ?: ""}
                    "geom": "point"
                }
              ]
            }""".trimIndent()
    }

    companion object {
        private fun PlotConfigClientSide.assertOrderOptionsSize(expectedSize: Int) : PlotConfigClientSide {
            val actualOptions = layerConfigs.first().orderOptions
            assertEquals(expectedSize, actualOptions.size)
            return this
        }

        private fun PlotConfigClientSide.assertOrderOptions(
            index: Int,
            expectedVariableName: String,
            expectedByVariable: String,
            expectedOrderDir: Int,
        ): PlotConfigClientSide {
            val actualOptions = layerConfigs.first().orderOptions
            assertTrue(index < actualOptions.size)
            val actual = actualOptions[index]
            assertEquals(expectedVariableName, actual.variableName)
            assertEquals(expectedByVariable, actual.byVariable ?: actual.variableName)
            assertEquals(expectedOrderDir, actual.getOrderDir())
            return this
        }

        private fun PlotConfigClientSide.assertOrderOption(
            expectedVariableName: String,
            expectedByVariable: String,
            expectedOrderDir: Int,
        ): PlotConfigClientSide {
            assertOrderOptions(index = 0, expectedVariableName, expectedByVariable, expectedOrderDir)
            return this
        }

        private fun assertFailed(spec: String, expectedMessage: String) {
            parsePlotSpec(spec)
                .let(ServerSideTestUtil::backendSpecTransform)
                .also {
                    require(PlotConfig.isFailure(it))
                    assertEquals(
                        expectedMessage,
                        PlotConfig.getErrorMessage(it)
                    )
                }
        }
    }
}