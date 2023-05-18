/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.BLUE
import jetbrains.datalore.base.values.Color.Companion.GREEN
import jetbrains.datalore.base.values.Color.Companion.RED
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.render.linetype.NamedLineType.*
import jetbrains.datalore.plot.base.render.point.NamedShape.STICK_SQUARE_TRIANGLE_UP
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.mapper.LineTypeMapper
import jetbrains.datalore.plot.builder.scale.mapper.ShapeMapper
import jetbrains.datalore.plot.common.color.ColorPalette
import jetbrains.datalore.plot.config.Option.Mapping.toOption
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals

class ScaleConfigTest {

    private fun checkRGBMapping(aes: Aes<*>, input: List<*>) {
        @Suppress("UNCHECKED_CAST")
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(aes as Aes<Any>, Color.TRANSPARENT)
        val expected = listOf(RED, GREEN, BLUE)
        checkMappingDiscrete(expected, input, mapperProvider)
    }

    private fun checkMappingDiscrete(expected: List<*>, input: List<*>, mapperProvider: MapperProvider<*>) {
        val transform = DiscreteTransform(input.filterNotNull(), emptyList())
        val inputTransformed = transform.apply(input)

        val mapper = mapperProvider.createDiscreteMapper(transform)
        for (i in input.indices) {
            assertEquals(expected[i], mapper(inputTransformed[i]))
        }
    }

    private fun checkIdentityMappingNumeric(aes: Aes<Double>, input: List<Double>) {
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(aes, Double.NaN)
        checkMappingDiscrete(input, input, mapperProvider)
        checkIdentityMappingContinuous(input, mapperProvider)
    }

    private fun checkIdentityMappingContinuous(input: List<Double>, mapperProvider: MapperProvider<Double>) {
        val map = mapOf(
            "var" to input
        )
        val data = DataFrameUtil.fromMap(map)
        val datavar = DataFrameUtil.findVariableOrFail(data, "var")
        val mapper = mapperProvider.createContinuousMapper(
            data.range(datavar)!!,
            Transforms.IDENTITY
        )
        for (v in input) {
            assertEquals(v, mapper(v))
        }
    }


    @Test
    fun colorIdentityMapper() {
        val inputs = listOf(
            listOf("red", "green", "blue"),
            listOf("#ff0000", "#00ff00", "#0000ff"),
            listOf("rgb(255,0,0)", "rgb(0,255,0)", "rgb(0,0,255)"),
            listOf("rgba(255,0,0,1.0)", "rgba(0,255,0,1.0)", "rgba(0,0,255,1.0)"),
            listOf(0xff0000, 0x00ff00, 0x0000ff),
            listOf(0xff0000.toDouble(), 0x00ff00.toDouble(), 0x0000ff.toDouble())
        )

        for (aes in Aes.values()) {
            if (aes.isColor) {
                for (input in inputs) {
                    checkRGBMapping(aes, input)
                }
            }
        }
    }

    @Test
    fun shapeIdentityMapper() {
        val input = listOf(14.0)
        val expected = listOf(STICK_SQUARE_TRIANGLE_UP)
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(Aes.SHAPE, ShapeMapper.NA_VALUE)
        checkMappingDiscrete(expected, input, mapperProvider)
    }

    @Test
    fun linetypeIdentityMapper() {
        @Suppress("SpellCheckingInspection")
        val input = listOf(2.0, "longdash", 5.0, "twodash")
        val expected = listOf(DASHED, LONGDASH, LONGDASH, TWODASH)
        val mapperProvider = ScaleConfig.createIdentityMapperProvider(Aes.LINETYPE, LineTypeMapper.NA_VALUE)
        checkMappingDiscrete(expected, input, mapperProvider)
    }

    @Test
    fun numericIdentityMapper() {
        val input = listOf(2.0, 3.0, 5.0, 7.0, Double.NaN)
        for (aes in Aes.values()) {
            if (aes.isNumeric) {
                @Suppress("UNCHECKED_CAST")
                checkIdentityMappingNumeric(aes as Aes<Double>, input)
            }
        }
    }

    @Test
    fun colorBrewerMapperForDiscreteColorScale() {
        fun checkDiscreteScale(aes: Aes<Color>) {

            val scaleSpec = mapOf(
                "discrete" to true,
                "aesthetic" to toOption(aes)
            )

            val scaleMapper = ScaleConfig<Color>(aes, scaleSpec)
//                .createScaleProvider()
//                .mapperProvider
                .createMapperProvider()
                .createDiscreteMapper(DiscreteTransform(listOf(1.0, 2.0, 3.0, 4.0), emptyList()))

            val expected = ColorPalette.Qualitative.Set2.getColors(4).map { Colors.parseColor(it) }
            assertEquals(expected[0], scaleMapper(0.0))
            assertEquals(expected[1], scaleMapper(1.0))
            assertEquals(expected[2], scaleMapper(2.0))
            assertEquals(expected[3], scaleMapper(3.0))
        }

//        checkDiscreteScale(Aes.FILL)
        checkDiscreteScale(Aes.COLOR)
    }

    @Test
    fun log10WithNull() {
        val spec = """
            
            {
              "data": {
                "x": [0, 1, 2, 3 ],
                "y": [0, 1, 4, 9],
                "v": [null, 0, 1, 81]
              },
              "mapping": {
                "x": "x",
                "y": "y"
              },
              "kind": "plot",
              "scales": [
                {
                  "aesthetic": "color",
                  "trans": "log10",
                  "scale_mapper_kind": "color_gradient"
                }
              ],
              "layers": [
                {
                  "geom": "point",
                  "mapping": {
                    "color": "v"
                  }
                }
              ]
            }
        """.trimIndent()

        val opts = parsePlotSpec(spec)
        val config = PlotConfigClientSide.create(opts) {}
        PlotConfigClientSideUtil.createPlotAssembler(config)
    }

    @Test
    fun `choosing a scale name`() {
        fun makePlotSpec(scaleParams: String? = null, asDiscreteParams: String? = null) = """
            {
              "data": { "x": [0, 1], "v": [0, 1] },
              "mapping": { "x": "x", "color": "v" },
              "kind": "plot",
              "scales": [ ${scaleParams.takeIf { it != null } ?: ""} ],
              "layers": [
                {
                  "geom": "point",
                  "data_meta": {
                    "mapping_annotations": [
                      {
                        "aes": "color",
                        "annotation": "as_discrete",
                        "parameters": ${asDiscreteParams.takeIf { it != null }}          
                      }
                    ]
                  }
                }
              ]
            }""".trimIndent()

        val scaleNameParam = "{ 'aesthetic': 'color', 'name': 'name from scale' }"
        val asDiscreteLabelParam = "{ 'label': 'label from as_discrete' }"

        // use variable name by default
        transformToClientPlotConfig(makePlotSpec())
            .assertScale(Aes.COLOR, isDiscrete = true, name = "color.v")

        // scale(name)
        transformToClientPlotConfig(makePlotSpec(scaleParams = scaleNameParam))
            .assertScale(Aes.COLOR, isDiscrete = true, name = "name from scale")

        // as_discrete(label)
        transformToClientPlotConfig(makePlotSpec(asDiscreteParams = asDiscreteLabelParam))
            .assertScale(Aes.COLOR, isDiscrete = true, name = "label from as_discrete")

        // scale(name) is a higher priority than as_discrete(label)
        transformToClientPlotConfig(makePlotSpec(scaleParams = scaleNameParam, asDiscreteParams = asDiscreteLabelParam))
            .assertScale(Aes.COLOR, isDiscrete = true, name = "name from scale")
    }
}