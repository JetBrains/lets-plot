/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Color.Companion.BLUE
import jetbrains.datalore.base.values.Color.Companion.GREEN
import jetbrains.datalore.base.values.Color.Companion.RED
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.render.linetype.NamedLineType.*
import jetbrains.datalore.plot.base.render.point.NamedShape.STICK_SQUARE_TRIANGLE_UP
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.scale.MapperProvider
import jetbrains.datalore.plot.builder.scale.mapper.LineTypeMapper
import jetbrains.datalore.plot.builder.scale.mapper.ShapeMapper
import jetbrains.datalore.plot.config.Option.Mapping.toOption
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
        val map = mapOf(
                "var" to input
        )
        val data = DataFrameUtil.fromMap(map)

        val inputTransformed = MapperUtil.mapDiscreteDomainValuesToNumbers(input)

        val mapper = mapperProvider.createDiscreteMapper(data, DataFrameUtil.findVariableOrFail(data, "var"))
        for (i in input.indices) {
            assertEquals(expected[i], mapper.apply(inputTransformed[input[i]]))
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

        val mapper = mapperProvider.createContinuousMapper(data, DataFrameUtil.findVariableOrFail(data, "var"),
                Double.NaN, Double.NaN, Transforms.IDENTITY)
        for (v in input) {
            assertEquals(v, mapper.apply(v))
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
    fun colorHueMapperForDiscreteFillColorScale() {
        fun checkDiscreteScale(aes: Aes<Color>) {

            val scaleSpec = mapOf(
                "discrete" to true,
                "aesthetic" to toOption(aes)
            )

            val dataFrame = DataFrameUtil.fromMap(mapOf("a" to listOf(1.0, 2.0, 3.0, 4.0)))

            val scaleMapper = ScaleConfig<Color>(scaleSpec)
                .createScaleProvider()
                .createScale(dataFrame, dataFrame.variables().first { it.name == "a" })
                .mapper

            assertEquals(Color(160,229,114), scaleMapper(1.0))
            assertEquals(Color(114,206,229), scaleMapper(2.0))
            assertEquals(Color(206,114,229), scaleMapper(3.0))
        }

        checkDiscreteScale(Aes.FILL)
        checkDiscreteScale(Aes.COLOR)
    }
}