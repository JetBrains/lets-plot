/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.scale.transform.DateTimeBreaksGen
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.LAYER
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.PLOT
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SeriesAnnotationTest {

    private val myData = """
        {
            "date": [946684800000.0, 949363200000.0, 951868800000.0],
            "y": [0, 0, 0]
        }
    """

    private fun makePlotSpec(
        dataStorage: Storage,
        mappingStorage: Storage,
        scales: String? = null
    ): String {
        val data = "\"data\": $myData,"
        val mapping = "\"x\": \"date\", \"y\": \"y\""
        val annotation = """
            |"data_meta": {
            |    "series_annotations": [
            |        {
            |            "column": "date",
            |            "type": "datetime"
            |        }
            |    ]
            |},
        """.trimMargin()
        return """
            |{
            |  ${data.takeIf { dataStorage == PLOT } ?: ""}
            |  ${annotation.takeIf { mappingStorage == PLOT } ?: ""}
            |  "mapping": {
            |    ${mapping.takeIf { mappingStorage == PLOT } ?: ""}
            |  },
            |  "kind": "plot",
            |  "scales": [ ${scales.takeIf { it != null } ?: ""} ],
            |  "layers": [
            |    {
            |      ${data.takeIf { dataStorage == LAYER } ?: ""}
            |      ${annotation.takeIf { mappingStorage == LAYER } ?: ""}
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
            .assertDateTimeScale(Aes.X, isDateTime = true)
    }

    @Test
    fun `ggplot + geom_point(data, mapping)`() {
        val spec = makePlotSpec(
            dataStorage = LAYER,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true)
    }

    @Test
    fun `ggplot(data) + geom_point(mapping)`() {
        val spec = makePlotSpec(
            dataStorage = PLOT,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true)
    }

    @Test
    fun `ggplot(mapping) + geom_point(data)`() {
        val spec = makePlotSpec(
            dataStorage = LAYER,
            mappingStorage = PLOT
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true)
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
                .assertDateTimeScale(Aes.X, isDateTime = false)
        }
        run {
            val spec = makePlotSpec(
                dataStorage = PLOT,
                mappingStorage = PLOT,
                scales = """{"aesthetic": "x", "discrete": true}"""
            )
            transformToClientPlotConfig(spec)
                .assertScale(Aes.X, isDiscrete = true)
                .assertDateTimeScale(Aes.X, isDateTime = false)
        }
    }

    @Test
    fun `as_discrete annotation is a higher priority`() {
        val spec = """
            {
              "data": $myData,
              "data_meta": {
                "mapping_annotations": [
                  {
                    "aes": "x",
                    "annotation": "as_discrete"
                  }
                ],  
                "series_annotations": [
                    {
                        "column": "date",
                        "type": "datetime"
                    }
                ]
              },
              "mapping": {
                "x": "date", "y": "y"
              },
              "kind": "plot",
              "layers": [
                {
                  "geom": "point"
                }
              ]
            }
        """.trimIndent()

        transformToClientPlotConfig(spec)
            .assertScale(Aes.X, isDiscrete = true)
            .assertDateTimeScale(Aes.X, isDateTime = false)
    }

    @Test
    fun twoLayers() {
        val spec = """
            {
              "kind": "plot",
              "layers": [
                {
                  "geom": "point",
                  "data": { "d1": [946684800000.0] },
                  "mapping": {
                    "x": "d1"
                  },
                  "data_meta": {
                    "series_annotations": [
                        {
                        "column": "d1",
                        "type": "datetime"
                        }
                    ]
                  }
                },
                {
                  "geom": "point",
                  "data": { "d2": [949363200000.0] },                  
                  "mapping": {
                    "x": "d2"
                  }                 
                }
              ]
            }""".trimIndent()

        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true)
    }


    private fun PlotConfigClientSide.assertDateTimeScale(
        aes: Aes<*>,
        isDateTime: Boolean
    ): PlotConfigClientSide {
        val scale = scaleMap[aes]
        if (scale.isContinuous) {
            val breaksGenerator =
                (scale.getBreaksGenerator() as Transforms.BreaksGeneratorForTransformedDomain).breaksGenerator
            assertTrue(breaksGenerator is DateTimeBreaksGen == isDateTime)
        } else {
            assertFalse(isDateTime)
        }
        return this
    }
}