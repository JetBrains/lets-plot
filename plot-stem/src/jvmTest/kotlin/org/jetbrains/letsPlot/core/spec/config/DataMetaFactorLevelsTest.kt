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

    private fun makePlotSpec(
        seriesAnnotations: String?,
        mappingAnnotations: String?,
        dataStorage: Storage = Storage.LAYER,
        mappingStorage: Storage = Storage.LAYER,
        data: String = myData,
        mapping: String = myMapping,
    ): String {
        val dataSpec = "\'data\': $data,"
        val mappingSpec = "\'mapping\': $mapping,"
        val annotations = listOfNotNull(seriesAnnotations, mappingAnnotations).joinToString()
        val annotation = """'data_meta': { $annotations },"""
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

    private fun mappingAnnotationsSpec(aesList: List<String>): String {
        val asDiscreteAnnotationsSpec = aesList.joinToString { aes ->
            """{
                'aes': '$aes',
                'annotation': 'as_discrete'
            }""".trimIndent()
        }
        return "'mapping_annotations': [$asDiscreteAnnotationsSpec]"
    }

    private fun seriesAnnotationsSpec(varListWithLevels: Map<String, List<Any>>): String {
        val seriesAnnotations = varListWithLevels.toList().joinToString { (variable, factorLevels) ->
            val factorStringList = factorLevels.joinToString { if (it is String) "\'$it\'" else "$it" }
            """{
                'column': '$variable',
                'factor_levels': [ $factorStringList ]
            }""".trimIndent()
        }
        return "'series_annotations': [$seriesAnnotations]"
    }

    private val myData = """{
        'name': ['a', 'b', 'c', 'd'],
        'c': [1, 2, 3, 4]
    }""".trimIndent()

    private val myMapping = "{'x': 'name', 'y': 'c', 'fill': 'c'}"

    @Test
    fun default() {
        val spec = makePlotSpec(
            seriesAnnotations = null,
            mappingAnnotations = null
        )
        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("a", "b", "c", "d"))
            .assertDistinctValues("c", listOf(1.0, 2.0, 3.0, 4.0))
    }

    @Test
    fun setupAsDiscreteAnnotations() {
        val spec = makePlotSpec(
            seriesAnnotations = null,
            mappingAnnotations = mappingAnnotationsSpec(listOf("x", "fill"))
        )
        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("a", "b", "c", "d"))
            .assertDistinctValues("c", listOf(1.0, 2.0, 3.0, 4.0))
            // + 'as_discrete' variables:
            .assertDistinctValues("x.name", listOf("a", "b", "c", "d"))
            .assertDistinctValues("fill.c", listOf(1.0, 2.0, 3.0, 4.0))
    }

    @Test
    fun setupSeriesAnnotations() {
        val spec = makePlotSpec(
            seriesAnnotations = seriesAnnotationsSpec(
                mapOf(
                    "name" to listOf("c", "b", "a"),
                    //"c" to listOf(2.0, 3.0, 1.0)
                )
            ),
            mappingAnnotations = null
        )
        transformToClientPlotConfig(spec)
            .assertDistinctValues("name", listOf("c", "b", "a", "d"))
        //.assertDistinctValues("c", listOf(1.0, 2.0, 3.0, 4.0))
    }

    private fun checkWithMappingAndSeriesAnnotations(
        dataStorage: Storage,
        mappingStorage: Storage
    ) {
        val spec = makePlotSpec(
            seriesAnnotations = seriesAnnotationsSpec(
                mapOf(
                    "name" to listOf("c", "b", "a"),
                    "c" to listOf(2.0, 3.0, 1.0)
                )
            ),
            mappingAnnotations = mappingAnnotationsSpec(listOf("x", "fill")),
            dataStorage,
            mappingStorage
        )
        transformToClientPlotConfig(spec)
            //.assertDistinctValues("name", listOf("c", "b", "a", "d"))
            // .assertDistinctValues("c", listOf(1.0, 2.0, 3.0, 4.0))
            .assertDistinctValues("x.name", listOf("c", "b", "a", "d"))
            .assertDistinctValues("fill.c", listOf(2.0, 3.0, 1.0, 4.0))
    }

    // settings in plot/layer spec

    @Test
    fun plot_LayerDataMapping() {
        checkWithMappingAndSeriesAnnotations(
            dataStorage = Storage.LAYER,
            mappingStorage = Storage.LAYER
        )
    }

    @Test
    fun plotDataMapping_Layer() {
        checkWithMappingAndSeriesAnnotations(
            dataStorage = Storage.PLOT,
            mappingStorage = Storage.PLOT
        )
    }

    @Test
    fun plotData_LayerMapping() {
        checkWithMappingAndSeriesAnnotations(
            dataStorage = Storage.PLOT,
            mappingStorage = Storage.LAYER
        )
    }

    @Test
    fun plotMapping_LayerData() {
        checkWithMappingAndSeriesAnnotations(
            dataStorage = Storage.LAYER,
            mappingStorage = Storage.PLOT
        )
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
                assertEquals(expectedFactors[index], actual[index], "expected: $expectedFactors, actual: $actual")
            }
            return this
        }
    }
}