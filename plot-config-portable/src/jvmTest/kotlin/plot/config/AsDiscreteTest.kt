/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.LAYER
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.PLOT
import org.junit.Ignore
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals

class AsDiscreteTest {

    private val data = """
        |{
        |    "x": [0, 2, 5, 8, 9, 12, 16, 20, 40],
        |    "y": [3, 1, 2, 7, 8, 9, 10, 10, 10],
        |    "g": [0, 0, 0, 1, 1, 1, 2, 2, 2],
        |    "cyl": [0, 0, 0, 1, 1, 1, 2, 2, 2],
        |    "d": ["0", "0", "0", "1", "1", "1", "2", "2", "2"]
        |}
    """.trimMargin()

    enum class Storage {
        PLOT,
        LAYER
    }

    private fun makePlotSpec(
        geom: String,
        dataStorage: Storage,
        mappingStorage: Storage
    ): String {
        val data = "\"data\": ${this.data},"
        val mapping = "\"color\": \"g\""
        val annotation = """
            |"data_meta": {
            |    "mapping_annotations": [
            |        {
            |            "aes": "color",
            |            "annotation": "as_discrete"
            |        }
            |    ]
            |},
        """.trimMargin()

        return """
            |{
            |  ${data.takeIf { dataStorage == PLOT } ?: ""}
            |  ${annotation.takeIf { mappingStorage == PLOT } ?: ""}
            |  "mapping": {
            |    ${(mapping + ",").takeIf { mappingStorage == PLOT } ?: ""}
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      ${data.takeIf { dataStorage == LAYER } ?: ""}
            |      ${annotation.takeIf { mappingStorage == LAYER } ?: ""}
            |      "geom": "$geom",
            |      "mapping": {
            |        ${mapping.takeIf { mappingStorage == LAYER } ?: ""}
            |      }
            |    }
            |  ]
            |}
        """.trimMargin()
    }

    @Test
    fun plot_LayerDataMapping_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = LAYER,
            mappingStorage = LAYER
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plotDataMapping_Layer_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = PLOT,
            mappingStorage = PLOT
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plotData_LayerMapping_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = PLOT,
            mappingStorage = LAYER
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plotMapping_LayerData_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = LAYER,
            mappingStorage = PLOT
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plot_LayerDataMapping_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = LAYER,
            mappingStorage = LAYER
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plotDataMapping_Layer_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = PLOT,
            mappingStorage = PLOT
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plotData_LayerMapping_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = PLOT,
            mappingStorage = LAYER
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun plotMapping_LayerData_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = LAYER,
            mappingStorage = PLOT
        )

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun smoothAsDiscreteWithGroupVar() {
        val spec = """
            |{
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "smooth",
            |      "mapping": {
            |        "color": "g",
            |        "group": "g"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "color",
            |            "annotation": "as_discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Ignore
    @Test
    fun smoothAsDiscreteWithStatVar() {
        val spec = """
            |{
            |  "data": $data,
            |  "data_meta": {
            |    "mapping_annotations": [
            |      {
            |        "aes": "color",
            |        "annotation": "as_discrete"
            |      }
            |    ]
            |  },
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "g"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "smooth",
            |      "mapping": {
            |        "color": "g"
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .hasVariable(Stats.GROUP)
            .assertVariable(varName = "g", isDiscrete = false)
    }

    @Test
    fun mergingMappingsAndData() {
        fun buildSpec(
            geom: String,
            fooData: Storage,
            fooMapping: Storage,
            barData: Storage,
            barMapping: Storage
        ): String {
            fun formatSpec(
                values: List<Pair<Storage, String>>,
                targetStorage: Storage,
                format: (String) -> String
            ): String? {
                return values
                    .filter { (thisStorage, _) -> thisStorage == targetStorage }
                    .takeIf { it.isNotEmpty() } // introduce nullability for easier string interpolation
                    ?.let { format(it.joinToString { (_, str) -> str }) }
            }

            val fooDataSpec = """"foo": [0, 0, 0, 1, 1, 1, 2, 2, 2]"""
            val fooMappingSpec = """"color": "foo""""
            val fooAnnotationSpec = """{"aes": "color", "annotation": "as_discrete"}"""

            val barDataSpec = """"bar": [3, 3, 3, 4, 4, 4, 5, 5, 5]"""
            val barMappingSpec = """"fill": "bar""""
            val barAnnotationSpec = """{"aes": "fill", "annotation": "as_discrete"}"""

            fun formatDataSpec(values: List<Pair<Storage, String>>, targetStorage: Storage): String? =
                formatSpec(values, targetStorage) { """"data": {${it}}""" }

            fun formatMappingSpec(values: List<Pair<Storage, String>>, targetStorage: Storage): String? =
                formatSpec(values, targetStorage) { """"mapping": {${it}}""" }

            fun formatAnnotationSpec(values: List<Pair<Storage, String>>, targetStorage: Storage): String? =
                formatSpec(values, targetStorage) { """"data_meta": {"mapping_annotations": [$it]}""" }

            val data = listOf(fooData to fooDataSpec, barData to barDataSpec)
            val mapping = listOf(fooMapping to fooMappingSpec, barMapping to barMappingSpec)
            val annotation = listOf(fooMapping to fooAnnotationSpec, barMapping to barAnnotationSpec)

            return """
            |{
            |  "kind": "plot",
            |  ${formatDataSpec(data, PLOT)?.let { it + "," } ?: ""}
            |  ${formatAnnotationSpec(annotation, PLOT)?.let { it + "," } ?: ""}
            |  ${formatMappingSpec(mapping, PLOT)?.let { it + "," } ?: ""}
            |  "layers": [
            |    {
            |       "geom": "$geom",
            |       ${formatDataSpec(data, LAYER)?.let { it + "," } ?: ""}
            |       ${formatAnnotationSpec(annotation, LAYER)?.let { it + "," } ?: ""}
            |       ${formatMappingSpec(mapping, LAYER)?.let { it + "," } ?: ""}
            |       "delimiter_zxc": "zxc"
            |    }
            |  ]
            |}
        """.trimMargin()
        }

        data class Case(val fooMapping: Storage, val fooData: Storage, val barData: Storage, val barMapping: Storage)

        fun Int.bit(bitNumber: Int) = this.and(1 shl bitNumber) != 0
        fun Boolean.asStorage() = PLOT.takeIf { this == false } ?: LAYER
        fun Int.asCase() = Case(bit(0).asStorage(), bit(1).asStorage(), bit(2).asStorage(), bit(3).asStorage())

        // generate cases
        val cases = (0 until 2.0.pow(4).toInt()).map(Int::asCase).toSet()

        // validate test data - all cases have to be unique to test different combinations. Set will drop same cases.
        assertEquals(2.0.pow(4).toInt(), cases.count())

        cases.forEach { case ->
            buildSpec(
                geom = "point",
                fooData = case.fooData,
                fooMapping = case.fooMapping,
                barData = case.barData,
                barMapping = case.barMapping
            )
                .let(::transformToClientPlotConfig)
                .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true) { case.toString() }
                .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.FILL, isDiscrete = true) { case.toString() }
                .assertVariable("color.foo", isDiscrete = true) { case.toString() }
                .assertVariable("fill.bar", isDiscrete = true) { case.toString() }

        }
    }

    @Test
    fun groupingWithDiscreteVariable() {
        val spec = """
            |{
            |  "ggtitle": {"text": "ggplot + geom_line(data, color=d)"}, 
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "data": $data,
            |      "geom": "line",
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "color": "d"
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("d", isDiscrete = true)
    }

    @Test
    fun groupingWithAsDiscrete() {
        val spec = """
            |{
            |  "ggtitle": {"text": "ggplot + geom_line(data, color=as_discrete(g))"}, 
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "data": $data,
            |      "geom": "line",
            |      "mapping": {
            |        "x": "x",
            |        "y": "y",
            |        "color": "g"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "color",
            |            "annotation": "as_discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()
        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.g", isDiscrete = true)
    }

    @Test
    fun `color='cyl', fill=as_discrete('cyl')`() {
        val spec = """
            |{
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "y": "y"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "color": "cyl",
            |        "fill": "cyl"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "fill",
            |            "annotation": "as_discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.FILL, isDiscrete = true)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = false)
            .assertVariable("fill.cyl", isDiscrete = true)
            .assertVariable("cyl", isDiscrete = false)
    }

    @Test
    fun `ggplot(color='cyl') + geom_point(color=as_discrete('cyl'))`() {
        val spec = """
            |{
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "cyl"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "color": "cyl"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "color",
            |            "annotation": "as_discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.cyl", isDiscrete = true)
    }

    val cyl123 = "\"cyl\": [1, 2, 3]"
    val cyl456 = "\"cyl\": [4, 5, 6]"

    @Test
    fun `mapping overriding ggplot(cyl123, color=as_discrete('cyl')) + geom_point()`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = true,
            layerData = null,
            layerMapping = false,
            layerAnnotation = false
        ).let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true) // as_discrete in plot
            .assertVariable("color.cyl", isDiscrete = true) // no overriding in layer
    }

    @Test
    fun `mapping overriding ggplot(cyl123, color=as_discrete('cyl')) + geom_point(color=as_discrete('cyl'))`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = true,
            layerData = null,
            layerMapping = true,
            layerAnnotation = true
        ).let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true) // as_discrete in plot
            .assertVariable("color.cyl", isDiscrete = true) // as_discrete in layer
    }

    @Test
    fun `mapping overriding ggplot(cyl123) + geom_point(color=as_discrete('cyl'))`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = false,
            plotAnnotation = false,
            layerData = null,
            layerMapping = true,
            layerAnnotation = true
        ).let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true) // as_discrete in layer
            .assertVariable("color.cyl", isDiscrete = true) // as_discrete in layer
    }

    @Test
    fun `mapping overriding ggplot(cyl123, color=as_discrete('cyl')) + geom_point(color='cyl')`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = true,
            layerData = null,
            layerMapping = true,
            layerAnnotation = false
        ).let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true) // as_discrete in plot
            .assertVariable("cyl", isDiscrete = false) // as is (numeric, overrided by layer)
    }

    @Test
    fun `overriding ggplot(cyl123, color=cyl) + geom_point(cyl456, color=cyl)`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = false,
            layerData = cyl456,
            layerMapping = true,
            layerAnnotation = false
        )
            .let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = false)
            .assertVariable("cyl", isDiscrete = false)
            .assertValue("cyl", listOf(4.0, 5.0, 6.0))
    }

    @Test
    fun `overriding ggplot(cyl123, color=as_discrete(cyl)) + geom_point(cyl456)`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = true,
            layerData = cyl456,
            layerMapping = false,
            layerAnnotation = false
        )
            .let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.cyl", isDiscrete = true)
            .assertValue("color.cyl", listOf(4.0, 5.0, 6.0))
    }

    @Test
    fun `overriding ggplot(cyl123) + geom_point(cyl456, color=as_discrete(cyl))`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = false,
            plotAnnotation = false,
            layerData = cyl456,
            layerMapping = true,
            layerAnnotation = true
        )
            .let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("color.cyl", isDiscrete = true)
            .assertValue("color.cyl", listOf(4.0, 5.0, 6.0))
    }

    @Test
    fun `overriding ggplot(cyl123, color=as_discrete(cyl)) + geom_point(cyl456, color=cyl)`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = true,
            layerData = cyl456,
            layerMapping = true,
            layerAnnotation = false
        )
            .let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("cyl", isDiscrete = false)
            .assertValue("cyl", listOf(4.0, 5.0, 6.0))
    }

    @Test
    fun `overriding ggplot(cyl123, color=as_discrete(cyl)) + geom_point(color=cyl)`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = true,
            layerData = null,
            layerMapping = true,
            layerAnnotation = false
        )
            .let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true)
            .assertVariable("cyl", isDiscrete = false)
            .assertValue("cyl", listOf(1.0, 2.0, 3.0))
    }

    @Test
    fun `overriding ggplot(cyl123, color=cyl) + geom_point(color=cyl)`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = false,
            layerData = null,
            layerMapping = true,
            layerAnnotation = false
        )
            .let(::transformToClientPlotConfig)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = false)
            .assertVariable("cyl", isDiscrete = false)
            .assertValue("cyl", listOf(1.0, 2.0, 3.0))
    }

    @Test
    fun `label ggplot() + geom_point(color=as_discrete('cyl', label='clndr'))`() {
        val spec = """
            |{
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "cyl"
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "color": "cyl"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "color",
            |            "annotation": "as_discrete",
            |            "parameters": {
            |               "label": "clndr"
            |            }
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true, name = "clndr")
            .assertVariable("color.cyl", isDiscrete = true)
    }

    @Test
    fun `label ggplot(color=as_discrete('cyl', label='ndr')) + geom_point(color=as_discrete('cyl', label='clndr'))`() {
        val spec = """
            |{
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "cyl"
            |  },
            |  "data_meta": {
            |    "mapping_annotations": [
            |      {
            |        "aes": "color",
            |        "annotation": "as_discrete",
            |        "parameters": {
            |           "label": "ndr"
            |        }
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "color": "cyl"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "color",
            |            "annotation": "as_discrete",
            |            "parameters": {
            |               "label": "clndr"
            |            }
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true, name = "clndr")
            .assertVariable("color.cyl", isDiscrete = true)
    }


    @Test
    fun `label ggplot(color=as_discrete('cyl', label='ndr')) + geom_point(color=as_discrete('cyl'))`() {
        val spec = """
            |{
            |  "data": $data,
            |  "mapping": {
            |    "x": "x",
            |    "y": "y",
            |    "color": "cyl"
            |  },
            |  "data_meta": {
            |    "mapping_annotations": [
            |      {
            |        "aes": "color",
            |        "annotation": "as_discrete",
            |        "parameters": {
            |           "label": "ndr"
            |        }
            |      }
            |    ]
            |  },
            |  "kind": "plot",
            |  "layers": [
            |    {
            |      "geom": "point",
            |      "mapping": {
            |        "color": "cyl"
            |      },
            |      "data_meta": {
            |        "mapping_annotations": [
            |          {
            |            "aes": "color",
            |            "annotation": "as_discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        transformToClientPlotConfig(spec)
            .assertScale(org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, isDiscrete = true, name = "ndr")
            .assertVariable("color.cyl", isDiscrete = true)
    }
}

@Suppress("ComplexRedundantLet")
private fun buildSpecWithOverriding(
    geom: String,
    plotData: String?,
    plotMapping: Boolean,
    plotAnnotation: Boolean,
    layerData: String?,
    layerMapping: Boolean,
    layerAnnotation: Boolean
): String {
    fun formatSpec(
        values: List<Pair<AsDiscreteTest.Storage, String>>,
        targetStorage: AsDiscreteTest.Storage,
        format: String
    ): String? {
        return values
            .filter { (thisStorage, _) -> thisStorage == targetStorage }
            .takeIf { it.isNotEmpty() } // introduce nullability for easier string interpolation
            ?.let { it.joinToString { (_, str) -> str } }
            ?.let { format.replace("%s", it) }
    }

    val data = listOfNotNull(
        plotData?.let { PLOT to it },
        layerData?.let { LAYER to layerData }
    )

    val cylMappingSpec = """"color": "cyl""""

    val mapping = listOfNotNull(
        (PLOT to cylMappingSpec).takeIf { plotMapping },
        (LAYER to cylMappingSpec).takeIf { layerMapping }
    )

    val cylAnnotationSpec = """{"aes": "color", "annotation": "as_discrete"}"""
    val annotation = listOfNotNull(
        (PLOT to cylAnnotationSpec).takeIf { plotAnnotation },
        (LAYER to cylAnnotationSpec).takeIf { layerAnnotation }
    )

    return """
            |{
            |  "kind": "plot",
            |  ${"\"data\": {%s},".let { formatSpec(data, PLOT, it) } ?: ""}
            |  ${"\"data_meta\": {\"mapping_annotations\": [%s]},".let { formatSpec(annotation, PLOT, it) } ?: ""}
            |  ${"\"mapping\": {%s},".let { formatSpec(mapping, PLOT, it) } ?: ""}
            |  "layers": [
            |    {
            |       "geom": "$geom",
            |       ${"\"data\": {%s},".let { formatSpec(data, LAYER, it) } ?: ""}
            |       ${"\"data_meta\": {\"mapping_annotations\": [%s]},".let { formatSpec(annotation, LAYER, it) } ?: ""}
            |       ${"\"mapping\": {%s},".let { formatSpec(mapping, LAYER, it) } ?: ""}
            |       "delimiter_zxc": "zxc"
            |    }
            |  ]
            |}
        """.trimMargin()
}
