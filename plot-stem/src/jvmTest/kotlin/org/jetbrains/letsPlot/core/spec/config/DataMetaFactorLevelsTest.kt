/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.config.AsDiscreteTest.*
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import kotlin.test.Test
import kotlin.test.assertEquals

class DataMetaFactorLevelsTest {

    private val myData = """{
        'name': ['b', 'c', 'a', 'd'],
        'c': [4, 1, 3, 2]
    }""".trimIndent()

    private val myMapping = "{'x': 'name', 'y': 'c'}"

    @Test
    fun default() {
        val spec = makePlotSpec(seriesAnnotations = null)
        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("b", "c", "a", "d"))
            .assertDistinctValues("c", listOf(4.0, 1.0, 3.0, 2.0))
    }

    private fun withSeriesAnnotations(
        dataStorage: Storage,
        mappingStorage: Storage
    ) {
        val spec = makePlotSpec(
            seriesAnnotations = withFactorLevels(
                mapOf(
                    "name" to listOf("a", "b", "c", "d"),
                    "c" to listOf(1.0, 2.0, 3.0, 4.0)
                )
            ),
            dataStorage,
            mappingStorage
        )
        transformToClientPlotConfig(spec)
            .assertVariable("name", isDiscrete = true)
            .assertVariable("c", isDiscrete = true)
            .assertDistinctValues("name", listOf("a", "b", "c", "d"))
            .assertDistinctValues("c", listOf(1.0, 2.0, 3.0, 4.0))
    }

    // settings in plot/layer spec

    @Test
    fun plot_LayerDataMapping() {
        withSeriesAnnotations(
            dataStorage = Storage.LAYER,
            mappingStorage = Storage.LAYER
        )
    }

    @Test
    fun plotDataMapping_Layer() {
        withSeriesAnnotations(
            dataStorage = Storage.PLOT,
            mappingStorage = Storage.PLOT
        )
    }

    @Test
    fun plotData_LayerMapping() {
        withSeriesAnnotations(
            dataStorage = Storage.PLOT,
            mappingStorage = Storage.LAYER
        )
    }

    @Test
    fun plotMapping_LayerData() {
        withSeriesAnnotations(
            dataStorage = Storage.LAYER,
            mappingStorage = Storage.PLOT
        )
    }

    @Test
    fun `should extend levels with missing values`() {
        val spec = makePlotSpec(
            seriesAnnotations = withFactorLevels(
                mapOf(
                    "name" to listOf("a", "b"),
                    "c" to listOf(1.0, 2.0)
                )
            )
        )

        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("a", "b", "c", "d"))
            .assertDistinctValues("c", listOf(1.0, 2.0, 4.0, 3.0))
    }

    @Test
    fun `with reverse ordering`() {
        val spec = makePlotSpec(
            seriesAnnotations = withFactorLevels(
                mapOf(
                    "name" to listOf("a", "b", "c", "d"),
                    "c" to listOf(1.0, 2.0, 3.0, 4.0)
                ),
                order = -1
            )
        )

        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("d", "c", "b", "a"))
            .assertDistinctValues("c", listOf(4.0, 3.0, 2.0, 1.0))
    }

    @Test
    fun `should extend levels with missing values - with reverse ordering`() {
        val spec = makePlotSpec(
            seriesAnnotations = withFactorLevels(
                mapOf(
                    "name" to listOf("a", "b"),
                    "c" to listOf(1.0, 2.0)
                ),
                order = -1
            )
        )

        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("d", "c", "b", "a"))
            .assertDistinctValues("c", listOf(3.0, 4.0, 2.0, 1.0))
    }

    private fun makePlotSpec(
        seriesAnnotations: String?,
        dataStorage: Storage = Storage.LAYER,
        mappingStorage: Storage = Storage.LAYER,
        data: String = myData,
        mapping: String = myMapping,
    ): String {
        val dataSpec = "\'data\': $data,"
        val mappingSpec = "\'mapping\': $mapping,"
        val annotation = seriesAnnotations?.let { """'data_meta': { $seriesAnnotations },""" } ?: ""
        return """{
              'kind': 'plot',
              ${dataSpec.takeIf { dataStorage == Storage.PLOT } ?: ""}
              ${annotation.takeIf { mappingStorage == Storage.PLOT } ?: ""}
              ${mappingSpec.takeIf { mappingStorage == Storage.PLOT } ?: ""}
              'layers': [
                {
                    ${dataSpec.takeIf { dataStorage == Storage.LAYER } ?: ""}
                    ${annotation.takeIf { mappingStorage == Storage.LAYER } ?: ""}
                    ${mappingSpec.takeIf { mappingStorage == Storage.LAYER } ?: ""}
                    'geom': 'bar', 'stat': 'identity'
                }
              ]
            }""".trimIndent()
    }

    private fun withFactorLevels(varListWithLevels: Map<String, List<Any>>, order: Int = 1): String {
        val seriesAnnotations = varListWithLevels.toList().joinToString { (variable, factorLevels) ->
            val factorStringList = factorLevels.joinToString { if (it is String) "\'$it\'" else "$it" }
            """{
                'column': '$variable',
                'factor_levels': [ $factorStringList ],
                'order': $order
            }""".trimIndent()
        }
        return "'series_annotations': [$seriesAnnotations]"
    }

    companion object {
        fun PlotConfigFrontend.assertDistinctValues(
            varName: String,
            expectedFactors: List<Any>
        ): PlotConfigFrontend {
            val layer = layerConfigs.single()
            val variable = DataFrameUtil.findVariableOrFail(layer.combinedData, varName)
            val actual = layer.combinedData.distinctValues(variable).toList()
            assertEquals(expectedFactors.size, actual.size, "Wrong number of factors")
            for (index in expectedFactors.indices) {
                assertEquals(expectedFactors[index], actual[index],
                    "variable '$varName', expected: $expectedFactors, actual: $actual\n")
            }
            return this
        }
    }
}