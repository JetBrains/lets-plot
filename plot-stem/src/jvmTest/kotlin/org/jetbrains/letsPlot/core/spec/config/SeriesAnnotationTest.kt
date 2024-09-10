/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DateTimeBreaksGen
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.spec.config.AsDiscreteTest.Storage
import org.jetbrains.letsPlot.core.spec.config.AsDiscreteTest.Storage.LAYER
import org.jetbrains.letsPlot.core.spec.config.AsDiscreteTest.Storage.PLOT
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import kotlin.test.*

class SeriesAnnotationTest {

    private val myData = """
        {
            "date": [946684800000.0, 949363200000.0, 951868800000.0],
            "y": [0, 0, 0]
        }
    """

    private val seriesAnnotation = """
            |"series_annotations": [
            |    {
            |        "column": "date",
            |        "type": "datetime"
            |    }
            |]
        """.trimMargin()
    private val mappingAnnotation = """
            |"mapping_annotations": [
            |    {
            |        "aes": "x",
            |        "annotation": "as_discrete"
            |    }
            ]""".trimMargin()


    private fun makePlotSpec(
        dataStorage: Storage,
        mappingStorage: Storage,
        dataMetaAnnotations: String = seriesAnnotation,
        scales: String? = null
    ): String {
        val data = "\"data\": $myData,"
        val mapping = "\"x\": \"date\", \"y\": \"y\""
        return """
            |{
            |  ${data.takeIf { dataStorage == PLOT } ?: ""}
            |  "data_meta": {
            |       ${dataMetaAnnotations.takeIf { mappingStorage == PLOT } ?: ""}
            |  },
            |  "mapping": {
            |    ${mapping.takeIf { mappingStorage == PLOT } ?: ""}
            |  },
            |  "kind": "plot",
            |  "scales": [ ${scales.takeIf { it != null } ?: ""} ],
            |  "layers": [
            |    {
            |      ${data.takeIf { dataStorage == LAYER } ?: ""}
            |      "data_meta": { 
            |           ${dataMetaAnnotations.takeIf { mappingStorage == LAYER } ?: ""}
            |      },
            |      "geom": "point",
            |      "mapping": {
            |        ${mapping.takeIf { mappingStorage == LAYER } ?: ""}
            |      }
            |    }
            |  ]
            |}
        """.trimMargin()
    }

    @Test
    fun `ggplot(data, mapping) + geom_point`() {
        val spec = makePlotSpec(
            dataStorage = PLOT,
            mappingStorage = PLOT
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, isDiscrete = false, name = "date")
            .assertDateTimeVariable(varName = "date", isDateTime = true)
    }

    @Test
    fun `ggplot + geom_point(data, mapping)`() {
        val spec = makePlotSpec(
            dataStorage = LAYER,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, isDiscrete = false, name = "date")
            .assertDateTimeVariable(varName = "date", isDateTime = true)
    }

    @Test
    fun `ggplot(data) + geom_point(mapping)`() {
        val spec = makePlotSpec(
            dataStorage = PLOT,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, isDiscrete = false, name = "date")
            .assertDateTimeVariable(varName = "date", isDateTime = true)
    }

    @Test
    fun `ggplot(mapping) + geom_point(data)`() {
        val spec = makePlotSpec(
            dataStorage = LAYER,
            mappingStorage = PLOT
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, isDiscrete = false, name = "date")
            .assertDateTimeVariable(varName = "date", isDateTime = true)
    }

    @Test
    fun `scale X is already defined`() {
        run {
            val spec = makePlotSpec(
                dataStorage = PLOT,
                mappingStorage = PLOT,
                scales = """{"aesthetic": "x", "reverse": true}"""
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = false, isDateTime = false, name = "date")
                .assertDateTimeVariable(varName = "date", isDateTime = true)
        }
        run {
            val spec = makePlotSpec(
                dataStorage = PLOT,
                mappingStorage = PLOT,
                scales = """{"aesthetic": "x", "discrete": true}"""
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = true, isDateTime = false, name = "date")
                .assertDateTimeVariable(varName = "date", isDateTime = true)
        }
    }

    @Test
    fun `as_discrete annotation is a higher priority`() {
        val dataMetaAnnotations = "$seriesAnnotation, $mappingAnnotation"
        run {
            val spec = makePlotSpec(
                dataStorage = PLOT,
                mappingStorage = PLOT,
                dataMetaAnnotations
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = true, isDateTime = false)
        }
        run {
            val spec = makePlotSpec(
                dataStorage = LAYER,
                mappingStorage = LAYER,
                dataMetaAnnotations
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = true, isDateTime = false)
        }
        run {
            val spec = makePlotSpec(
                dataStorage = PLOT,
                mappingStorage = LAYER,
                dataMetaAnnotations
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = true, isDateTime = false)
        }
        run {
            val spec = makePlotSpec(
                dataStorage = LAYER,
                mappingStorage = PLOT,
                dataMetaAnnotations
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = true, isDateTime = false)
        }
    }

    @Test
    fun twoLayers() {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "data": { "foo": [946684800000.0] },
                  "mapping": {
                    "x": "foo"
                  },
                  "data_meta": {
                    "series_annotations": [
                        {
                        "column": "foo",
                        "type": "datetime"
                        }
                    ]
                  }
                },
                {
                  "geom": "point",
                  "data": { "bar": [949363200000.0] },                  
                  "mapping": {
                    "x": "bar"
                  }                 
                }
              ]
            }""".trimIndent()

        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, isDiscrete = false, name = "x")
    }

    @Test
    fun `same variable name for different aes in two layers`() {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "data": { "foo": [946684800000.0] },
                  "mapping": {
                    "x": "foo"
                  },
                  "data_meta": {
                    "series_annotations": [
                        {
                        "column": "foo",
                        "type": "datetime"
                        }
                    ]
                  }
                },
                {
                  "geom": "point",
                  "data": { "bar": [949363200000.0],  "foo": [0.0]  },                  
                  "mapping": {
                    "x": "bar",
                    "y": "foo"
                  }                 
                }
              ]
            }""".trimIndent()

        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, isDiscrete = false, name = "x")
            .assertDateTimeScale(Aes.Y, isDateTime = false, isDiscrete = false, name = "foo")
    }

    @Test
    fun `ggplot(data) + geom_point(aes(x=array, y=var_from_data))`() {
        val spec = """
            {
              "data": {
                "date": [946684800000.0, 949363200000.0, 951868800000.0]
              },
              "data_meta": {
                "series_annotations": [
                  {
                    "column": "date",
                    "type": "datetime"
                  }
                ]
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "mapping": {
                    "x": [0, 0, 0],
                    "y": "date"
                  }
                }
              ]
            }
            """.trimIndent()

        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.Y, isDateTime = true, isDiscrete = false, name = "date")
            .assertDateTimeVariable(varName = "date", isDateTime = true)
    }

    private fun PlotConfigFrontend.assertDateTimeScale(
        aes: Aes<*>,
        isDateTime: Boolean,
        isDiscrete: Boolean,
        name: String? = null
    ): PlotConfigFrontend {
        val scale = createScales().getValue(aes)
        if (scale.isContinuous) {
            val breaksGenerator =
                (scale.getBreaksGenerator() as Transforms.BreaksGeneratorForTransformedDomain).breaksGenerator
            assertTrue(breaksGenerator is DateTimeBreaksGen == isDateTime, "Scale '${aes.name}' should be date-time")
        } else {
            assertFalse(isDateTime)
        }
        assertScale(aes, isDiscrete, name) { "Wrong 'isDiscrete' checking for aes='${aes.name}'" }
        return this
    }

    private fun PlotConfigFrontend.assertDateTimeVariable(
        varName: String,
        isDateTime: Boolean
    ): PlotConfigFrontend {
        val layer = layerConfigs.single()
        if (!DataFrameUtil.hasVariable(layer.combinedData, varName)) {
            fail("Variable $varName is not found in ${layer.combinedData.variables().map(DataFrame.Variable::name)}")
        }
        val dfVar = DataFrameUtil.findVariableOrFail(layer.combinedData, varName)
        assertEquals(isDateTime, layer.combinedData.isDateTime(dfVar))
        return this
    }
}