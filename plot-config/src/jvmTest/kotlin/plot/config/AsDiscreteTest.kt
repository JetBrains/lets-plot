/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.DataFrameUtil.variables
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.LAYER
import jetbrains.datalore.plot.config.AsDiscreteTest.Storage.PLOT
import jetbrains.datalore.plot.config.DataMetaUtil.toDiscrete
import jetbrains.datalore.plot.config.PlotConfig.Companion.getErrorMessage
import jetbrains.datalore.plot.config.PlotConfig.Companion.isFailure
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals
import kotlin.test.fail

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
            |    "mapping_annotation": [
            |        {
            |            "aes": "color",
            |            "annotation": "discrete"
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

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plotDataMapping_Layer_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = PLOT,
            mappingStorage = PLOT
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plotData_LayerMapping_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = PLOT,
            mappingStorage = LAYER
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plotMapping_LayerData_Geom() {
        val spec = makePlotSpec(
            geom = "point",
            dataStorage = LAYER,
            mappingStorage = PLOT
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plot_LayerDataMapping_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = LAYER,
            mappingStorage = LAYER
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plotDataMapping_Layer_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = PLOT,
            mappingStorage = PLOT
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plotData_LayerMapping_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = PLOT,
            mappingStorage = LAYER
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
    }

    @Test
    fun plotMapping_LayerData_Stat() {
        val spec = makePlotSpec(
            geom = "smooth",
            dataStorage = LAYER,
            mappingStorage = PLOT
        )

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
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
            |        "mapping_annotation": [
            |          {
            |            "aes": "color",
            |            "annotation": "discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
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
            val fooAnnotationSpec = """{"aes": "color", "annotation": "discrete"}"""

            val barDataSpec = """"bar": [3, 3, 3, 4, 4, 4, 5, 5, 5]"""
            val barMappingSpec = """"fill": "bar""""
            val barAnnotationSpec = """{"aes": "fill", "annotation": "discrete"}"""

            fun formatDataSpec(values: List<Pair<Storage, String>>, targetStorage: Storage): String? =
                formatSpec(values, targetStorage) { """"data": {${it}}""" }

            fun formatMappingSpec(values: List<Pair<Storage, String>>, targetStorage: Storage): String? =
                formatSpec(values, targetStorage) { """"mapping": {${it}}""" }

            fun formatAnnotationSpec(values: List<Pair<Storage, String>>, targetStorage: Storage): String? =
                formatSpec(values, targetStorage) { """"data_meta": {"mapping_annotation": [$it]}""" }

            val data = listOf(fooData to fooDataSpec, barData to barDataSpec)
            val mapping = listOf(fooMapping to fooMappingSpec, barMapping to barMappingSpec)
            val annotation = listOf(fooMapping to fooAnnotationSpec, barMapping to barAnnotationSpec)

            return """
            |{
            |  "kind": "plot",
            |  ${formatDataSpec(data, PLOT)?.let { it + "," } ?: ""}
            |  "splitter_qwe": "qwe",
            |  ${formatAnnotationSpec(annotation, PLOT)?.let { it + "," } ?: ""}
            |  "delimiter_asd": "asd",
            |  ${formatMappingSpec(mapping, PLOT)?.let { it + "," } ?: ""}
            |  "delimiter_zxc": "zxc",
            |  "layers": [
            |    {
            |       "geom": "$geom",
            |       ${formatDataSpec(data, LAYER)?.let { it + "," } ?: ""}
            |       "splitter_qwe": "qwe",
            |       ${formatAnnotationSpec(annotation, LAYER)?.let { it + "," } ?: ""}
            |       "delimiter_asd": "asd",
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
                .let(::toClientPlotConfig)
                .assertDiscreteMapping(Aes.COLOR, toDiscrete("foo")) { case.toString() }
                .assertDiscreteMapping(Aes.FILL, toDiscrete("bar")) { case.toString() }
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

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, "d")
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
            |        "mapping_annotation": [
            |          {
            |            "aes": "color",
            |            "annotation": "discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()
        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("g"))
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
            |        "mapping_annotation": [
            |          {
            |            "aes": "fill",
            |            "annotation": "discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.FILL, toDiscrete("cyl"))
            .assertContinuousMapping(Aes.COLOR, "cyl")

    }

    @Test
    fun `ggplot(color=as_discrete('cyl')) + geom_point(color='cyl')`() {
        val spec = """
            |{
            |  "data": $data,
            |  "data_meta": {
            |    "mapping_annotation": [
            |      {
            |        "aes": "color",
            |        "annotation": "discrete"
            |      }
            |    ]
            |  },
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
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("cyl"))
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
            |        "mapping_annotation": [
            |          {
            |            "aes": "color",
            |            "annotation": "discrete"
            |          }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        toClientPlotConfig(spec)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("cyl"))
    }

    val cyl123 = "\"cyl\": [1, 2, 3]"
    val cyl456 = "\"cyl\": [4, 5, 6]"

    @Test
    fun `overriding ggplot(cel123, color=cyl) + geom_point(cyl456, color=cyl)`() {
        buildSpecWithOverriding(
            geom = "point",
            plotData = cyl123,
            plotMapping = true,
            plotAnnotation = false,
            layerData = cyl456,
            layerMapping = true,
            layerAnnotation = false
        )
            .let(::toClientPlotConfig)
            .assertContinuousMapping(Aes.COLOR, "cyl")
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
            .let(::toClientPlotConfig)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("cyl"))
            .assertValue(toDiscrete("cyl"), listOf(4.0, 5.0, 6.0))
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
            .let(::toClientPlotConfig)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("cyl"))
            .assertValue(toDiscrete("cyl"), listOf(4.0, 5.0, 6.0))
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
            .let(::toClientPlotConfig)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("cyl"))
            .assertValue(toDiscrete("cyl"), listOf(4.0, 5.0, 6.0))
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
            .let(::toClientPlotConfig)
            .assertDiscreteMapping(Aes.COLOR, toDiscrete("cyl"))
            .assertValue(toDiscrete("cyl"), listOf(1.0, 2.0, 3.0))
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
            .let(::toClientPlotConfig)
            .assertContinuousMapping(Aes.COLOR, "cyl")
            .assertValue("cyl", listOf(1.0, 2.0, 3.0))
    }
}

private fun PlotConfigClientSide.assertValue(variable: String, values: List<*>): PlotConfigClientSide {
    val data = layerConfigs.single().combinedData
    assertEquals(values, data.get(variables(data)[variable]!!))
    return this
}

private fun PlotConfigClientSide.assertDiscreteMapping(
    aes: Aes<*>,
    varName: String,
    msg: () -> String = { "" }
): PlotConfigClientSide {
    assertAes(aes, varName, isContinuous = false, msg = msg)
    return this
}

private fun PlotConfigClientSide.assertContinuousMapping(
    aes: Aes<*>,
    varName: String,
    msg: () -> String = { "" }
): PlotConfigClientSide {
    assertAes(aes, varName, isContinuous = true, msg = msg)
    return this
}

private fun PlotConfigClientSide.assertAes(
    aes: Aes<*>,
    varName: String,
    isContinuous: Boolean,
    msg: () -> String = { "" }
): PlotConfigClientSide {
    val layer = layerConfigs.single()
    val binding = layer.varBindings.firstOrNull { it.aes == aes } ?: fail("$aes not found. ${msg()}")
    assertEquals(isContinuous, binding.scale!!.isContinuous)
    assertEquals(varName, binding.variable.name, varName)

    val dfVar = DataFrameUtil.findVariableOrFail(layer.combinedData, varName)
    assertEquals(isContinuous, layer.combinedData.isNumeric(dfVar))
    return this
}

private fun toClientPlotConfig(spec: String): PlotConfigClientSide {
    return parsePlotSpec(spec)
        .let(ServerSideTestUtil::serverTransformWithoutEncoding)
        .also { require(!isFailure(it)) { getErrorMessage(it) } }
        .let(TestUtil::assertClientWontFail)
}


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
            ?.let { format.replace("%s", it)}
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

    val cylAnnotationSpec = """{"aes": "color", "annotation": "discrete"}"""
    val annotation = listOfNotNull(
        (PLOT to cylAnnotationSpec).takeIf { plotAnnotation },
        (LAYER to cylAnnotationSpec).takeIf { layerAnnotation }
    )

    return """
            |{
            |  "kind": "plot",
            |  ${"\"data\": {%s},".let { formatSpec(data, PLOT, it) } ?: ""}
            |  "splitter_qwe": "qwe",
            |  ${"\"data_meta\": {\"mapping_annotation\": [%s]},".let { formatSpec(annotation, PLOT, it) } ?: ""}
            |  "delimiter_asd": "asd",
            |  ${"\"mapping\": {%s},".let { formatSpec(mapping, PLOT, it) } ?: ""}
            |  "delimiter_zxc": "zxc",
            |  "layers": [
            |    {
            |       "geom": "$geom",
            |       ${"\"data\": {%s},".let { formatSpec(data, LAYER, it) } ?: ""}
            |       "splitter_qwe": "qwe",
            |       ${"\"data_meta\": {\"mapping_annotation\": [%s]},".let { formatSpec(annotation, LAYER, it) } ?: ""}
            |       "delimiter_asd": "asd",
            |       ${"\"mapping\": {%s},".let { formatSpec(mapping, LAYER, it) } ?: ""}
            |       "delimiter_zxc": "zxc"
            |    }
            |  ]
            |}
        """.trimMargin()
}
