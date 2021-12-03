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
            .assertDateTimeScale(Aes.X, isDateTime = true, name = "date")
    }

    @Test
    fun `ggplot + geom_point(data, mapping)`() {
        val spec = makePlotSpec(
            dataStorage = LAYER,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, name = "date")
    }

    @Test
    fun `ggplot(data) + geom_point(mapping)`() {
        val spec = makePlotSpec(
            dataStorage = PLOT,
            mappingStorage = LAYER
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, name = "date")
    }

    @Test
    fun `ggplot(mapping) + geom_point(data)`() {
        val spec = makePlotSpec(
            dataStorage = LAYER,
            mappingStorage = PLOT
        )
        transformToClientPlotConfig(spec)
            .assertDateTimeScale(Aes.X, isDateTime = true, name = "date")
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
        }
        run {
            val spec = makePlotSpec(
                dataStorage = PLOT,
                mappingStorage = PLOT,
                scales = """{"aesthetic": "x", "discrete": true}"""
            )
            transformToClientPlotConfig(spec)
                .assertDateTimeScale(Aes.X, isDiscrete = true, isDateTime = false, name = "date")
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
            .assertDateTimeScale(Aes.X, isDateTime = true, name = "x")
    }


    private fun PlotConfigClientSide.assertDateTimeScale(
        aes: Aes<*>,
        isDateTime: Boolean,
        isDiscrete: Boolean = !isDateTime,
        name: String? = null
    ): PlotConfigClientSide {
        assertScale(Aes.X, isDiscrete, name)

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